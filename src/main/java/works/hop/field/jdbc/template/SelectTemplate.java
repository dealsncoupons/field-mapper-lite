package works.hop.field.jdbc.template;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
        } catch (SQLException e) {
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
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Problem executing fetch query", e);
        }
    }
}
