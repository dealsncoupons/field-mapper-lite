package works.hop.javro.jdbc.reflect;

import com.google.common.primitives.Primitives;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.javro.jdbc.annotation.Column;
import works.hop.javro.jdbc.annotation.Id;
import works.hop.javro.jdbc.annotation.JoinColumn;
import works.hop.javro.jdbc.annotation.Table;
import works.hop.javro.jdbc.resolver.AbstractResolver;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReflectionUtil {

    private static final Logger log = LoggerFactory.getLogger(ReflectionUtil.class);

    public static void set(Field field, Object target, Object value) {
        boolean isAccessible = field.canAccess(target);
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            log.warn("Could not SET value in field {} in object {}", field.getName(), target.getClass().getName());
        } finally {
            field.setAccessible(isAccessible);
        }
    }

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

    public static List<Field> getJoinColumnFields(Class<?> type) {
        List<Field> annotatedFields = new ArrayList<>();
        for (Field field : type.getDeclaredFields()) {
            if (isAcceptableField(field)) {
                if (field.isAnnotationPresent(JoinColumn.class)) {
                    annotatedFields.add(field);
                }
            }
        }
        if (type.getSuperclass() != Object.class) {
            annotatedFields.addAll(getJoinColumnFields(type.getSuperclass()));
        }
        return annotatedFields;
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

    public static String getColumnName(Field field) {
        return (field.isAnnotationPresent(Column.class)) ?
                field.getAnnotation(Column.class).value() :
                field.getName();
    }

    public static String getJoinColumnName(Field field) {
        return (field.isAnnotationPresent(JoinColumn.class)) ?
                field.getAnnotation(JoinColumn.class).value() :
                field.getName();
    }

    public static String getTableName(Class<?> entityClass) {
        return entityClass.isAnnotationPresent(Table.class) ?
                entityClass.getAnnotation(Table.class).value() :
                entityClass.getSimpleName().toLowerCase();
    }

    public static List<String> getIdColumns(Class<?> entityClass) {
        List<String> idColumns = Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class)).map(field -> {
                    if (field.isAnnotationPresent(Column.class)) {
                        return field.getAnnotation(Column.class).value();
                    } else return field.getName();
                }).collect(Collectors.toList());
        if (entityClass.getSuperclass() != null) {
            idColumns.addAll(getIdColumns(entityClass.getSuperclass()));
        }
        return idColumns;
    }

    public static Map<String, Optional<Object>> getIdColumnValues(Class<?> entityClass, Object entity) {
        Map<String, Optional<Object>> idColumns =
                Arrays.stream(entityClass.getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(Id.class))
                        .collect(Collectors.toMap(Field::getName, field -> Optional.ofNullable(getFieldValue(field, entity))));
        if (idColumns.isEmpty() && entityClass.getSuperclass() != null) {
            idColumns.putAll(getIdColumnValues(entityClass.getSuperclass(), entity));
        }
        return idColumns;
    }

    public static <E> void setIdValue(Class<?> entityClass, E entity, UUID uuid) {
        Optional<Field> idField = Arrays.stream(entityClass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Id.class)).findFirst();
        if (idField.isPresent()) {
            Field field = idField.get();
            ReflectionUtil.set(field, entity, uuid);
        } else {
            if (entityClass.getSuperclass() != Object.class) {
                setIdValue(entityClass.getSuperclass(), entity, uuid);
            } else {
                log.warn("Could not find id field for entity - " + entity.getClass());
            }
        }
    }

    public static <T> AbstractResolver<T> resolverInstance(Class<T> entityType) {
        return new AbstractResolver<>(entityType) {
        };
    }

    public static <T> T entityInstance(Class<T> entityType) {
        try {
            return entityType.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not create entity instance", e);
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
