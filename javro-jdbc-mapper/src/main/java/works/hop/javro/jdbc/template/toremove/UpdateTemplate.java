package works.hop.javro.jdbc.template.toremove;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.javro.jdbc.template.AbstractUpdate;
import works.hop.javro.jdbc.template.ConnectionProvider;
import works.hop.javro.jdbc.template.QueryExecutor;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static works.hop.javro.jdbc.reflect.ReflectionUtil.*;

public class UpdateTemplate {

    static final Logger log = LoggerFactory.getLogger(UpdateTemplate.class);

    public static String prepareQuery(String tableName, Map<String, Object> idFields, Map<String, Object> fields) {
        StringBuilder builder = new StringBuilder();
        builder.append("update ").append(tableName).append(" set ");
        int fieldIndex = 0;
        for (String columnName : fields.keySet()) {
            builder.append(columnName).append(" = ?");
            fieldIndex++;
            if (fieldIndex < fields.size()) {
                builder.append(", ");
            }
        }
        builder.append(" where ");
        int idColumnIndex = 0;
        for (String idColumn : idFields.keySet()) {
            builder.append(idColumn).append(" = ?::uuid");
            idColumnIndex += 1;
            if (idColumnIndex < idFields.size()) {
                builder.append(" and ");
            }
        }
        return builder.toString();
    }

    public static <E> void updateOne(E entity) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);
            updateInTransaction(entity, conn);
            conn.commit();
            conn.setAutoCommit(true);
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (Exception sqle) {
                    log.error("Failed to rollback transaction", sqle);
                }
            }
            throw new RuntimeException("Problem executing update query", e);
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

    private static <E> void updateInTransaction(E entity, Connection connection) {
        List<Field> joinColumnFields = getJoinColumnFields(entity.getClass());
        if (!joinColumnFields.isEmpty()) {
            for (Field field : joinColumnFields) {
                if (isCollectionField(field)) {
                    List<Object> joinColumnCollection = (List<Object>) getFieldValue(field, entity);
                    if (joinColumnCollection != null) {
                        for (Object collectionValue : joinColumnCollection) {
                            updateInTransaction(collectionValue, connection);
                        }
                    }
                } else {
                    Object joinColumnValue = getFieldValue(field, entity);
                    if (joinColumnValue != null) {
                        updateInTransaction(joinColumnValue, connection);
                    }
                }
            }
        } else {
            doUpdateInTransaction(entity, connection);
        }
        doUpdateInTransaction(entity, connection);
    }

    private static <E> void doUpdateInTransaction(E entity, Connection connection) {
        AbstractUpdate abstractUpdate = new AbstractUpdate(entity);
        Map<String, Object> idFields = abstractUpdate.idFields;
        Map<String, Object> fields = abstractUpdate.fields;
        String query = prepareQuery(getTableName(entity.getClass()), idFields, fields);

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            AtomicInteger index = new AtomicInteger(1);

            fields.putAll(idFields);
            for (String fieldName : fields.keySet()) {
                ps.setObject(index.getAndIncrement(), fields.get(fieldName));
            }
            int rowsAffected = ps.executeUpdate();
            log.info("{} row(s) affected after update operation", rowsAffected);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Problem executing fetch query", e);
        }
    }

    public static Object executeUpdate(String query, Object[] args) {
        new QueryExecutor() {
        }.executeUpdate(query, args);
        return 1;
    }
}
