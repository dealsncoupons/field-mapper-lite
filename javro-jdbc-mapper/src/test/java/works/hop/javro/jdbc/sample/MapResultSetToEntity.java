package works.hop.javro.jdbc.sample;

import works.hop.javro.jdbc.sample.template.SelectTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MapResultSetToEntity {

    private <T> T mapRsToEntityRow(ResultSet rs, Class<T> type, EntityInfo entityInfo) throws SQLException {
        Map<String, Object> source = new HashMap<>();
        for (FieldInfo field : entityInfo.getFields()) {
            if (field.isEmbedded) {
                Class<?> embeddedType = field.type;
                EntityInfo embeddedEntityInfo = EntityMetadata.getEntityInfo.apply(embeddedType);
                Object embeddedValue = mapRsToEntityRow(rs, embeddedType, embeddedEntityInfo);
                source.put(field.name, embeddedValue);
            } else if (field.isRelational) {
                boolean hasJoinTable = field.joinTable != null && field.joinTable.trim().length() > 0;
                if (field.isCollection) {
                    if (hasJoinTable) {
                        System.out.println("collection relation with join column - Not yet handled");
                    } else {
                        Class<?> relationalType = field.type;
                        Object joinValue = rs.getObject(field.columnName, relationalType);
                        Object relationalValue = SelectTemplate.selectListByJoinColumn(entityInfo, relationalType, new Object[]{joinValue});
                        source.put(field.name, relationalValue);
                    }
                } else {
                    if (hasJoinTable) {
                        System.out.println("non-collection relation with join column - Not yet handled");
                    } else {
                        Class<?> relationalType = field.type;
                        EntityInfo joinEntityInfo = EntityMetadata.getEntityInfo.apply(relationalType);
                        Class<?> joinType = joinEntityInfo.getFields().stream()
                                .filter(f -> f.isId)
                                .map(f -> f.type)
                                .findFirst().orElse(null);
                        if (joinType != null) {
                            Object joinValue = rs.getObject(field.columnName, joinType);
                            Object relationalValue = SelectTemplate.selectOne(relationalType, new Object[]{joinValue});
                            source.put(field.name, relationalValue);
                        }
                    }
                }
            } else {
                Object columnValue = rs.getObject(field.columnName, field.type);
                source.put(field.name, columnValue);
            }
        }
        return EntityProxyFactory.create(type, source);
    }

    public <T> T mapRsToEntity(ResultSet rs, Class<T> type) throws SQLException {
        EntityInfo entityInfo = EntityMetadata.getEntityInfo.apply(type);
        if (rs.next()) {
            return mapRsToEntityRow(rs, type, entityInfo);
        }
        return EntityProxyFactory.create(type, Collections.emptyMap());
    }

    public <T> Collection<T> mapRsToEntityCollection(ResultSet rs, Class<T> type) throws SQLException {
        Collection<T> collection = new LinkedList<>();
        EntityInfo entityInfo = EntityMetadata.getEntityInfo.apply(type);
        while (rs.next()) {
            collection.add(mapRsToEntityRow(rs, type, entityInfo));
        }
        return collection;
    }
}
