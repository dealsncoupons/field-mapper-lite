package works.hop.javro.jdbc.sample;

import java.lang.reflect.Proxy;

public class EntitySourceFactory {

    public static <I extends Unreflect, S> I create(S source) {
        return (I) Proxy.newProxyInstance(
                source.getClass().getClassLoader(),
                source.getClass().getInterfaces(),
                new EntitySource<>(source));
    }
}
