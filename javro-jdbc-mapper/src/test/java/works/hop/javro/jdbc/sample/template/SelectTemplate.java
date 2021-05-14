package works.hop.javro.jdbc.sample.template;

import io.vavr.Tuple3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.javro.jdbc.sample.*;
import works.hop.javro.jdbc.template.ConnectionProvider;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static works.hop.javro.jdbc.sample.template.StaticQueries.SELECT_LIST_BY_JOIN__COLUMN;
import static works.hop.javro.jdbc.sample.template.StaticQueries.SELECT_ONE_BY_ID;

public class SelectTemplate {

    static final Logger log = LoggerFactory.getLogger(InsertTemplate.class);

    private static List<String> allColumns(List<FieldInfo> entityInfo) {
        return entityInfo.stream()
                .filter(field -> !field.isCollection)
                .map(field -> field.columnName).collect(Collectors.toList());
    }

    private static List<String> idColumns(List<FieldInfo> entityInfo) {
        return entityInfo.stream()
                .filter(field -> field.isId)
                .map(field -> field.columnName).collect(Collectors.toList());
    }

    public static <E extends Unreflect> E selectOne(Class<E> type, Object[] args) {
        AtomicInteger columnPrefixChar = new AtomicInteger(65);
        StringBuilder queryBuilder = new StringBuilder("select");
        EntityInfo entityInfo = EntityMetadata.getEntityInfo.apply(type);

        List<String> allNonRelationalColumns = entityInfo.getFields().stream()
                .filter(field -> !field.isRelational)
                .map(field -> field.columnName)
                .collect(Collectors.toList());
        int columnCount = 0;
        char columnPrefix = (char) columnPrefixChar.getAndIncrement();
        for(String columnName : allNonRelationalColumns){
            queryBuilder.append(" ").append(columnPrefix).append(".").append(columnName);
            columnCount++;
            if(columnCount < allNonRelationalColumns.size()){
                queryBuilder.append(", ");
            }
        }

        List<FieldInfo> allOneToOneRelationalFields = entityInfo.getFields().stream()
                .filter(field -> field.isRelational)
                .filter(filed -> !filed.isCollection)
                .collect(Collectors.toList());
        for(FieldInfo columnField : allOneToOneRelationalFields){
            char tablePrefix = (char) columnPrefixChar.getAndIncrement();
            EntityInfo relationalEntityInfo = EntityMetadata.getEntityInfo.apply(columnField.type);
            FieldInfo relationalEntityPkField = relationalEntityInfo.getFields().stream()
                    .filter(field -> field.isId)
                    .findFirst().orElse(null);
            if(relationalEntityPkField != null) {
                queryBuilder.append(" inner join ").append(tablePrefix).append(".").append(relationalEntityInfo.getTableName());
                queryBuilder.append(" on ").append(tablePrefix).append(".").append(relationalEntityPkField.columnName);
                queryBuilder.append(" = ?::uuid");
            }
        }

        E parentValue;
        try (Connection conn = ConnectionProvider.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(queryBuilder.toString())) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    MapResultSetToEntity mapper = new MapResultSetToEntity();
                    parentValue = mapper.mapRsToEntity(rs, type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Problem executing fetch query", e);
        }

        if(parentValue != null) {
            List<FieldInfo> allManyToOneRelationalFields = entityInfo.getFields().stream()
                    .filter(field -> field.isRelational)
                    .filter(filed -> filed.isCollection)
                    .collect(Collectors.toList());
            for (FieldInfo columnField : allManyToOneRelationalFields) {
                Object relationalIdValue = parentValue.get("id");
                Class<?> collectionItemType = columnField.type;
                Object relationCollection = selectList(entityInfo, collectionItemType, new Object[]{relationalIdValue});
                parentValue.set(columnField.name, relationCollection);
            }
        }
        return parentValue;
    }

    public static <E extends Unreflect> Collection<E> selectList(EntityInfo entityInfo, Class<E> type, Object[] args) {
        StringBuilder queryBuilder = new StringBuilder("select ");
        List<FieldInfo> allNonCollectionColumns = entityInfo.getFields().stream()
                .filter(field -> !field.isCollection)
                .collect(Collectors.toList());

        int count = 0;
        for(FieldInfo fieldInfo : allNonCollectionColumns){
            queryBuilder.append(fieldInfo.columnName);
            count++;
            if(count < allNonCollectionColumns.size()){
                queryBuilder.append(", ");
            }
        }
        String pkFieldName = entityInfo.getFields().stream()
                .filter(field -> field.isId)
                .map(field -> field.columnName)
                .findFirst().orElse("id");
        queryBuilder.append(" where ").append(pkFieldName).append(" = ?::uuid");

        try (Connection conn = ConnectionProvider.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(queryBuilder.toString())) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    MapResultSetToEntity mapper = new MapResultSetToEntity();
                    return mapper.mapRsToEntityCollection(rs, type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Problem executing fetch query", e);
        }
    }
}
