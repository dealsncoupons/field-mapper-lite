package works.hop.javro.jdbc.sample;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.stream.Stream;

public class EntitySource<E> implements InvocationHandler {

    private final E entity;

    public EntitySource(E entity) {
        this.entity = entity;
    }

    private String setterMethod(String field) {
        char firstLetter = Character.toUpperCase(field.charAt(0));
        return "set" + firstLetter + field.substring(1);
    }

    private String getterMethod(String field) {
        char firstLetter = Character.toUpperCase(field.charAt(0));
        return "get" + firstLetter + field.substring(1);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("set")) {
            Class<?>[] setterArgs = Stream.of(args).map(Object::getClass).toArray(Class<?>[]::new);
            Method entityMethod = entity.getClass().getMethod(setterMethod(args[0].toString()), setterArgs[1]);
            return entityMethod.invoke(entity, args[1]);
        } else if (method.getName().equals("get")) {
            Method entityMethod = entity.getClass().getMethod(getterMethod(args[0].toString()));
            return entityMethod.invoke(entity);
        } else {
            Method entityMethod = entity.getClass().getMethod(method.getName());
            return entityMethod.invoke(entity, args);
        }
    }
}
