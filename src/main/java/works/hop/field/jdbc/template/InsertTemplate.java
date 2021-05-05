package works.hop.field.jdbc.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.field.jdbc.resolver.AbstractResolver;

import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static works.hop.field.jdbc.resolver.AbstractResolver.getTableName;

public class InsertTemplate {

    static final Logger log = LoggerFactory.getLogger(InsertTemplate.class);

    public static String prepareQuery(String tableName, Map<String, Object> fields) {
        String fieldNames = String.join(", ", fields.keySet());
        String valueFields = Arrays.stream(fieldNames.split(", ")).map(f -> "?").collect(Collectors.joining(","));
        return String.join(" ", "insert into", tableName, "(", fieldNames, ") values (", valueFields, ") on conflict do nothing returning id");
    }

    public static <E> E insertOne(E entity) {
        Map<String, Object> fields = new AbstractInsert(entity).fields.entrySet().stream().filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        String query = prepareQuery(getTableName(entity.getClass()), fields);

        try (Connection conn = ConnectionProvider.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                AtomicInteger index = new AtomicInteger(1);
                for (String fieldName : fields.keySet()) {
                    ps.setObject(index.getAndIncrement(), fields.get(fieldName));
                }
                int rowsAffected = ps.executeUpdate();
                log.info("{} row(s) affected after insert operation", rowsAffected);

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        UUID uuid = UUID.fromString(keys.getString(1));
                        AbstractResolver.setIdValue(entity.getClass(), entity, uuid);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    log.warn("Could not retrieve generated id value", e);
                }
                return entity;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Problem executing fetch query", e);
        }
    }

    public static <E> List<E> insertList(List<E> entities, Class<E> type) {
        List<Map<String, Object>> multipleEntities = entities.stream().map(entity -> new AbstractInsert(entity).fields.entrySet().stream().filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))).collect(Collectors.toList());

        if (entities.size() > 0) {
            String query = prepareQuery(getTableName(entities.get(0).getClass()), multipleEntities.get(0));
            try (Connection conn = ConnectionProvider.getConnection()) {
                try (PreparedStatement ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                    for (Map<String, Object> fields : multipleEntities) {
                        AtomicInteger index = new AtomicInteger(1);
                        for (String fieldName : fields.keySet()) {
                            ps.setObject(index.getAndIncrement(), fields.get(fieldName));
                        }
                    }
                    int[] rowsAffected = ps.executeBatch();
                    log.info("{} row(s) affected after insert operation", rowsAffected.length);

                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        List<UUID> insertedRecords = new ArrayList<>();
                        while (keys.next()) {
                            UUID uuid = UUID.fromString(keys.getString(1));
                            insertedRecords.add(uuid);
                        }
                        return SelectTemplate.selectList(String.format("select * from %s where id in (?)", getTableName(type)), rs -> {
                            AbstractResolver<E> entityFetcher = FetchersFactory.entityFetcher(type);
                            entityFetcher.resolve(null, AbstractResolver.createContext(rs));
                            return entityFetcher.targetObject();
                        }, new Object[]{insertedRecords.stream().map(UUID::toString).collect(Collectors.joining(","))});
                    } catch (SQLException e) {
                        e.printStackTrace();
                        log.warn("Could not retrieve generated id value", e);
                        return entities;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                throw new RuntimeException("Problem executing fetch query", e);
            }
        } else {
            return Collections.emptyList();
        }
    }
}
