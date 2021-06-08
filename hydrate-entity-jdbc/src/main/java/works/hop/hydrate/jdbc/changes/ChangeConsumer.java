package works.hop.hydrate.jdbc.changes;

import io.debezium.data.Envelope;
import io.debezium.engine.DebeziumEngine;
import io.debezium.engine.RecordChangeEvent;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import works.hop.hydrate.jdbc.context.LocalCache;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.debezium.data.Envelope.FieldName.*;

public class ChangeConsumer implements DebeziumEngine.ChangeConsumer<RecordChangeEvent<SourceRecord>> {

    private static final Logger log = LoggerFactory.getLogger(ChangeConsumer.class);

    private static final Object lock = new Object();
    private static ChangeConsumer instance;

    private final Set<ChangeDispatcher> listeners;

    private ChangeConsumer() {
        this.listeners = new HashSet<>();
    }

    public static ChangeConsumer getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new ChangeConsumer();
            }
            return instance;
        }
    }

    public void register(ChangeDispatcher dispatcher) {
        if (this.listeners.add(dispatcher)) {
            log.info("Registered a change dispatcher with the change consumer successfully");
        }
    }

    public void handleBatch(List<RecordChangeEvent<SourceRecord>> records, DebeziumEngine.RecordCommitter<RecordChangeEvent<SourceRecord>> committer) throws InterruptedException {
        for (RecordChangeEvent<SourceRecord> event : records) {
            log.info("Event key = {}", event.record().key());

            Struct sourceRecordValue = (Struct) event.record().value();
            if (sourceRecordValue != null) {
                Envelope.Operation operation = Envelope.Operation.forCode((String) sourceRecordValue.get(OPERATION));
                log.info("Event type - {}", operation.name());

                Struct before = (Struct) sourceRecordValue.get(BEFORE);
                log.info("Event payload (before) - {}", before);

                Struct after = (Struct) sourceRecordValue.get(AFTER);
                log.info("Event payload (after) - {}", after);

                Struct source = (Struct) sourceRecordValue.get(SOURCE);
                log.info("Event source - {}", source);

                String table = source.getString("table");

                listeners.forEach(listener -> listener.dispatch(after, table, operation.name(), LocalCache.getInstance()));
                committer.markProcessed(event);
            }
        }
    }
}
