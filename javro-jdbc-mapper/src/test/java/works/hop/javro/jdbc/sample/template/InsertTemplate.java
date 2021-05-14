package works.hop.javro.jdbc.sample.template;

import io.vavr.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.javro.jdbc.sample.EntityInfo;
import works.hop.javro.jdbc.sample.EntityMetadata;
import works.hop.javro.jdbc.sample.EntitySourceFactory;
import works.hop.javro.jdbc.sample.Unreflect;
import works.hop.javro.jdbc.template.ConnectionProvider;
import works.hop.javro.jdbc.template.QueryExecutor;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static works.hop.javro.jdbc.sample.template.StaticQueries.allColumnsExceptId;

public class InsertTemplate {

    static final Logger log = LoggerFactory.getLogger(InsertTemplate.class);

    private static String prepareInsertQuery(EntityInfo entityInfo, Map<String, Object> record) {
        List<String> fields = allColumnsExceptId(entityInfo.getFields()).stream().filter(record::containsKey).collect(Collectors.toList());
        String fieldNames = String.join(", ", fields);
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
            EntityInfo entityInfo = EntityMetadata.getEntityInfo.apply(entity.getClass());
            Unreflect entitySource = EntitySourceFactory.create(entity);
            E insertResult = (E) insertInTransaction(entitySource, entityInfo, new HashMap<>(), conn);
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

    private static <E extends Unreflect> E insertInTransaction(E entitySource, EntityInfo entityInfo, Map<String, Object> record, Connection connection) {
        entityInfo.getFields().stream()
                .filter(field -> !field.isId)
                .filter(field -> !field.isRelational)
                .filter(field -> entitySource.get(field.name) == null)
                .map(field -> new Tuple2<>(field.columnName, entitySource.get(field.name)))
                .forEach(tuple -> record.put(tuple._1, tuple._2));

        String query = prepareInsertQuery(entityInfo, record);
        E inserted = doInsertInTransaction(entitySource, entityInfo, query, record, connection);

        Map<String, Object> relationalRecord = new HashMap<>();
        entityInfo.getFields().stream()
                .filter(field -> field.isRelational)
                .forEach(field -> {
                    Unreflect fkFieldValue = entitySource.get(field.name);
                    if (fkFieldValue != null) {
                        if (!field.isCollection) {
                            String insertedPkFieldName = pkFieldName(entityInfo);
                            Object insertedPkFieldValue = inserted.get(insertedPkFieldName);
                            relationalRecord.put(field.columnName, insertedPkFieldValue);

                            EntityInfo fkEntityInfo = EntityMetadata.getEntityInfo.apply(field.type);
                            String fkFieldName = pkFieldName(fkEntityInfo);
                            Unreflect relationValue = insertInTransaction(fkFieldValue, fkEntityInfo, relationalRecord, connection);
                            log.info("inserted one-to-one relation entity with new id - {}", relationValue.get(fkFieldName).toString());
                        } else {
                            Collection<? extends Unreflect> fkCollectionFieldValue = entitySource.get(field.name);
                            for (Unreflect fkCollectionValue : fkCollectionFieldValue) {
                                String insertedPkFieldName = pkFieldName(entityInfo);
                                Object insertedPkFieldValue = inserted.get(insertedPkFieldName);
                                relationalRecord.put(field.columnName, insertedPkFieldValue);

                                EntityInfo fkEntityInfo = EntityMetadata.getEntityInfo.apply(field.type);
                                String fkFieldName = pkFieldName(fkEntityInfo);
                                Unreflect relationValue = insertInTransaction(fkCollectionValue, fkEntityInfo, relationalRecord, connection);
                                log.info("inserted many-to-one relation entity with new id - {}", relationValue.get(fkFieldName).toString());
                            }
                        }
                    }
                });
        return inserted;
    }

    private static <E extends Unreflect> E doInsertInTransaction(E entitySource, EntityInfo entityInfo, String query, Map<String, Object> record, Connection connection) {
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
