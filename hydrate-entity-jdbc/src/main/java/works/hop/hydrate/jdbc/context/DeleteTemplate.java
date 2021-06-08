package works.hop.hydrate.jdbc.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class DeleteTemplate {

    private static final Logger log = LoggerFactory.getLogger(DeleteTemplate.class);

    public static <E extends Hydrate> E deleteOne(E entity) {
        Connection connection = null;
        try {
            connection = ConnectionProvider.getConnection();
            connection.setAutoCommit(false);
            E deleteResult = entity.delete(connection);
            connection.commit();
            return deleteResult;
        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception sqle) {
                    log.error("Failed to rollback transaction", sqle);
                }
            }
            throw new RuntimeException("Problem executing delete query", e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (Exception sqle) {
                    log.error("Failed to commit transaction", sqle);
                }
            }
        }
    }

    public static <E extends Hydrate> E deleteOne(E entity, Connection connection) {
        try {
            return entity.delete(connection);
        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception sqle) {
                    log.error("Failed to rollback transaction", sqle);
                }
            }
            throw new RuntimeException("Problem executing delete query", e);
        }
    }
}
