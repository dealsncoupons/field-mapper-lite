package works.hop.javro.jdbc.sample.template;

import io.vavr.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.javro.jdbc.sample.EntityInfo;
import works.hop.javro.jdbc.sample.EntityMetadata;
import works.hop.javro.jdbc.sample.FieldInfo;
import works.hop.javro.jdbc.sample.Unreflect;
import works.hop.javro.jdbc.template.ConnectionProvider;
import works.hop.javro.jdbc.template.QueryExecutor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class UpdateTemplate {

    static final Logger log = LoggerFactory.getLogger(UpdateTemplate.class);

    public static String prepareUpdateQuery(EntityInfo entityInfo, List<String> columns) {
        List<String> idFields = idColumns(entityInfo.getFields());
        StringBuilder builder = new StringBuilder();
        builder.append("update ").append(entityInfo.getTableName()).append(" set ");
        int fieldIndex = 0;
        for (String columnName : columns) {
            builder.append(columnName).append(" = ?");
            fieldIndex++;
            if (fieldIndex < columns.size()) {
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
                .filter(field -> !field.isId)
                .filter(field -> !field.isCollection)
                .map(field -> field.columnName).collect(Collectors.toList());
    }

    private static List<String> idColumns(List<FieldInfo> entityInfo) {
        return entityInfo.stream()
                .filter(field -> field.isId)
                .map(field -> field.columnName).collect(Collectors.toList());
    }

    public static <E extends Unreflect> void updateOne(E entity) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);
            updateOne(entity, conn);
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

    private static <E extends Unreflect> void updateOne(E entity, Connection conn) {
        EntityInfo entityInfo = EntityMetadata.entityInfoByType.apply(entity.getClass());
        Map<String, Optional<Object>> record = columnsToUpdate(entity, entityInfo);
//        updateInTransaction(entity, entityInfo, record, conn); //Should this operation drop all dependencies and re-insert the new ones (like Spring Data JDBC)?
        doUpdateInTransaction(entityInfo, record, conn);
    }

    private static <E extends Unreflect> Map<String, Optional<Object>> columnsToUpdate(E entitySource, EntityInfo entityInfo) {
        return entityInfo.getFields().stream()
                .filter(field -> !field.isCollection)
                .map(field -> {
                    if (field.isRelational) {
                        String joinColumnPk = entityInfo.getFields().stream()
                                .filter(f -> f.isId)
                                .map(f -> f.columnName)
                                .findFirst().orElse("id");
                        Unreflect joinColumnValue = entitySource.get(field.name);
                        if (joinColumnValue != null) {
                            return new Tuple2<>(field.columnName, Optional.ofNullable(joinColumnValue.get(joinColumnPk)));
                        }
                        return new Tuple2<>(field.columnName, Optional.empty());
                    }
                    return new Tuple2<>(field.columnName, Optional.of(entitySource.get(field.name)));
                })
                .collect(Collectors.toMap(tuple -> tuple._1, tuple -> tuple._2));
    }

    private static <E extends Unreflect> void updateInTransaction(E entitySource, EntityInfo entityInfo, Map<String, Optional<Object>> record, Connection connection) {
        for (FieldInfo field : entityInfo.getFields()) {
            if (field.isRelational) {
                if (field.isCollection) {
                    Collection<Unreflect> joinColumnCollection = entitySource.get(field.name);
                    if (joinColumnCollection != null) {
                        for (Unreflect collectionValue : joinColumnCollection) {
                            updateOne(collectionValue, connection);
                        }
                    }
                } else {
                    Unreflect joinColumnValue = entitySource.get(field.name);
                    if (joinColumnValue != null) {
                        EntityInfo joinColumnInfo = EntityMetadata.entityInfoByType.apply(field.type);
                        updateInTransaction(joinColumnValue, joinColumnInfo, record, connection);
                    }
                }
            }
        }
        doUpdateInTransaction(entityInfo, record, connection);
    }

    private static void doUpdateInTransaction(EntityInfo entityInfo, Map<String, Optional<Object>> record, Connection connection) {
        List<String> fields = dataColumns(entityInfo.getFields());
        String query = prepareUpdateQuery(entityInfo, fields);

        Map<String, Optional<Object>> orderedColumns = new LinkedHashMap<>();
        fields.forEach(field -> orderedColumns.put(field, record.remove(field)));
        orderedColumns.putAll(record);

        try (PreparedStatement ps = connection.prepareStatement(query)) {
            AtomicInteger index = new AtomicInteger(1);
            for (String column : orderedColumns.keySet()) {
                Object columnValue = orderedColumns.get(column).orElse(null);
                ps.setObject(index.getAndIncrement(), columnValue);
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
