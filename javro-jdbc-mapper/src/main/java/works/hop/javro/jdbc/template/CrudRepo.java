package works.hop.javro.jdbc.template;

import works.hop.javro.jdbc.reflect.ReflectionUtil;
import works.hop.javro.jdbc.resolver.AbstractResolver;

import java.lang.reflect.ParameterizedType;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static works.hop.javro.jdbc.reflect.ReflectionUtil.getIdColumns;
import static works.hop.javro.jdbc.reflect.ReflectionUtil.getTableName;

public interface CrudRepo<T, I> {

    default Class<? extends CrudRepo<T, I>> getInstanceType() {
        //override in implementation
        return null;
    }

    default T save(T entity) {
        return InsertTemplate.insertOne(entity);
    }

    default T update(T entity) {
        //TODO: implement this
        return null;
    }

    default T deleteById(I id) {
        //TODO: implement this
        return null;
    }

    default int delete(T entity) {
        //TODO: implement this
        return -1;
    }

    default Optional<T> findById(I id) {
        ParameterizedType genericSuperclass = (ParameterizedType) getInstanceType().getGenericInterfaces()[0];
        return findById(id, (Class<T>) genericSuperclass.getActualTypeArguments()[0]);
    }

    default Optional<T> findById(I id, Class<T> type) {
        String query = findByIdQuery(type);
        return Optional.ofNullable(SelectTemplate.selectOne(query, rs -> {
            AbstractResolver<T> entityFetcher = ReflectionUtil.resolverInstance(type);
            entityFetcher.resolve(null, AbstractResolver.createContext(rs));
            return entityFetcher.targetObject();
        }, new Object[]{id}));
    }

    default String findByIdQuery(Class<?> entityType) {
        String tableName = getTableName(entityType);
        List<String> idColumns = getIdColumns(entityType);
        String idCriteria = idColumns.stream()
                .map(id -> String.format("%s = ?::uuid", id))
                .collect(Collectors.joining(" and "));
        return String.format("select * from %s where %s", tableName, idCriteria);
    }

    default <V> Optional<T> findByUnique(String columnName, V value) {
        ParameterizedType genericSuperclass = (ParameterizedType) getInstanceType().getGenericInterfaces()[0];
        return findByUnique(columnName, value, (Class<T>) genericSuperclass.getActualTypeArguments()[0]);
    }

    default <V> Optional<T> findByUnique(String columnName, V value, Class<T> type) {
        String query = findByUniqueQuery(type, columnName);
        return Optional.ofNullable(SelectTemplate.selectOne(query, rs -> {
            AbstractResolver<T> entityFetcher = ReflectionUtil.resolverInstance(type);
            entityFetcher.resolve(null, AbstractResolver.createContext(rs));
            return entityFetcher.targetObject();
        }, new Object[]{value}));
    }

    default String findByUniqueQuery(Class<?> entityType, String columnName) {
        String tableName = getTableName(entityType);
        return String.format("select * from %s where %s = ?", tableName, columnName);
    }

    default List<T> findAll(int offset, int limit) {
        ParameterizedType genericSuperclass = (ParameterizedType) getInstanceType().getGenericInterfaces()[0];
        return findAll(offset, limit, (Class<T>) genericSuperclass.getActualTypeArguments()[0]);
    }

    default List<T> findAll(int offset, int limit, Class<?> type) {
        String query = findAllQuery(type, offset, limit);
        return (List<T>) SelectTemplate.selectList(query, (Function<ResultSet, Object>) rs -> {
            AbstractResolver<?> entityFetcher = ReflectionUtil.resolverInstance(type);
            entityFetcher.resolve(null, AbstractResolver.createContext(rs));
            return entityFetcher.targetObject();
        }, new Object[]{offset, limit});
    }

    default String findAllQuery(Class<?> entityType, int offset, int limit) {
        String tableName = getTableName(entityType);
        return String.format("select * from %s offset %d limit %d", tableName, offset, limit);
    }
}