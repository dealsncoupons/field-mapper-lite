package works.hop.field.mapper;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

public class MapContainer<K, V> extends LinkedHashMap<K, V> {

    public Type[] genericType;

    public MapContainer(Type[] genericType) {
        this.genericType = genericType;
    }

    public MapContainer(Map<? extends K, ? extends V> m, Type[] genericType) {
        super(m);
        this.genericType = genericType;
    }
}
