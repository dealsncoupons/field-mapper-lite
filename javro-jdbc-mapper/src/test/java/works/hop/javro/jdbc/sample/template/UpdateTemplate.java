package works.hop.javro.jdbc.sample.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.javro.jdbc.EntityInfo;
import works.hop.javro.jdbc.sample.Unreflect;
import works.hop.javro.jdbc.sample.EntityMetadata;
import works.hop.javro.jdbc.sample.EntitySourceFactory;
import works.hop.javro.jdbc.sample.FieldInfo;
import works.hop.javro.jdbc.template.ConnectionProvider;
import works.hop.javro.jdbc.template.QueryExecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class UpdateTemplate {

    static final Logger log = LoggerFactory.getLogger(UpdateTemplate.class);

    public static String prepareQuery(EntityInfo entityInfo) {
        List<String> fields = dataColumns(entityInfo.getFields());
        List<String> idFields = idColumns(entityInfo.getFields());
        StringBuilder builder = new StringBuilder();
        builder.append("update ").append(entityInfo.getTableName()).append(" set ");
        int fieldIndex = 0;
        for (String columnName : fields) {
            builder.append(columnName).append(" = ?");
            fieldIndex++;
            if (fieldIndex < fields.size()) {
                builder.append(", ");
            }
        }
        builder.append(" where ");
        int idColumnIndex = 0;
        for (String idColumn : idFields) {
            builder.append(idColumn).append(" = ?::uuid");
            idColumnIndex += 1;
            if (idColumnIndex < idFields.size()) {
                builder.append(" and ");
            }
        }
        return builder.toString();
    }

    private static List<String> dataColumns(List<FieldInfo> entityInfo) {
        return entityInfo.stream()
                .filter(field -> !field.isRelational)
                .filter(field -> !field.isId)
                .map(field -> field.columnName).collect(Collectors.toList());
    }

    private static List<String> idColumns(List<FieldInfo> entityInfo) {
        return entityInfo.stream()
                .filter(field -> field.isId)
                .map(field -> field.columnName).collect(Collectors.toList());
    }

    public static <E> void updateOne(E entity) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);
            EntityInfo entityInfo = EntityMetadata.getEntityInfo.apply(entity.getClass());
            updateInTransaction(entity, entityInfo, conn);
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

    private static <E> void updateInTransaction(E entity, EntityInfo entityInfo, Connection connection) {
        Unreflect entitySource = EntitySourceFactory.create(entity);
        for (FieldInfo field : entityInfo.getFields()) {
            EntityInfo joinColumnInfo = EntityMetadata.getEntityInfo.apply(field.type);
            if (field.isCollection) {
                Collection<Unreflect> joinColumnCollection = entitySource.get(field.name);
                if (joinColumnCollection != null) {
                    for (Object collectionValue : joinColumnCollection) {
                        updateInTransaction(collectionValue, joinColumnInfo, connection);
                    }
                }
            } else {
                Unreflect joinColumnValue = entitySource.get(field.name);
                if (joinColumnValue != null) {
                    updateInTransaction(joinColumnValue, joinColumnInfo, connection);
                }
            }
        }
        doUpdateInTransaction(entitySource, entityInfo, connection);
    }

    private static void doUpdateInTransaction(Unreflect entity, EntityInfo entityInfo, Connection connection) {
        List<String> fields = dataColumns(entityInfo.getFields());
        List<String> idFields = idColumns(entityInfo.getFields());
        String query = prepareQuery(entityInfo);

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            AtomicInteger index = new AtomicInteger(1);
            fields.addAll(idFields);
            for (String fieldName : fields) {
                ps.setObject(index.getAndIncrement(), entity.get(fieldName));
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
