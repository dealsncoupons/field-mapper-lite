package works.hop.javro.jdbc.resolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.javro.jdbc.annotation.Embedded;
import works.hop.javro.jdbc.annotation.JoinColumn;
import works.hop.javro.jdbc.reflect.ReflectionUtil;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static works.hop.javro.jdbc.reflect.ReflectionUtil.getColumnName;
import static works.hop.javro.jdbc.reflect.ReflectionUtil.getJoinColumnName;

public abstract class AbstractResolver<T> implements AnnotationResolver {

    static final Logger log = LoggerFactory.getLogger(AbstractResolver.class);
    public final Map<Field, AnnotationResolver> resolvers = new HashMap<>();
    private final T target;

    public AbstractResolver(Class<T> type) {
        this.target = ReflectionUtil.entityInstance(type);
        Class<?> targetClass = type;
        do {
            Field[] fields = targetClass.getDeclaredFields();
            Arrays.stream(fields).filter(ReflectionUtil::isAcceptableField).forEach(field -> {
                if (field.isAnnotationPresent(Embedded.class)) {
                    resolvers.put(field, new EmbeddedResolver<>(target));
                    log.debug("Adding embedded annotation resolver");
                } else if (field.isAnnotationPresent(JoinColumn.class)) {
                    resolvers.put(field, new JoinColumnResolver<>(target, getJoinColumnName(field)));
                    log.debug("Adding join-column annotation resolver");
                } else {
                    resolvers.put(field, new ColumnResolver<>(target, getColumnName(field)));
                    log.debug("Adding column annotation resolver");
                }
            });
            targetClass = targetClass.getSuperclass();
        }
        while (targetClass != Object.class);
    }

    public static ResolverContext createContext(ResultSet resultSet) {
        return () -> resultSet;
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
