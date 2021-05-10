package works.hop.javro.jdbc.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static works.hop.javro.jdbc.reflect.ReflectionUtil.*;

public class DeleteTemplate {

    static final Logger log = LoggerFactory.getLogger(DeleteTemplate.class);

    public static String prepareQuery(String tableName, Map<String, Object> fields) {
        StringBuilder builder = new StringBuilder();
        builder.append("delete from ").append(tableName).append(" where ");
        int fieldIndex = 0;
        for (String columnName : fields.keySet()) {
            builder.append(columnName).append(" = ?");
            fieldIndex++;
            if (fieldIndex < fields.size()) {
                builder.append(" and ");
            }
        }
        return builder.toString();
    }

    public static <E> void deleteOne(E entity) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);
            deleteInTransaction(entity, conn);
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception sqle) {
                    log.error("Failed to rollback transaction", sqle);
                }
            }
            throw new RuntimeException("Problem executing delete query", e);
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

    private static <E> void deleteInTransaction(E entity, Connection connection) {
        doDeleteInTransaction(entity, connection);
        List<Field> joinColumnFields = getJoinColumnFields(entity.getClass());
        if (!joinColumnFields.isEmpty()) {
            for (Field field : joinColumnFields) {
                if (isCollectionField(field)) {
                    List<Object> joinColumnCollection = (List<Object>) getFieldValue(field, entity);
                    if (joinColumnCollection != null) {
                        for (Object collectionValue : joinColumnCollection) {
                            deleteInTransaction(collectionValue, connection);
                        }
                    }
                } else {
                    Object joinColumnValue = getFieldValue(field, entity);
                    if (joinColumnValue != null) {
                        deleteInTransaction(joinColumnValue, connection);
                    }
                }
            }
        }
    }

    private static <E> void doDeleteInTransaction(E entity, Connection connection) {
        AbstractDelete abstractDelete = new AbstractDelete(entity);
        Map<String, Object> idFields = abstractDelete.idFields;
        String query = prepareQuery(getTableName(entity.getClass()), idFields);

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            AtomicInteger index = new AtomicInteger(1);
            for (String fieldName : idFields.keySet()) {
                ps.setObject(index.getAndIncrement(), idFields.get(fieldName));
            }
            int rowsAffected = ps.executeUpdate();
            log.info("{} row(s) affected after delete operation", rowsAffected);
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
