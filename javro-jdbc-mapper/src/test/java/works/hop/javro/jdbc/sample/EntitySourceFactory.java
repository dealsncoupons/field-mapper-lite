package works.hop.javro.jdbc.sample;

import java.lang.reflect.Proxy;
import java.util.Map;

public class EntitySourceFactory {

    public static <I extends Accessible, S> I create(S source) {
        return (I) Proxy.newProxyInstance(
                source.getClass().getClassLoader(),
                source.getClass().getInterfaces(),
                new EntitySource<>(source));
    }
}
