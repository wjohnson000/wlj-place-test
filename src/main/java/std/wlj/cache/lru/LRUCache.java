package std.wlj.cache.lru;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import std.wlj.cache.lru.listener.EventType;

/**
 * Simple LRU cache: once the cache is full, the oldest (least recently used) entry will
 * be removed, thus keeping the cache at a specified maximum size.
 * <p/>
 * <strong>NOTE:</strong> the underlying implementation will result in changes to the
 * cache even for "get(...)" operations.  Thus the overhead of synchronization is usually
 * the best practice.
 * <p/>
 * <strong>NOTE:</strong> this cache is synchronized by default, making it usable in a
 * multi-threaded environment.  Synchronization can disabled via a constructor parameter,
 * but don't say we didn't warn you!
 * <p/>
 * 
 * @author wjohnson000
 *
 * @param <K> type of the KEY
 * @param <V> type of the VALUE
 */

public class LRUCache<K, V> extends CacheImplBase<K, V> {
    
    private final Map<K, V> cache;

    public LRUCache(String name, final int cacheSize) {
        this(name, cacheSize, true);
    }
    
    public LRUCache(String name, final int cacheSize, boolean isSynchronized) {
        super(name);

        Map<K, V> tCache = createCache(cacheSize);
        if (isSynchronized) {
            this.cache = Collections.synchronizedMap(tCache);
        } else {
            this.cache = tCache;
        }
    }

    public void put(K key, V value) {
        cacheStats.incrPutCount();
        V oldValue = cache.put(key, value);
        if (oldValue != null) {
            notifyListeners(EventType.UPDATED, key, oldValue, value);
        }
    }

    public V get(K key) {
        cacheStats.incrGetCount();
        return cache.get(key);
    }

    public V remove(K key) {
        cacheStats.incrRemoveCount();
        V value = cache.remove(key);
        if (value != null) {
            notifyListeners(EventType.REMOVED, key, value, null);
        }
        return value;
    }

    public void removeAll() {
        cache.clear();
        notifyListeners(EventType.REMOVE_ALL, null, null, null);
    }

    public int size() {
        return cache.size();
    }

    Map<K, V> createCache(final int cacheSize) {
        Map<K, V> tCache = new LinkedHashMap<K, V>(cacheSize, 0.75F, true) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                if (size() <= cacheSize) {
                    return false;
                } else {
                    cacheStats.incrDiscardCount();
                    notifyListeners(EventType.EVICTED, eldest.getKey(), eldest.getValue(), null);
                    return true;
                }
            }
        };
        return tCache;
    }
}
