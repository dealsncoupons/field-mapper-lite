package works.hop.field.jdbc.resolver;

import works.hop.field.jdbc.annotation.Embedded;
import works.hop.field.jdbc.mapper.MapperUtils;
import works.hop.field.jdbc.template.FetchersFactory;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.Arrays;

import static works.hop.field.jdbc.resolver.AbstractResolver.getColumnName;
import static works.hop.field.jdbc.resolver.AbstractResolver.isSimpleDataField;

public class EmbeddedResolver<T> implements Resolver {

    final T target;

    public EmbeddedResolver(T target) {
        this.target = target;
    }

    @Override
    public void resolve(Field field, ResolverContext context) {
        if (field.isAnnotationPresent(Embedded.class)) {
            Class<?> fieldType = field.getType();
            Object fieldValue = FetchersFactory.entityInstance(fieldType);
            do {
                Field[] fields = fieldType.getDeclaredFields();
                Arrays.stream(fields).filter(AbstractResolver::isAcceptableField).forEach(embeddedField -> {
                    if (isSimpleDataField(embeddedField.getType())) {
                        try {
                            MapperUtils.set(embeddedField, fieldValue, context.getResultSet().getObject(getColumnName(embeddedField), embeddedField.getType()));
                        } catch (SQLException e) {
                            e.printStackTrace();
                            System.out.println("Skipping field " + field.getName());
                        }
                    } else {
                        AbstractResolver<?> entityFetcher = FetchersFactory.entityFetcher(embeddedField.getType());
                        entityFetcher.resolve(embeddedField, context);
                        Object value = entityFetcher.targetObject();
                        MapperUtils.set(embeddedField, target, value);
                    }
                });
                fieldType = field.getType().getSuperclass();
            }
            while (fieldType != Object.class);
            MapperUtils.set(field, target, fieldValue);
        }
    }

    @Override
    public T targetObject() {
        return target;
    }
}
