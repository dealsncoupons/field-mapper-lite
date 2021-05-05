package works.hop.field.data.fetcher;

import java.util.HashMap;

public class FetchCache extends HashMap<Object, Object> {

    public <K, V> V putValue(K key, V value){
        return (V) this.put(key, value);
    }

    public <K, V> V getValue(K key){
        return this.getValue(key);
    }
}
