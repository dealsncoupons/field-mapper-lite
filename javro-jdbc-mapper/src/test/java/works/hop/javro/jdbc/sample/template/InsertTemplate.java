package works.hop.javro.jdbc.sample.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.javro.jdbc.EntityInfo;
import works.hop.javro.jdbc.sample.Accessible;
import works.hop.javro.jdbc.sample.EntityMetadata;
import works.hop.javro.jdbc.sample.EntitySourceFactory;
import works.hop.javro.jdbc.sample.FieldInfo;
import works.hop.javro.jdbc.template.ConnectionProvider;
import works.hop.javro.jdbc.template.QueryExecutor;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InsertTemplate {

    static final Logger log = LoggerFactory.getLogger(InsertTemplate.class);

    private static String prepareQuery(EntityInfo entityInfo) {
        List<String> fields = columnNames(entityInfo.getFields());
        String fieldNames = String.join(", ", fields);
        String valueFields = Arrays.stream(fieldNames.split(", ")).map(f -> "?").collect(Collectors.joining(","));
        return String.join(" ", "insert into", entityInfo.getTableName(), "(", fieldNames, ") values (", valueFields, ") on conflict do nothing returning id::uuid");
    }

    private static List<String> columnNames(List<FieldInfo> entityInfo) {
        return entityInfo.stream()
                .filter(field -> !field.isRelational)
                .filter(field -> !field.isId)
                .map(field -> field.columnName).collect(Collectors.toList());
    }

    public static <E> E insertOne(E entity) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);
            EntityInfo entityInfo = EntityMetadata.getEntityInfo.apply(entity.getClass());
            E insertResult = insertInTransaction(entity, entityInfo, conn);
            conn.commit();
            return insertResult;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception sqle) {
                    log.error("Failed to rollback transaction", sqle);
                }
            }
            throw new RuntimeException("Problem executing insert query", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception sqle) {
                    log.error("Failed to commit transaction", sqle);
                }
            }
        }
    }

    private static <E> E insertInTransaction(E entity, EntityInfo entityInfo, Connection connection) {
//        for (FieldInfo field : entityInfo) {
//            if (field.isCollection) {
//                Collection<Object> joinColumnCollection = entitySource.get(field.name);
//                if (joinColumnCollection != null) {
//                    Collection<Object> savedJoinCollection = newCollectionInstance(field.type);
//                    for (Object collectionValue : joinColumnCollection) {
//                        Object savedCollectionValue = insertInTransaction(collectionValue, connection);
//                        savedJoinCollection.add(savedCollectionValue);
//                    }
//                    set(field, entity, savedJoinCollection);
//                }
//            } else {
//                Object joinColumnValue = entitySource.get(field.name);
//                if (joinColumnValue != null) {
//                    Object savedJoinColumnValue = insertInTransaction(joinColumnValue, connection);
//                    if (savedJoinColumnValue != null) {
//                        set(field, entity, savedJoinColumnValue);
//                    }
//                }
//            }
//        }
        return doInsertInTransaction(entity, entityInfo, connection);
    }

    private static <E> E doInsertInTransaction(E entity, EntityInfo entityInfo, Connection connection) {
        String query = prepareQuery(entityInfo);
        Accessible entitySource = EntitySourceFactory.create(entity);

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            List<String> fields = columnNames(entityInfo.getFields());
            AtomicInteger index = new AtomicInteger(1);
            for (String fieldName : fields) {
                Object columnValue = entitySource.get(fieldName);
                ps.setObject(index.getAndIncrement(), columnValue);
            }
            int rowsAffected = ps.executeUpdate();
            log.info("{} row(s) affected after insert operation", rowsAffected);

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    UUID uuid = UUID.fromString(keys.getString(1));
                    entitySource.set("id", uuid);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                log.warn("Could not retrieve generated id value", e);
            }

            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Problem executing fetch query", e);
        }
    }

    public static Object execute(String queryString, Object[] args) {
        new QueryExecutor() {
        }.executeUpdate(queryString, args);
        return 1;
    }
}
