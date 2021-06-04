package works.hop.upside.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.Map;

public class UpdateTemplate {

    static final Logger log = LoggerFactory.getLogger(UpdateTemplate.class);

    public static <E extends Hydrate> E updateOne(E entity, Map<String, Object> columnValues) {
        Connection connection = null;
        try {
            connection = ConnectionProvider.getConnection();
            connection.setAutoCommit(false);
            E updateResult = entity.update(columnValues, connection);
            connection.commit();
            return updateResult;
        } catch (Exception e) {
            e.printStackTrace();
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (Exception sqle) {
                    log.error("Failed to rollback transaction", sqle);
                }
            }
            throw new RuntimeException("Problem executing update query", e);
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

    public static <E extends Hydrate> E updateOne(E entity, Map<String, Object> columnValues, Connection connection) {
        try {
            return entity.update(columnValues, connection);
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
