package std.wlj.cache.lru;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import std.wlj.cache.lru.listener.EventType;

/**
 * Simple LRU cache with an expiry time.  Entries can thus be removed either of two ways:
 * <ul>
 *   <li>LRU, i.e., it is the oldest entry and a new value is being added</li>
 *   <li>Stale, i.e., it has been in the cache for too long</li>
 * </ul>
 * Additionally there is a parameter that controls whether an entry expires based on its
 * 'create' time, or based on its 'last-access' time.
 * </p>
 * The default "Time-to-live" is 10 minutes (600 seconds), which can be overridden via a
 * constructor parameter.
 * </p>
 * The default {@link ExpireType} is "USE_CREATE_TIME", i.e., the entry will expire at a
 * specific time based on when it was created, regardless of how many times it was accessed.
 * The other option is "USE_ACCESS_TIME", in which case each subsequent access of an
 * entry will reset the expire time.  This value can be overridden via a constructor
 * parameter. 
 * </p>
 * <strong>NOTE:</strong> the underlying implementation will result in changes to the
 * cache even for "get(...)" operations.  Thus the overhead of synchronization is usually
 * the best practice.
 * <p/>
 * <strong>NOTE:</strong> this cache is synchronized by default, making it usable in a
 * multi-threaded environment.  Synchronization can be disabled via a constructor parameter,
 * but don't say we didn't warn you!
 * <p/>
 * 
 * @author wjohnson000
 *
 * @param <K> type of the KEY
 * @param <V> type of the VALUE
 */
public class ExpiringLRUCache<K, V> extends CacheImplBase<K, V> {

    public static final int DEFAULT_TTL_SECONDS = 10 * 60;

    private int timeToLive;
    private ExpireType expireType;
    private final Map<K, CacheEntry<V>> cache;

    public ExpiringLRUCache(String name, int cacheSize) {
        this(name, cacheSize, DEFAULT_TTL_SECONDS);
    }

    public ExpiringLRUCache(String name, int cacheSize, int timeToLive) {
        this(name, cacheSize, timeToLive, ExpireType.USE_CREATE_TIME, true);
    }

    public ExpiringLRUCache(String name, int cacheSize, int timeToLive, ExpireType expireType, boolean isSynchronized) {
        super(name);

        this.timeToLive = timeToLive;
        this.expireType = expireType;

        Map<K, CacheEntry<V>> tCache = createCache(cacheSize);
        if (isSynchronized) {
            this.cache = Collections.synchronizedMap(tCache);
        } else {
            this.cache = tCache;
        }
    }

    public void put(K key, V value) {
        cacheStats.incrPutCount();
        CacheEntry<V> newValue = new CacheEntry<V>(value);
        CacheEntry<V> oldValue = cache.put(key, newValue);
        if (oldValue != null) {
            notifyListenersUnwrap(EventType.UPDATED, key, oldValue, newValue);
        }
    }

    public V get(K key) {
        cacheStats.incrGetCount();
        CacheEntry<V> cacheEntry = cache.get(key);
        if (cacheEntry == null) {
            return null;
        } else if (cacheEntry.isExpired(timeToLive)) {
            cacheStats.incrExpireCount();
            cache.remove(key);
            notifyListenersUnwrap(EventType.EXPIRED, key, cacheEntry, null);
            return null;
        } else {
            cacheStats.incrHitCount();
            if (expireType == ExpireType.USE_ACCESS_TIME) {
                cacheEntry.updateAccessTime();
            }
            return cacheEntry.getValue();
        }
    }

    public V remove(K key) {
        cacheStats.incrRemoveCount();
        CacheEntry<V> cacheEntry = cache.remove(key);
        if (cacheEntry == null) {
            return null;
        } else if (cacheEntry.isExpired(timeToLive)) {
            notifyListenersUnwrap(EventType.EXPIRED, key, cacheEntry, null);
            return null;
        } else {
            notifyListenersUnwrap(EventType.REMOVED, key, cacheEntry, null);
            return cacheEntry.getValue();
        }
    }

    public void removeAll() {
        cache.clear();
        notifyListenersUnwrap(EventType.REMOVE_ALL, null, null, null);
    }

    public int size() {
        return cache.size();
    }

    /**
     * Create the initial cache based on a {@LinkedHashMap}, evicting the oldest entry once
     * the maximum size has been reached.
     * 
     * @param cacheSize maximum cache size
     * @return
     */
    Map<K, CacheEntry<V>> createCache(final int cacheSize) {
        Map<K, CacheEntry<V>> tCache = new LinkedHashMap<K, CacheEntry<V>>(cacheSize, 0.75F, true) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
                if (size() <= cacheSize) {
                    return false;
                } else {
                    cacheStats.incrEvictCount();
                    notifyListenersUnwrap(EventType.EVICTED, eldest.getKey(), eldest.getValue(), null);
                    return true;
                }
            }
        };
        return tCache;
    }

    void notifyListenersUnwrap(EventType type, K key, CacheEntry<V> oldValue, CacheEntry<V> newValue) {
        V oldV = (oldValue == null) ? null : oldValue.getValue();
        V newV = (newValue == null) ? null : newValue.getValue();
        super.notifyListeners(type, key, oldV, newV);
    }
}
