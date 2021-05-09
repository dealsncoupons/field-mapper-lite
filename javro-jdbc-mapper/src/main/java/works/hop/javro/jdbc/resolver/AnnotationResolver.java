package works.hop.javro.jdbc.resolver;

import java.lang.reflect.Field;

public interface AnnotationResolver {

    void resolve(Field field, ResolverContext context);

    <T> T targetObject();
}
