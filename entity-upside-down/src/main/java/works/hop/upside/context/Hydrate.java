package works.hop.upside.context;

import org.apache.kafka.connect.data.Struct;
import works.hop.upside.relations.EntityInfo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Optional;

public interface Hydrate {

    EntityInfo getEntityInfo();

    <O> O get(String property);

    <O> void set(String property, O value);

    <E extends Hydrate> E select(ResultSet rs, DbSelect resolver, Connection connection, LocalCache cache);

    <E extends Hydrate> E insert(Connection connection);

    <E extends Hydrate> E update(Map<String, Object> columnValues, Connection connection);

    <E extends Hydrate> E delete(Connection connection);

    <E extends Hydrate> E refresh(Struct record);

    default void extractEntityValues(Map<String, Optional<Object>> parameters, Hydrate entity, EntityInfo entityInfo) {
        entityInfo.getFields().stream()
                .filter(fieldInfo -> !fieldInfo.isCollection).forEach(fieldInfo -> {
            String key = fieldInfo.columnName;
            if (fieldInfo.isRelational) {
                Hydrate relationalEntity = entity.get(fieldInfo.name);
                if (relationalEntity != null) {
                    parameters.put(key, Optional.ofNullable(relationalEntity.get("id")));
                }
            } else if (fieldInfo.isEmbedded) {
                Hydrate embeddedEntity = entity.get(fieldInfo.name);
                if (embeddedEntity != null) {
                    EntityInfo embeddedEntityInfo = embeddedEntity.getEntityInfo();
                    extractEntityValues(parameters, embeddedEntity, embeddedEntityInfo);
                }
            } else {
                parameters.put(key, Optional.ofNullable(entity.get(fieldInfo.name)));
            }
        });
    }
}
