package works.hop.javro.jdbc.sample;

import works.hop.javro.jdbc.EntityInfo;
import works.hop.javro.jdbc.sample.account.ResultSetMultipleRows;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MapResultSetToEntity {

    private <T> T mapRsToEntityRow(ResultSet rs, Class<T> type, List<FieldInfo> entityFields) throws SQLException {
        Map<String, Object> source = new HashMap<>();
        for (FieldInfo field : entityFields) {
            if (field.isEmbedded) {
                Class<?> embeddedType = field.type;
                EntityInfo embeddedEntityInfo = EntityMetadata.getEntityInfo.apply(embeddedType);
                Object embeddedValue = mapRsToEntityRow(rs, embeddedType, embeddedEntityInfo.getFields());
                source.put(field.name, embeddedValue);
            } else if (field.isRelational) {
                boolean hasJoinTable = field.joinTable != null && field.joinTable.trim().length() > 0;
                if (field.isCollection) {
                    if (hasJoinTable) {
                        System.out.println("collection relation with join column - Not yet handled");
                    } else {
                        System.out.println("collection relation without join column - Not yet handled");
                    }
                } else {
                    if (hasJoinTable) {
                        System.out.println("non-collection relation with join column - Not yet handled");
                    }
                    else {
                        Class<?> relationalType = field.type;
                        MapResultSetToEntity mapper = new MapResultSetToEntity();
                        //line below should make use of a service to fetch new result set
                        ResultSet relationalResultSet = ResultSetMultipleRows.membersResultSet();
                        Object relationalValue = mapper.mapRsToEntity(relationalResultSet, relationalType);
                        source.put(field.name, relationalValue);
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
            return mapRsToEntityRow(rs, type, entityInfo.getFields());
        }
        return EntityProxyFactory.create(type, Collections.emptyMap());
    }

    public <T> Collection<T> mapRsToEntityCollection(ResultSet rs, Class<T> type) throws SQLException {
        Collection<T> collection = new LinkedList<>();
        EntityInfo entityInfo= EntityMetadata.getEntityInfo.apply(type);
        while (rs.next()) {
            collection.add(mapRsToEntityRow(rs, type, entityInfo.getFields()));
        }
        return collection;
    }
}
