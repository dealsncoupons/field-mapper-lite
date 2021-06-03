package works.hop.javro.jdbc.template.toremove;

import works.hop.javro.jdbc.reflect.ReflectionUtil;
import works.hop.javro.jdbc.resolver.AbstractResolver;
import works.hop.javro.jdbc.template.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SelectTemplate {

    public static <E> E selectOne(String query, Function<ResultSet, E> handler, Object[] args) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return handler.apply(rs);
                    } else {
                        throw new RuntimeException("Expected 1 but found 0 records");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Problem executing fetch query", e);
        }
    }

    public static <E> List<E> selectList(String query, Function<ResultSet, E> handler, Object[] args) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    List<E> entities = new ArrayList<>();
                    while (rs.next()) {
                        entities.add(handler.apply(rs));
                    }
                    return entities;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Problem executing fetch query", e);
        }
    }

    public static <E> E executeQuery(String query, Object[] args, Class<?> resultType) {
        return selectOne(query, rs -> {
            AbstractResolver<E> entityFetcher = (AbstractResolver<E>) ReflectionUtil.resolverInstance(resultType);
            entityFetcher.resolve(null, AbstractResolver.createContext(rs));
            return entityFetcher.targetObject();
        }, args);
    }

    public static <E> List<E> executeQueryList(String query, Object[] args, Class<?> resultType) {
        return selectList(query, rs -> {
            AbstractResolver<E> entityFetcher = (AbstractResolver<E>) ReflectionUtil.resolverInstance(resultType);
            entityFetcher.resolve(null, AbstractResolver.createContext(rs));
            return entityFetcher.targetObject();
        }, args);
    }
}
