package works.hop.field.jdbc.resolver;

import java.lang.reflect.Field;

public interface Resolver {

    void resolve(Field field, ResolverContext context);

    <T> T targetObject();
}
