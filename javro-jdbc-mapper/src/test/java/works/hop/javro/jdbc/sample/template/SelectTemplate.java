package works.hop.javro.jdbc.sample.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.javro.jdbc.sample.*;
import works.hop.javro.jdbc.template.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SelectTemplate {

    static final Logger log = LoggerFactory.getLogger(InsertTemplate.class);

    private static String selectOneQuery(EntityInfo entityInfo) {
        StringBuilder queryBuilder = new StringBuilder("select ");
        List<String> dataColumns = entityInfo.getFields().stream()
                .filter(field -> !field.isCollection)
                .map(field -> field.columnName)
                .collect(Collectors.toList());

        String pkColumn = entityInfo.getFields().stream()
                .filter(field -> field.isId)
                .map(field -> field.columnName)
                .findFirst().orElse("id");

        int endOfList = 0;
        for (String column : dataColumns) {
            queryBuilder.append(column);
            endOfList++;
            if (endOfList < dataColumns.size()) {
                queryBuilder.append(", ");
            }
        }
        queryBuilder.append(" from ").append(entityInfo.getTableName());
        queryBuilder.append(" where ").append(pkColumn).append(" = ?::uuid");
        return queryBuilder.toString();
    }

    private static String selectListQuery(String rightTableName, String rightTablePk, EntityInfo leftTableEntityInfo, String joinColumn) {
        char leftTableAlias = 'L', rightTableAlias = 'R';
        StringBuilder queryBuilder = new StringBuilder("select ");
        List<String> leftTableColumns = leftTableEntityInfo.getFields().stream()
                .filter(field -> !field.isCollection)
                .map(field -> field.columnName)
                .collect(Collectors.toList());
        int endOfList = 0;

        for (String column : leftTableColumns) {
            queryBuilder.append(leftTableAlias).append(".").append(column);
            endOfList++;
            if (endOfList < leftTableColumns.size()) {
                queryBuilder.append(", ");
            }
        }
        queryBuilder.append(" from ").append(leftTableEntityInfo.getTableName()).append(" as ").append(leftTableAlias);
        queryBuilder.append(" inner join ").append(rightTableName).append(" as ").append(rightTableAlias);
        queryBuilder.append(" on ").append(leftTableAlias).append(".").append(joinColumn);
        queryBuilder.append(" = ").append(rightTableAlias).append(".").append(rightTablePk);
        queryBuilder.append(" where ").append(leftTableAlias).append(".").append(joinColumn).append(" = ?::uuid");
        return queryBuilder.toString();
    }

    public static <E extends Unreflect> E selectOne(Class<?> type, Object[] args, Map<String, Unreflect> localCache) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            MapResultSetToEntity mapper = new MapResultSetToEntity(localCache);
            return selectOne(type, args, conn, mapper);
        } catch (Exception e) {
            String errorMessage = "Problem executing fetch query";
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    public static <E extends Unreflect> E selectOne(Class<?> type, Object[] args, Connection conn, MapResultSetToEntity mapper) {
        EntityInfo entityInfo = EntityMetadata.entityInfoByType.apply(type);
        String query = selectOneQuery(entityInfo);

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return mapper.mapRsToEntity(rs, type, conn);
            }
        } catch (Exception e) {
            String errorMessage = "Problem executing fetch query";
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    public static <E extends Unreflect> Collection<E> selectList(EntityInfo parentEntityInfo, FieldInfo joinField, Object[] args, Map<String, Unreflect> localCache) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            MapResultSetToEntity mapper = new MapResultSetToEntity(localCache);
            return selectList(parentEntityInfo, joinField, args, conn, mapper);
        } catch (Exception e) {
            String errorMessage = "Problem executing fetch query";
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    public static <E extends Unreflect> Collection<E> selectList(EntityInfo entityInfo, FieldInfo joinField, Object[] args, Connection conn, MapResultSetToEntity mapper) {
        EntityInfo joinEntityInfo = EntityMetadata.entityInfoByType.apply(joinField.type);
        String parentPkColumn = entityInfo.getFields().stream()
                .filter(field -> field.isId)
                .map(field -> field.columnName)
                .findFirst().orElse("id");
        String query = selectListQuery(entityInfo.getTableName(), parentPkColumn, joinEntityInfo, joinField.columnName);

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return mapper.mapRsToEntityCollection(rs, joinField.type, joinEntityInfo, conn);
            }
        } catch (Exception e) {
            String errorMessage = "Problem executing fetch query";
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }
}
