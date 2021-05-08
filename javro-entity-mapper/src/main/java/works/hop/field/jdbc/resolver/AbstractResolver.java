package works.hop.field.jdbc.resolver;

import com.google.common.primitives.Primitives;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.field.jdbc.annotation.*;
import works.hop.field.jdbc.mapper.MapperUtils;
import works.hop.field.jdbc.template.FetchersFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Stream;

public abstract class AbstractResolver<T> implements Resolver {

    static final Logger log = LoggerFactory.getLogger(AbstractResolver.class);
    public final Map<Field, Resolver> resolvers = new HashMap<>();
    private final T target;

    public AbstractResolver(Class<T> type) {
        this.target = FetchersFactory.entityInstance(type);
        Class<?> targetClass = type;
        do {
            Field[] fields = targetClass.getDeclaredFields();
            Arrays.stream(fields).filter(AbstractResolver::isAcceptableField).forEach(field -> {
                if (field.isAnnotationPresent(Embedded.class)) {
                    resolvers.put(field, new EmbeddedResolver<>(target));
                } else if (field.isAnnotationPresent(JoinColumn.class)) {
                    resolvers.put(field, new JoinColumnResolver<>(target, getJoinColumnName(field)));
                } else {
                    resolvers.put(field, new ColumnResolver<>(target, getColumnName(field)));
                }
            });
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != Object.class);
    }

    public static ResolverContext createContext(ResultSet resultSet) {
        return () -> resultSet;
    }

    public static boolean isAcceptableField(Field field) {
        int modifiers = field.getModifiers();
        return !Modifier.isFinal(modifiers) && !Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers);
    }

    public static boolean isCollectionField(Field field) {
        return Stream.of(List.class, Set.class, Queue.class).anyMatch(fieldType -> field.getType().isAssignableFrom(fieldType));
    }

    public static boolean isAssociationField(Field field) {
        return Map.class.isAssignableFrom(field.getType());
    }

    public static boolean isSimpleDataField(Class<?> type) {
        return type.isPrimitive() || Primitives.isWrapperType(type) ||
                Stream.of("java.lang", "java.time", "java.util.UUID").anyMatch(pkg -> type.getName().startsWith(pkg));
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

    public static <E> void setIdValue(Class<?> entityClass, E entity, UUID uuid) {
        Optional<Field> idField = Arrays.stream(entityClass.getDeclaredFields()).filter(field -> field.isAnnotationPresent(Id.class)).findFirst();
        if (idField.isPresent()) {
            Field field = idField.get();
            MapperUtils.set(field, entity, uuid);
        } else {
            if (entityClass.getSuperclass() != Object.class) {
                setIdValue(entityClass.getSuperclass(), entity, uuid);
            } else {
                log.warn("Could not find id field for entity - " + entity.getClass());
            }
        }
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

    @Override
    public void resolve(Field field, ResolverContext context) {
        resolvers.forEach((keyField, resolver) -> {
            resolver.resolve(keyField, context);
        });
    }

    @Override
    public T targetObject() {
        return this.target;
    }
}
