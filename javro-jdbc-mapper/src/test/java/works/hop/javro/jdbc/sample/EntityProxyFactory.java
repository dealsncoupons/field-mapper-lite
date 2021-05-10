package works.hop.javro.jdbc.sample;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class EntityProxyFactory {

    private static final Map<Class<?>, Class<?>> interfaceProxyMap = new HashMap<>();

    static {
        interfaceProxyMap.put(EntityInterface.class, EntityProxy.class);
    }

    public static <T> T create(Class<T> type, Map<String, Object> source){
        try {
            return (T) Proxy.newProxyInstance(
                    type.getClassLoader(),
                    new Class[]{type},
                    (InvocationHandler) interfaceProxyMap.get(type)
                            .getConstructor(Map.class).newInstance(source));
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not create proxy for " + type.getTypeName());
        }
    }
}
