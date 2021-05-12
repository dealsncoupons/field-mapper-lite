package works.hop.javro.jdbc.sample.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.javro.jdbc.sample.EntityInfo;
import works.hop.javro.jdbc.sample.Unreflect;
import works.hop.javro.jdbc.sample.EntityMetadata;
import works.hop.javro.jdbc.sample.EntitySourceFactory;
import works.hop.javro.jdbc.sample.FieldInfo;
import works.hop.javro.jdbc.template.ConnectionProvider;
import works.hop.javro.jdbc.template.QueryExecutor;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InsertTemplate {

    static final Logger log = LoggerFactory.getLogger(InsertTemplate.class);

    private static String prepareQuery(EntityInfo entityInfo) {
        List<String> fields = dataColumns(entityInfo.getFields());
        String fieldNames = String.join(", ", fields);
        String valueFields = Arrays.stream(fieldNames.split(", ")).map(f -> "?").collect(Collectors.joining(","));
        return String.join(" ", "insert into", entityInfo.getTableName(), "(", fieldNames, ") values (", valueFields, ") on conflict do nothing returning id::uuid");
    }

    private static List<String> dataColumns(List<FieldInfo> entityInfo) {
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
        Unreflect entitySource = EntitySourceFactory.create(entity);
        for (FieldInfo field : entityInfo.getFields()) {
            if (field.isCollection) {
                EntityInfo joinColumnInfo = EntityMetadata.getEntityInfo.apply(field.type);
                if (joinColumnInfo != null) {
                    List<Unreflect> savedJoinCollection = new ArrayList<>();
                    Collection<Unreflect> targetJoinCollection = entitySource.get(field.name);
                    for (Unreflect collectionValue : targetJoinCollection) {
                        Unreflect savedCollectionValue = insertInTransaction(collectionValue, joinColumnInfo, connection);
                        savedJoinCollection.add(savedCollectionValue);
                    }
                    entitySource.set(field.name, savedJoinCollection);
                }
            } else if (field.isRelational) {
                EntityInfo joinColumnInfo = EntityMetadata.getEntityInfo.apply(field.type);
                Unreflect joinColumnValue = entitySource.get(field.name);
                if (joinColumnValue != null) {
                    Unreflect savedJoinColumnValue = insertInTransaction(joinColumnValue, joinColumnInfo, connection);
                    if (savedJoinColumnValue != null) {
                        entitySource.set(field.name, savedJoinColumnValue);
                    }
                }
            }
        }
        return doInsertInTransaction(entitySource, entityInfo, connection);
    }

    private static <E> E doInsertInTransaction(Unreflect entitySource, EntityInfo entityInfo, Connection connection) {
        String query = prepareQuery(entityInfo);
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            List<String> fields = dataColumns(entityInfo.getFields());
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

            return (E) entitySource;
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
