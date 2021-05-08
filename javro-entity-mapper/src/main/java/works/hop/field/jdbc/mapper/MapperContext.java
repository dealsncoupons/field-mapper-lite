package works.hop.field.jdbc.mapper;

import java.util.HashMap;
import java.util.Map;

public class MapperContext extends HashMap<String, Object> {

    public final String key;
    public final Map<String, MapperFunction<?, ?>> resolvers = new HashMap<>();

    public MapperContext(String key) {
        this.key = key;
    }

    public MapperContext resolveContext(String parent, String source) {
        if (parent == null || parent.trim().length() == 0) {
            return (MapperContext) this.get(source);
        } else {
            MapperContext context = (MapperContext) this.get(parent);
            if (context != null) {
                return (MapperContext) context.get(source);
            }
        }
        return null;
    }

    public String resolveAtoB(String parent, String source) {
        MapperContext resolved = resolveContext(parent, source);
        return resolved != null ? resolved.key : source;
    }

    public Object resolveAToB(String parent, String source, Object input) {
        MapperContext resolved = resolveContext(parent, source);
        if (resolved != null) {
            if (resolved.get(resolved.key) != null) {
                MapperContext resolverContext = (MapperContext) resolved.get(resolved.key);
                if (resolverContext.resolvers != null && !resolverContext.resolvers.isEmpty()) {
                    return ((MapperFunction) resolverContext.resolvers.get(source)).apply(input);
                }
            }
        }
        return input;
    }
}
