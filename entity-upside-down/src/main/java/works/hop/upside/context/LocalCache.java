package works.hop.upside.context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LocalCache {

    private static final Logger log = LoggerFactory.getLogger(LocalCache.class);

    private static final Object lock = new Object();
    private static LocalCache instance;
    private final Map<String, Map<String, Hydrate>> localCache = new ConcurrentHashMap<>();

    private LocalCache() {
        super();
    }

    public static LocalCache getInstance() {
        synchronized (lock) {
            if (instance == null) {
                instance = new LocalCache();
            }
        }
        return instance;
    }

    public <E extends Hydrate> Optional<E> get(UUID idValue, String tableName) {
        if (this.localCache.containsKey(tableName)) {
            if (localCache.get(tableName).containsKey(idValue.toString())) {
                return Optional.of((E) localCache.get(tableName).get(idValue.toString()));
            }
        }
        return Optional.empty();
    }

    public <E extends Hydrate> void add(UUID idValue, E entity, String tableName) {
        if (this.localCache.containsKey(tableName)) {
            this.localCache.get(tableName).put(idValue.toString(), entity);
        } else {
            log.warn("There does not exist a cache for the table '{}'. A new one will be created", tableName);
            Map<String, Hydrate> newTableCache = new ConcurrentHashMap<>();
            newTableCache.put(idValue.toString(), entity);
            this.localCache.put(tableName, newTableCache);
        }
    }
}
