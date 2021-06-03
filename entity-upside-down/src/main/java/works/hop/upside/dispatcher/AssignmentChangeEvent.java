package works.hop.upside.dispatcher;

import org.apache.kafka.connect.data.Struct;
import works.hop.upside.context.LocalCache;

import java.util.UUID;

public class AssignmentChangeEvent implements ChangeDispatcher {

    @Override
    public boolean canHandle(String source) {
        String table = "tbl_assignment";
        return table.equals(source);
    }

    @Override
    public void dispatch(Struct record, String source, LocalCache cache) {
        cache.get(UUID.fromString(record.getString("id")), source).ifPresent(entity -> entity.refresh(record));
    }
}
