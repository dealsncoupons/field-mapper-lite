// This change event handler is AUTO-GENERATED, so there's no point of modifying it
package works.hop.upside.entity.dispatcher;

import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.kafka.connect.data.Struct;
import works.hop.upside.context.LocalCache;

public class ChangeDispatcherChain implements ChangeDispatcher {
  private static final Object lock = new Object();

  private static ChangeDispatcherChain instance;

  private final Collection<ChangeDispatcher> dispatchers;

  private ChangeDispatcherChain() {
    this.dispatchers = new ArrayList<>();
    this.initialize();
  }

  public static ChangeDispatcherChain chain() {
    synchronized (lock) {
            if (instance == null) {
                instance = new ChangeDispatcherChain();
            }
        }
        return instance;
  }

  @Override
  public boolean canHandle(String source) {
    return true;
  }

  @Override
  public void dispatch(Struct record, String source, String operation, LocalCache cache) {
     for (ChangeDispatcher changeDispatcher : dispatchers) {
            if (changeDispatcher.canHandle(source)) {
                changeDispatcher.dispatch(record, source, operation, cache);
                break;
            }
        };
  }

  public void initialize() {
    this.dispatchers.add(new works.hop.upside.entity.dispatcher.AccountEventDispatcher());
    this.dispatchers.add(new works.hop.upside.entity.dispatcher.AssignmentEventDispatcher());
    this.dispatchers.add(new works.hop.upside.entity.dispatcher.TaskEventDispatcher());
    this.dispatchers.add(new works.hop.upside.entity.dispatcher.UserEventDispatcher());
  }
}
