package works.hop.javro.jdbc.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static works.hop.javro.jdbc.reflect.ReflectionUtil.*;

public class InsertTemplate {

    static final Logger log = LoggerFactory.getLogger(InsertTemplate.class);

    public static String prepareQuery(String tableName, Map<String, Object> fields) {
        String fieldNames = String.join(", ", fields.keySet());
        String valueFields = Arrays.stream(fieldNames.split(", ")).map(f -> "?").collect(Collectors.joining(","));
        return String.join(" ", "insert into", tableName, "(", fieldNames, ") values (", valueFields, ") on conflict do nothing returning id");
    }

    public static <E> E insertOne(E entity) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);
            E insertResult = insertInTransaction(entity, conn);
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

    private static <E> E insertInTransaction(E entity, Connection connection) {
        List<Field> joinColumnFields = getJoinColumnFields(entity.getClass());
        if (!joinColumnFields.isEmpty()) {
            for (Field field : joinColumnFields) {
                if (isCollectionField(field)) {
                    List<Object> joinColumnCollection = (List<Object>) getFieldValue(field, entity);
                    if (joinColumnCollection != null) {
                        Collection<Object> savedJoinCollection = newCollectionInstance(field.getType());
                        for (Object collectionValue : joinColumnCollection) {
                            Object savedCollectionValue = insertInTransaction(collectionValue, connection);
                            savedJoinCollection.add(savedCollectionValue);
                        }
                        set(field, entity, savedJoinCollection);
                    }
                } else {
                    Object joinColumnValue = getFieldValue(field, entity);
                    if (joinColumnValue != null) {
                        Object savedJoinColumnValue = insertInTransaction(joinColumnValue, connection);
                        if (savedJoinColumnValue != null) {
                            set(field, entity, savedJoinColumnValue);
                        }
                    }
                }
            }
        } else {
            return doInsertInTransaction(entity, connection);
        }
        return doInsertInTransaction(entity, connection);
    }

    private static <E> E doInsertInTransaction(E entity, Connection connection) {
        AbstractInsert abstractInsert = new AbstractInsert(entity);
        Map<String, Object> fields = abstractInsert.fields;

        String query = prepareQuery(getTableName(entity.getClass()), fields);

        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            AtomicInteger index = new AtomicInteger(1);
            for (String fieldName : fields.keySet()) {
                ps.setObject(index.getAndIncrement(), fields.get(fieldName));
            }
            int rowsAffected = ps.executeUpdate();
            log.info("{} row(s) affected after insert operation", rowsAffected);

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    UUID uuid = UUID.fromString(keys.getString(1));
                    setIdValue(entity.getClass(), entity, uuid);
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

    public static Object executeUpdate(String queryString, Object[] args) {
        new QueryExecutor() {
        }.executeUpdate(queryString, args);
        return 1;
    }
}
