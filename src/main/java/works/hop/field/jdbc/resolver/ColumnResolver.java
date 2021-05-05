package works.hop.field.jdbc.resolver;

import works.hop.field.jdbc.mapper.MapperUtils;

import java.lang.reflect.Field;
import java.sql.SQLException;

public class ColumnResolver<T> implements Resolver {

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
            MapperUtils.set(field, target, value);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public T targetObject() {
        return target;
    }
}
