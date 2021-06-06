package works.hop.javro.jdbc.sample.template;

import io.vavr.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.javro.jdbc.sample.EntityInfo;
import works.hop.javro.jdbc.sample.EntityMetadata;
import works.hop.javro.jdbc.sample.Unreflect;
import works.hop.javro.jdbc.template.ConnectionProvider;
import works.hop.javro.jdbc.template.QueryExecutor;

import java.sql.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class InsertTemplate {

    static final Logger log = LoggerFactory.getLogger(InsertTemplate.class);

    private static String prepareInsertQuery(EntityInfo entityInfo, Map<String, Object> record) {
        String fieldNames = String.join(", ", record.keySet());
        String valueFields = Arrays.stream(fieldNames.split(", ")).map(f -> "?").collect(Collectors.joining(","));
        return String.join(" ", "insert into", entityInfo.getTableName(), "(", fieldNames, ") values (", valueFields, ") on conflict do nothing returning id::uuid");
    }

    private static String pkFieldName(EntityInfo entityInfo) {
        return entityInfo.getFields().stream()
                .filter(field -> field.isId)
                .map(field -> field.name).findFirst().orElse("id");
    }

    public static <E extends Unreflect> E insertOne(E entity) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);
            E insertResult = insertOne(entity, conn);
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

    private static <E extends Unreflect> E insertOne(E entity, Connection conn) {
        EntityInfo entityInfo = EntityMetadata.entityInfoByType.apply(entity.getClass());
        Map<String, Object> record = columnsToInsert(entity, entityInfo);
        return insertInTransaction(entity, entityInfo, record, conn);
    }

    private static <E extends Unreflect> Map<String, Object> columnsToInsert(E entitySource, EntityInfo entityInfo) {
        return entityInfo.getFields().stream()
                .filter(field -> !field.isId)
                .filter(field -> !field.isRelational)
                .filter(field -> entitySource.get(field.name) != null)
                .map(field -> new Tuple2<>(field.columnName, entitySource.get(field.name)))
                .collect(Collectors.toMap(tuple -> tuple._1, tuple -> tuple._2));
    }

    private static <E extends Unreflect> E insertInTransaction(E entitySource, EntityInfo entityInfo, Map<String, Object> record, Connection connection) {
        E inserted = doInsertInTransaction(entitySource, entityInfo, record, connection);
        entityInfo.getFields().stream()
                .filter(field -> field.isRelational)
                .forEach(field -> {
                    if (!field.isCollection) {
                        Unreflect fkEntityValue = entitySource.get(field.name);
                        if (fkEntityValue != null) {
                            EntityInfo fkEntityInfo = EntityMetadata.entityInfoByType.apply(field.type);
                            Map<String, Object> fkFieldRecord = columnsToInsert(fkEntityValue, fkEntityInfo);

                            String insertedPkFieldName = pkFieldName(entityInfo);
                            Object insertedPkFieldValue = inserted.get(insertedPkFieldName);
                            fkFieldRecord.put(field.columnName, insertedPkFieldValue);

                            entitySource.set(field.name, insertedPkFieldValue);

                            String fkFieldName = pkFieldName(fkEntityInfo);
                            Unreflect relationValue = insertInTransaction(fkEntityValue, fkEntityInfo, fkFieldRecord, connection);
                            log.info("inserted one-to-one relation entity with new id - {}", relationValue.get(fkFieldName).toString());
                        }
                    } else {
                        Collection<? extends Unreflect> fkCollectionFieldValue = entitySource.get(field.name);
                        if (fkCollectionFieldValue != null) {
                            for (Unreflect fkEntityValue : fkCollectionFieldValue) {
                                EntityInfo fkEntityInfo = EntityMetadata.entityInfoByType.apply(field.type);
                                Map<String, Object> fkFieldRecord = columnsToInsert(fkEntityValue, fkEntityInfo);

                                String insertedPkFieldName = pkFieldName(entityInfo);
                                Object insertedPkFieldValue = inserted.get(insertedPkFieldName);
                                fkFieldRecord.put(field.columnName, insertedPkFieldValue);

                                String fkFieldName = pkFieldName(fkEntityInfo);
                                Unreflect relationValue = insertInTransaction(fkEntityValue, fkEntityInfo, fkFieldRecord, connection);
                                log.info("inserted many-to-one relation entity with new id - {}", relationValue.get(fkFieldName).toString());
                            }
                        }
                    }
                });
        return inserted;
    }

    private static <E extends Unreflect> E doInsertInTransaction(E entitySource, EntityInfo entityInfo, Map<String, Object> record, Connection connection) {
        String query = prepareInsertQuery(entityInfo, record);
        try (PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            AtomicInteger index = new AtomicInteger(1);
            for (String column : record.keySet()) {
                Object columnValue = record.get(column);
                ps.setObject(index.getAndIncrement(), columnValue);
            }
            int rowsAffected = ps.executeUpdate();
            log.info("{} row(s) affected after insert operation", rowsAffected);

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    String pkColumnName = pkFieldName(entityInfo);
                    UUID uuid = UUID.fromString(keys.getString(1));
                    entitySource.set(pkColumnName, uuid);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                log.warn("Could not retrieve generated id value", e);
            }

            return entitySource;
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
