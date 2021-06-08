package works.hop.field.mapper;

public class MapperBuilder {

    private final MapperContext context;

    private MapperBuilder(String key) {
        this.context = new MapperContext(key);
    }

    public static MapperBuilder newBuilder(String key) {
        return new MapperBuilder(key);
    }

    public MapperBuilder mapAToB(String source, String target) {
        MapperContext newContext = new MapperContext(target);
        this.context.put(source, newContext);
        return this;
    }

    public MapperBuilder mapAToBResolver(String source, String target, MapperFunction<Object, Object> resolver) {
        MapperContext mapperContext = (MapperContext) this.context.get(source);
        MapperContext targetContext = (MapperContext) mapperContext.get(target);
        if (targetContext != null) {
            targetContext.resolvers.put(source, resolver);
        } else {
            MapperContext newContext = new MapperContext(target);
            newContext.resolvers.put(source, resolver);
            mapperContext.put(target, newContext);
        }
        return this;
    }

    public MapperBuilder mapAToB(String source, String target, BuilderFunction builder) {
        MapperContext mapperContext = (MapperContext) this.context.get(source);
        MapperContext newMapperContext = builder.apply(new MapperBuilder(target));
        mapperContext.put(target, newMapperContext);
        return this;
    }

    public MapperContext build() {
        return this.context;
    }
}
