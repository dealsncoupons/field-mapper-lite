package works.hop.upside.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;

public class SelectTemplate {

    private static final Logger log = LoggerFactory.getLogger(SelectTemplate.class);
    private static final LocalCache cache = LocalCache.getInstance();

    public static <E extends Hydrant> E selectOne(String query, Class<?> type, DbSelect resolver, Object[] args) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            return selectOne(query, type, resolver, args, connection);
        } catch (Exception e) {
            String errorMessage = "Problem executing fetch query";
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    public static <E extends Hydrant> E selectOne(String query, Class<?> type, DbSelect resolver, Object[] args, Connection conn) {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return EntityInstance.create(type).select(rs, resolver, conn, cache);
                }
                return null;
            }
        } catch (Exception e) {
            String errorMessage = "Problem executing fetch query";
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    public static <T extends Hydrant> Collection<T> selectList(String query, Class<?> type, DbSelect resolver, Object[] args) {
        try (Connection connection = ConnectionProvider.getConnection()) {
            return selectList(query, type, resolver, args, connection);
        } catch (Exception e) {
            String errorMessage = "Problem executing fetch query";
            log.error(errorMessage, e);
            throw new RuntimeException(errorMessage, e);
        }
    }

    public static <T extends Hydrant> Collection<T> selectList(String query, Class<?> type, DbSelect resolver, Object[] args, Connection conn) {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }

            Collection<T> result = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    T entity = EntityInstance.create(type).select(rs, resolver, conn, cache);
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
