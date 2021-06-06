package works.hop.javro.jdbc.sample;

import works.hop.javro.jdbc.sample.template.SelectTemplate;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapResultSetToEntity {

    private final Map<String, Unreflect> localCache;

    public MapResultSetToEntity(Map<String, Unreflect> localCache) {
        this.localCache = localCache;
    }

    private <T extends Unreflect> T mapRsToEntityRow(ResultSet rs, Class<?> type, EntityInfo entityInfo, Connection conn) throws SQLException {
        Unreflect source = EntityInstance.create(type);

        //get Entity's primary key field(s)
        FieldInfo idField = entityInfo.getFields().stream()
                .filter(field -> field.isId)
                .findFirst().orElse(null);

        String idValue = rs.getObject(idField.columnName, idField.type).toString();
        if (localCache.containsKey(idValue)) {
            return (T) localCache.get(idValue);
        } else {
            localCache.put(idValue, source);
        }

        //retrieve values for all non-relational fields
        List<FieldInfo> nonRelationFields = entityInfo.getFields().stream()
                .filter(field -> !field.isRelational)
                .collect(Collectors.toList());

        //populate non-relational fields retrieved
        for (FieldInfo field : nonRelationFields) {
            if (field.isEmbedded) {
                Class<?> embeddedType = field.type;
                EntityInfo embeddedEntityInfo = EntityMetadata.entityInfoByType.apply(embeddedType);
                Object embeddedValue = mapRsToEntityRow(rs, type, embeddedEntityInfo, conn);
                source.set(field.name, embeddedValue);
            } else {
                Object columnValue = rs.getObject(field.columnName, field.type);
                source.set(field.name, columnValue);
            }
        }

        //deal with relational fields
        List<FieldInfo> relationFields = entityInfo.getFields().stream()
                .filter(field -> field.isRelational)
                .collect(Collectors.toList());

        for (FieldInfo field : relationFields) {
            boolean hasJoinTable = field.joinTable != null && field.joinTable.trim().length() > 0;
            if (!field.isCollection) {
                if (hasJoinTable) {
                    System.out.println("non-collection relation with join column - Not yet handled");
                } else {
                    Class<?> relationalType = field.type;
                    EntityInfo relationalTypeEntityInfo = EntityMetadata.entityInfoByType.apply(relationalType);
                    FieldInfo relationalTypeIdField = relationalTypeEntityInfo.getFields().stream()
                            .filter(relationalField -> relationalField.isId)
                            .findFirst().orElse(null);
                    if (relationalTypeIdField != null) {
                        Object joinFkValue = rs.getObject(field.columnName, relationalTypeIdField.type);
                        if (joinFkValue != null) {
                            Unreflect relationalValue = SelectTemplate.selectOne(relationalType, new Object[]{joinFkValue}, localCache);
                            source.set(field.name, relationalValue);
                        }
                    }
                }
            } else {
                if (hasJoinTable) {
                    System.out.println("collection relation with join column - Not yet handled");
                } else {
                    Class<?> joinCollectionItemType = field.type;
                    EntityInfo joinCollectionItemEntityInfo = EntityMetadata.entityInfoByType.apply(joinCollectionItemType);
                    Object joinPkValue = rs.getObject(idField.columnName, idField.type);
                    if (joinPkValue != null) {
                        Collection<Unreflect> relationalValue = SelectTemplate.selectList(joinCollectionItemEntityInfo, field, new Object[]{joinPkValue}, conn, this);
                        source.set(field.name, relationalValue);
                    }
                }
            }
        }
        return (T) source;
    }

    public <T extends Unreflect> T mapRsToEntity(ResultSet rs, Class<?> type, Connection conn) throws SQLException {
        EntityInfo entityInfo = EntityMetadata.entityInfoByType.apply(type);
        if (rs.next()) {
            return mapRsToEntityRow(rs, type, entityInfo, conn);
        }
        return EntityInstance.create(type);
    }

    public <T extends Unreflect> Collection<T> mapRsToEntityCollection(ResultSet rs, Class<?> type, EntityInfo entityInfo, Connection conn) throws SQLException {
        Collection<T> collection = new LinkedList<>();
        while (rs.next()) {
            collection.add(mapRsToEntityRow(rs, type, entityInfo, conn));
        }
        return collection;
    }
}
