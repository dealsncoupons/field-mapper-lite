// This change event handler is AUTO-GENERATED, so there's no point of modifying it
package works.hop.sample1.hydrate.entity.dispatcher;

import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.kafka.connect.data.Struct;
import works.hop.hydrate.jdbc.changes.ChangeConsumer;
import works.hop.hydrate.jdbc.changes.ChangeDispatcher;
import works.hop.hydrate.jdbc.context.LocalCache;

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
    this.dispatchers.add(new works.hop.sample1.hydrate.entity.dispatcher.MemberEventDispatcher());
    this.dispatchers.add(new works.hop.sample1.hydrate.entity.dispatcher.AccountEventDispatcher());
    this.dispatchers.add(new works.hop.sample1.hydrate.entity.dispatcher.ClubEventDispatcher());
    this.dispatchers.add(new works.hop.sample1.hydrate.entity.dispatcher.MembershipEventDispatcher());
    this.dispatchers.add(new works.hop.sample1.hydrate.entity.dispatcher.MyMembershipEventDispatcher());
    ChangeConsumer.getInstance().register(this);
  }
}
