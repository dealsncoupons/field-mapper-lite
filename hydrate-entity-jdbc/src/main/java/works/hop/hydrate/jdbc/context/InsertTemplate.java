package works.hop.hydrate.jdbc.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;

public class InsertTemplate {

    static final Logger log = LoggerFactory.getLogger(InsertTemplate.class);

    public static <E extends Hydrate> E insertOne(E entity) {
        Connection connection = null;
        try {
            connection = ConnectionProvider.getConnection();
            connection.setAutoCommit(false);
            E insertResult = entity.insert(connection);
            connection.commit();
            return insertResult;
        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception sqle) {
                    log.error("Failed to rollback transaction", sqle);
                }
            }
            throw new RuntimeException("Problem executing insert query", e);
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

    public static <E extends Hydrate> E insertOne(E entity, Connection connection) {
        try {
            return entity.insert(connection);
        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception sqle) {
                    log.error("Failed to rollback transaction", sqle);
                }
            }
            throw new RuntimeException("Problem executing insert query", e);
        }
    }
}
