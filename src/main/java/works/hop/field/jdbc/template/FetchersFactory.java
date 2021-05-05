package works.hop.field.jdbc.template;

import works.hop.field.jdbc.resolver.AbstractResolver;

import java.lang.reflect.InvocationTargetException;

public class FetchersFactory {


    public static <T> AbstractResolver<T> entityFetcher(Class<T> entityType) {
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
}
