package works.hop.field.jdbc.resolver;

import works.hop.field.jdbc.annotation.JoinColumn;
import works.hop.field.jdbc.annotation.Table;
import works.hop.field.jdbc.mapper.MapperUtils;
import works.hop.field.jdbc.template.FetchersFactory;
import works.hop.field.jdbc.template.SelectTemplate;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class JoinColumnResolver<T> implements Resolver {

    final T target;
    final String columnName;

    public JoinColumnResolver(T target, String columnName) {
        this.target = target;
        this.columnName = columnName;
    }

    @Override
    public void resolve(Field field, ResolverContext context) {
        Class<?> entityClass = field.getType();

        if (Stream.of(List.class, Set.class, Queue.class).anyMatch(clazz -> clazz.isAssignableFrom(entityClass))) {
            try {
                Class<?> genericClass = Class.forName(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName());
                String pkTable = Optional.of(genericClass.getAnnotation(Table.class).value())
                        .orElse(genericClass.getSimpleName().toLowerCase());

                String fkTable = null;
                if (field.isAnnotationPresent(JoinColumn.class)) {
                    fkTable = field.getAnnotation(JoinColumn.class).fkTable();
                }
                if (fkTable == null || fkTable.length() == 0) {
                    fkTable = genericClass.isAnnotationPresent(Table.class) ?
                            genericClass.getAnnotation(Table.class).value() :
                            genericClass.getSimpleName().toLowerCase();
                }

                UUID entityId = UUID.fromString(context.getResultSet().getString(columnName));
                List<?> entityValue = SelectTemplate.selectList(String.format(
                        "select pk_table.* from %s pk_table left join %s fk_table on pk_table.id = fk_table.%s where fk_table.%s = ?::uuid", pkTable, fkTable, columnName, columnName),
                        (Function<ResultSet, Object>) rs -> {
                            AbstractResolver<?> entityFetcher = FetchersFactory.entityFetcher(genericClass);
                            entityFetcher.resolve(null, AbstractResolver.createContext(rs));
                            return entityFetcher.targetObject();
                        }, new Object[]{entityId});
                //set value
                MapperUtils.set(field, target, entityValue);
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            String tableName = field.getName();
            if (entityClass.isAnnotationPresent(Table.class)) {
                tableName = entityClass.getAnnotation(Table.class).value();
            }

            try {
                UUID entityId = UUID.fromString(context.getResultSet().getString(columnName));
                Object entityValue = SelectTemplate.selectOne(
                        String.format("select * from %s where id = ?", tableName),
                        (Function<ResultSet, Object>) rs -> {
                            AbstractResolver<?> entityFetcher = FetchersFactory.entityFetcher(entityClass);
                            entityFetcher.resolve(null, AbstractResolver.createContext(rs));
                            return entityFetcher.targetObject();
                        }, new Object[]{entityId});
                //set value
                MapperUtils.set(field, target, entityValue);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public T targetObject() {
        return target;
    }
}
