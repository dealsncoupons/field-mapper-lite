package works.hop.javro.jdbc.resolver;

import works.hop.javro.jdbc.reflect.ReflectionUtil;

import java.lang.reflect.Field;
import java.sql.SQLException;

public class ColumnResolver<T> implements AnnotationResolver {

    final T target;
    final String columnName;

    public ColumnResolver(T target, String columnName) {
        this.target = target;
        this.columnName = columnName;
    }

    @Override
    public void resolve(Field field, ResolverContext context) {
        try {
            Object value = context.getResultSet().getObject(columnName, field.getType());
            ReflectionUtil.set(field, target, value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public T targetObject() {
        return target;
    }
}
