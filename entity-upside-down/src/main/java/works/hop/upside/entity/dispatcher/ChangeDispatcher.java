package works.hop.upside.entity.dispatcher;

import org.apache.kafka.connect.data.Struct;
import works.hop.upside.context.LocalCache;

public interface ChangeDispatcher {

    boolean canHandle(String source);

    void dispatch(Struct record, String source, String operation, LocalCache cache);
}
