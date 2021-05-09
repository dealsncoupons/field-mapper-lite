package works.hop.javro.jdbc.resolver;

import works.hop.javro.jdbc.annotation.Embedded;
import works.hop.javro.jdbc.reflect.ReflectionUtil;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Arrays;

import static works.hop.javro.jdbc.reflect.ReflectionUtil.getColumnName;
import static works.hop.javro.jdbc.reflect.ReflectionUtil.isSimpleDataField;

public class EmbeddedResolver<T> implements AnnotationResolver {

    final T target;

    public EmbeddedResolver(T target) {
        this.target = target;
    }

    @Override
    public void resolve(Field field, ResolverContext context) {
        if (field.isAnnotationPresent(Embedded.class)) {
            Class<?> fieldType = field.getType();
            Object fieldValue = ReflectionUtil.entityInstance(fieldType);
            do {
                Field[] fields = fieldType.getDeclaredFields();
                Arrays.stream(fields).filter(ReflectionUtil::isAcceptableField).forEach(embeddedField -> {
                    if (isSimpleDataField(embeddedField.getType())) {
                        try {
                            ReflectionUtil.set(embeddedField, fieldValue, context.getResultSet().getObject(getColumnName(embeddedField), embeddedField.getType()));
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("Skipping field " + field.getName());
                        }
                    } else {
                        AbstractResolver<?> entityFetcher = ReflectionUtil.resolverInstance(embeddedField.getType());
                        entityFetcher.resolve(embeddedField, context);
                        Object value = entityFetcher.targetObject();
                        ReflectionUtil.set(embeddedField, target, value);
                    }
                });
                fieldType = field.getType().getSuperclass();
            }
            while (fieldType != Object.class);
            ReflectionUtil.set(field, target, fieldValue);
        }
    }

    @Override
    public T targetObject() {
        return target;
    }
}
