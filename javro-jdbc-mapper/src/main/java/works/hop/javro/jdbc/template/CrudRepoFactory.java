package works.hop.javro.jdbc.template;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class CrudRepoFactory {

    private static final Map<Class<?>, CrudRepo<?, ?>> repoInstances = new HashMap<>();

    public static <R extends CrudRepo<?, ?>> R getInstance(Class<R> type) {
        if (repoInstances.containsKey(type)) {
            return (R) repoInstances.get(type);
        }
        return (R) Proxy.newProxyInstance(CrudRepoFactory.class.getClassLoader(),
                new Class[]{type},
                new CrudRepoHandler(type));
    }
}
