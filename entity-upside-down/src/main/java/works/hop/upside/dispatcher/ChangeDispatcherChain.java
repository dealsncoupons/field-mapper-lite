package works.hop.upside.dispatcher;

import org.apache.kafka.connect.data.Struct;
import works.hop.upside.context.LocalCache;

import java.util.ArrayList;
import java.util.List;

public class ChangeDispatcherChain implements ChangeDispatcher {

    private static final Object lock = new Object();
    private static ChangeDispatcherChain chain;

    private final List<ChangeDispatcher> dispatchers;

    private ChangeDispatcherChain() {
        this.dispatchers = new ArrayList<>();
        this.initialize();
    }

    public static ChangeDispatcherChain chain() {
        synchronized (lock) {
            if (chain == null) {
                chain = new ChangeDispatcherChain();
            }
        }
        return chain;
    }

    public void initialize() {
        this.dispatchers.add(new AccountChangeEvent());
        this.dispatchers.add(new TaskChangeEvent());
        this.dispatchers.add(new UserChangeEvent());
        this.dispatchers.add(new AssignmentChangeEvent());
    }

    @Override
    public boolean canHandle(String source) {
        return true;
    }

    @Override
    public void dispatch(Struct record, String source, LocalCache cache) {
        for (ChangeDispatcher changeDispatcher : dispatchers) {
            if (changeDispatcher.canHandle(source)) {
                changeDispatcher.dispatch(record, source, cache);
                break;
            }
        }
    }
}
