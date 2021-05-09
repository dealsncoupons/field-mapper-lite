package works.hop.field.reflect;

import com.google.common.primitives.Primitives;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

public class ReflectionUtil {

    private static final Logger log = LoggerFactory.getLogger(ReflectionUtil.class);

    public static boolean isAcceptableField(Field field) {
        int modifiers = field.getModifiers();
        return !Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers);
    }

    public static boolean isSimpleDataField(Class<?> type) {
        return type.isPrimitive() || Primitives.isWrapperType(type) ||
                Stream.of("java.lang", "java.time", "java.util.UUID").anyMatch(pkg -> type.getName().startsWith(pkg));
    }

    public static boolean isCollectionField(Field field) {
        return Stream.of(List.class, Set.class, Queue.class).anyMatch(fieldType -> field.getType().isAssignableFrom(fieldType));
    }

    public static boolean isAssociationField(Field field) {
        return Map.class.isAssignableFrom(field.getType());
    }

    public static Object getFieldValue(Field field, Object source) {
        boolean isAccessible = field.canAccess(source);
        try {
            field.setAccessible(true);
            return field.get(source);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            log.warn("Could not get field value for {}", field.getName());
            return null;
        } finally {
            field.setAccessible(isAccessible);
        }
    }

    public static Optional<Field> getTargetField(String fieldName, Object target, Class<?> type) {
        for (Field field : type.getDeclaredFields()) {
            if (isAcceptableField(field)) {
                if (field.getName().equals(fieldName)) {
                    return Optional.of(field);
                }
            }
        }
        if (type.getSuperclass() != Object.class) {
            return getTargetField(fieldName, target, type.getSuperclass());
        } else {
            return Optional.empty();
        }
    }

    public static Collection<Object> newCollectionInstance(Class<?> type) {
        if (List.class.isAssignableFrom(type)) {
            return new ArrayList<>();
        } else if (Set.class.isAssignableFrom(type)) {
            return new HashSet<>();
        } else if (Queue.class.isAssignableFrom(type)) {
            return new LinkedList<>();
        } else {
            throw new RuntimeException("Could not determine instance type for collection instance");
        }
    }
}
