package works.hop.hydrate.jdbc.changes;

import org.apache.kafka.connect.data.Struct;
import works.hop.hydrate.jdbc.context.LocalCache;

public interface ChangeDispatcher {

    boolean canHandle(String source);

    void dispatch(Struct record, String source, String operation, LocalCache cache);
}
