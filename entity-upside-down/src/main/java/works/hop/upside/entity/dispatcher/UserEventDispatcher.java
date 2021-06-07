// This change event handler is AUTO-GENERATED, so there's no point of modifying it
package works.hop.upside.entity.dispatcher;

import java.lang.Override;
import java.lang.String;
import java.util.UUID;
import org.apache.kafka.connect.data.Struct;
import works.hop.upside.context.LocalCache;

public class UserEventDispatcher implements ChangeDispatcher {
  @Override
  public boolean canHandle(String source) {
    String table = "tbl_user";
    return table.equals(source);
  }

  @Override
  public void dispatch(Struct record, String source, String operation, LocalCache cache) {
     if(record != null) {
      cache.get(UUID.fromString(record.getString("id")), source).ifPresent(entity -> entity.refresh(record));
    }
  }
}
