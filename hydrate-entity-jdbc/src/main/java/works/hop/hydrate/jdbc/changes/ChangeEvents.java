package works.hop.hydrate.jdbc.changes;

import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import io.debezium.engine.format.ChangeEventFormat;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ChangeEvents {

    private static final Logger log = LoggerFactory.getLogger(ChangeEvents.class);

    private static final Object lock = new Object();
    private static ChangeEvents instance;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private ChangeEvents() {
        super();
        try {
            initialize();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Could not initialize postgres connector", e);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                executor.shutdown();
                while (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    log.info("Waiting another 5 seconds for the embedded engine to shut down");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }));
    }

    public static ChangeEvents getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new ChangeEvents();
            }
        }
        return instance;
    }

    public static void main(String[] args) {
        new ChangeEvents();
    }

    public void initialize() throws IOException {
        // Define the configuration for the Debezium Engine with Postgres connector...
        Properties props = new Properties();
        props.setProperty("name", "engine");
        props.setProperty("plugin.name", "pgoutput");
        props.setProperty("connector.class", "io.debezium.connector.postgresql.PostgresConnector");
        props.setProperty("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore");
        props.setProperty("offset.storage.file.filename", "c:\\Users\\Mainas\\pgdata\\offsets.dat");
        props.setProperty("offset.flush.interval.ms", "60000");

        /* begin connector properties */
        props.setProperty("database.hostname", "localhost");
        props.setProperty("database.port", "5432");
        props.setProperty("database.user", "postgres");
        props.setProperty("database.password", "postgres");
        props.setProperty("database.dbname", "postgres");
        props.setProperty("database.server.name", "connector-app");
        props.setProperty("database.history", "io.debezium.relational.history.FileDatabaseHistory");
        props.setProperty("database.history.file.filename", "~/pgdata/dbhistory.dat");

        // Create the engine with this configuration ...
        try (DebeziumEngine<RecordChangeEvent<SourceRecord>> engine = DebeziumEngine
                .create(ChangeEventFormat.of(Connect.class))
                .using(props)
                .notifying(ChangeConsumer.getInstance()).build()
        ) {
            // Run the engine asynchronously ...
            executor.execute(engine);

            // Do something else or wait for a signal or an event
        }
        // Engine is stopped when the main code is finished
    }
}
