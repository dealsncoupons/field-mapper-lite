package works.hop.javro.jdbc.sample;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class EntityProxy implements InvocationHandler {

    private final Map<String, Object> source;

    public EntityProxy(Map<String, Object> source) {
        this.source = source;
    }

    private String getName(String getter){
        char firstLetter = Character.toLowerCase(getter.charAt(3));
        return firstLetter + getter.substring(4);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return source.get(getName(method.getName()));
    }
}
