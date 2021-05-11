package works.hop.javro.jdbc.sample.template;

import works.hop.javro.jdbc.sample.MapResultSetToEntity;
import works.hop.javro.jdbc.template.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;

public class SelectTemplate {

    public static <E> E selectOne(String query, Class<E> type, Object[] args) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    MapResultSetToEntity mapper = new MapResultSetToEntity();
                    return mapper.mapRsToEntity(rs, type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Problem executing fetch query", e);
        }
    }

    public static <E> Collection<E> selectList(String query, Class<E> type, Object[] args) {
        try (Connection conn = ConnectionProvider.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                for (int i = 0; i < args.length; i++) {
                    ps.setObject(i + 1, args[i]);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    MapResultSetToEntity mapper = new MapResultSetToEntity();
                    return mapper.mapRsToEntityCollection(rs, type);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Problem executing fetch query", e);
        }
    }
}
