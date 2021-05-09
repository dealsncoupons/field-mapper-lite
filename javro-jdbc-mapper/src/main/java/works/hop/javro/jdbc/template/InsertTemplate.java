package works.hop.javro.jdbc.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static works.hop.javro.jdbc.reflect.ReflectionUtil.getTableName;
import static works.hop.javro.jdbc.reflect.ReflectionUtil.setIdValue;

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
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException sqle) {
                    log.error("Failed to rollback transaction", sqle);
                }
            }
            throw new RuntimeException("Problem executing insert query", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException sqle) {
                    log.error("Failed to commit transaction", sqle);
                }
            }
        }
    }

    private static <E> E insertInTransaction(E entity, Connection connection) {
        AbstractInsert abstractInsert = new AbstractInsert(entity);
        Map<String, Object> fields = abstractInsert.fields.entrySet().stream().filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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

            if (!abstractInsert.joinFields.isEmpty()) {
                abstractInsert.joinFields.forEach((s, joinField) -> insertInTransaction(joinField, connection));
            }
            if (!abstractInsert.collectionJoinFields.isEmpty()) {
                abstractInsert.collectionJoinFields.values().forEach(items ->
                        items.forEach(item -> insertInTransaction(item, connection)));
            }
            return entity;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Problem executing fetch query", e);
        }
    }

    public static Object executeUpdate(String queryString, Object[] args) {
        new QueryExecutor(){}.executeUpdate(queryString, args);
        return 1;
    }
}
