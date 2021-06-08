package works.hop.hydrate.jdbc.context;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionProvider {

    private static final Logger log = LoggerFactory.getLogger(ConnectionProvider.class);

    private static final HikariConfig config = new HikariConfig();
    private static final HikariDataSource ds;

    static {
        Properties properties = connectionProperties();
        config.setJdbcUrl(properties.getProperty("database.jdbcUrl"));
        config.setUsername(properties.getProperty("database.username"));
        config.setPassword(properties.getProperty("database.password"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        ds = new HikariDataSource(config);
    }

    private static DataSource dataSource() {
        return ds;
    }

    public static Connection getConnection() throws SQLException {
        return ds.getConnection();
    }

    public static Properties connectionProperties() {
        Properties properties = new Properties();
        try {
            properties.load(ConnectionProvider.class.getResourceAsStream("/sql/connection.properties"));
        } catch (IOException e) {
            log.error("Failed to load properties from 'connection.properties' file. Default properties will be used instead");
            properties.setProperty("database.jdbcUrl", "jdbc:postgresql://localhost:5432/postgres");
            properties.setProperty("database.username", "postgres");
            properties.setProperty("database.password", "postgres");
        }
        return properties;
    }

    public static int fetchBatchSize() {
        return 2;
    }
}
