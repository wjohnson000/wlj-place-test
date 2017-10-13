package std.wlj.cache.lru;

import java.util.ArrayList;
import java.util.List;

import std.wlj.cache.lru.listener.CacheEventListener;
import std.wlj.cache.lru.listener.EventType;

/**
 * Partial implementation of a basic cache with listeners.
 * 
 * @author wjohnson000
 *
 * @param <K> type of the KEY
 * @param <V> type of the VALUE
 */
public abstract class CacheImplBase<K, V> implements Cache<K, V> {

    private String name;
    private final List<CacheEventListener<K, V>> listeners = new ArrayList<>();
    protected final CacheStats cacheStats = new CacheStats();

    public CacheImplBase(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public CacheStats getStats() {
        return cacheStats;
    }

    @Override
    public void addListener(CacheEventListener<K, V> listener) {
        if (! listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    @Override
    public void removeListener(CacheEventListener<K, V> listener) {
        listeners.remove(listener);
    }

    protected void notifyListeners(EventType type, K key, V oldValue, V newValue) {
        for (CacheEventListener<K, V> listener : listeners) {
            switch(type) {
                case EVICTED:
                    listener.notifyElementEvicted(key, oldValue);  break;
                case EXPIRED:
                    listener.notifyElementExpired(key, oldValue);  break;
                case REMOVED:
                    listener.notifyElementRemoved(key, oldValue);  break;
                case REMOVE_ALL:
                    listener.notifyElementRemoveAll();  break;
                case UPDATED:
                    listener.notifyElementUpdated(key, oldValue, newValue);  break;
            }
        }
    }
}
