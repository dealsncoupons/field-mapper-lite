package works.hop.javro.jdbc.template;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public interface QueryExecutor {

    Logger log = LoggerFactory.getLogger(QueryExecutor.class);

    default void executeUpdate(String query, Object[] args) {
        Connection conn = null;
        try {
            conn = ConnectionProvider.getConnection();
            conn.setAutoCommit(false);
            executeQuery(conn, query, args);
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException sqle) {
                    log.error("Failed to rollback transaction", sqle);
                }
            }
            throw new RuntimeException("Problem executing delete query", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException sqle) {
                    log.error("Failed to commit transaction", sqle);
                }
            }
        }
    }

    default void executeQuery(Connection conn, String query, Object[] args) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            AtomicInteger index = new AtomicInteger(1);
            for (Object param : args) {
                ps.setObject(index.getAndIncrement(), param);
            }
            int rowsAffected = ps.executeUpdate();
            log.info("{} row(s) affected after delete operation", rowsAffected);
        }
    }
}
