package works.hop.javro.jdbc.sample;

import java.lang.reflect.Proxy;
import java.util.Map;

public class EntityProxyFactory {

    public static <T> T create(Class<T> type, Map<String, Object> source) {
        return (T) Proxy.newProxyInstance(
                type.getClassLoader(),
                new Class[]{type},
                new EntityProxy(source));
    }
}
