package works.hop.hydrate.jdbc.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

public class SelectTemplate {

    private static final Logger log = LoggerFactory.getLogger(SelectTemplate.class);

    public static <E extends Hydrate> E selectOne(E instance, String query, DbSelector resolver, Object[] args) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            return selectOne(instance, query, resolver, args, connection);
        } catch (Exception e) {
            String errorMessage = "Problem executing fetch query";
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    public static <E extends Hydrate> E selectOne(E instance, String query, DbSelector resolver, Object[] args, Connection conn) {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return instance.select(rs, resolver, conn, LocalCache.getInstance());
                }
                throw new Exception("No entity was found with given criteria.");
            }
        } catch (Exception e) {
            String errorMessage = "Problem executing fetch query";
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    public static <T extends Hydrate> Collection<T> selectList(Supplier<T> newInstance, String query, DbSelector resolver, Object[] args) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            return selectList(newInstance, query, resolver, args, connection);
        } catch (Exception e) {
            String errorMessage = "Problem executing fetch query";
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    public static <T extends Hydrate> Collection<T> selectList(Supplier<T> newInstance, String query, DbSelector resolver, Object[] args, Connection conn) {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            Collection<T> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    T entity = newInstance.get().select(rs, resolver, conn, LocalCache.getInstance());
                    result.add(entity);
                }
                return result;
            }
        } catch (Exception e) {
            String errorMessage = "Problem executing fetch query";
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }
}
