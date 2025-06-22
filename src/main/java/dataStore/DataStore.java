package dataStore;

import java.util.Map;

public interface DataStore<K,V> {

    V set( K k , V v);

    V get(K k);

    Iterable<K> getKeys();

    int size();
}
