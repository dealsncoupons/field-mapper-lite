// This metadata class is AUTO-GENERATED, so there's no point of modifying it

import works.hop.javro.gen.metadata.EntityInfo;
import works.hop.sample.app.model.Task;
import works.hop.sample.app.model.TaskTO;

import java.util.function.Function;

public class EntityMetadata {
    public static Function<Class, EntityInfo> entityInfoByType = getEntityInfoByType();

    private static Function<Class, EntityInfo> getEntityInfoByType() {
        return entityType -> {
            if (Task.class.isAssignableFrom(entityType)) {
//        return getTaskInfo().get();
                return null;
            }
            if (TaskTO.class.isAssignableFrom(entityType)) {
//        return getTaskTOInfo().get();
                return null;
            }
            throw new RuntimeException("Unknown entity type - " + entityType.getName());
        };
    }
}
