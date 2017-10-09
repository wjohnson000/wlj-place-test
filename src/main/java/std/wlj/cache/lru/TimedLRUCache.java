package std.wlj.cache.lru;

import java.util.LinkedHashMap;
import java.util.Map;

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
 * 
 * @author wjohnson000
 *
 * @param <K> type of the KEY
 * @param <V> type of the VALUE
 */
public class TimedLRUCache<K, V> {

    public static final int DEFAULT_TTL_SECONDS = 10 * 60;

    public static enum ExpireType {
        USE_CREATE_TIME,
        USE_ACCESS_TIME
    }

    private static class CacheEntry<V> {
        long milliTime;
        V    value;

        private CacheEntry(V value) {
            this.value = value;
            this.milliTime = System.currentTimeMillis();
        }
    }

    private int        timeToLive;
    private ExpireType expireType;
    private final Map<K, CacheEntry<V>> cache;

    public TimedLRUCache(int cacheSize) {
        this(cacheSize, DEFAULT_TTL_SECONDS);
    }

    public TimedLRUCache(int cacheSize, int timeToLive) {
        this(cacheSize, timeToLive, ExpireType.USE_CREATE_TIME);
    }

    public TimedLRUCache(int cacheSize, int timeToLive, ExpireType expireType) {
        this.timeToLive = timeToLive;
        this.expireType = expireType;

        this.cache = new LinkedHashMap<K, CacheEntry<V>>(cacheSize, 0.75F, true) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheEntry<V>> eldest) {
                return size() > cacheSize;
            }
        };
    }

    public synchronized void put(K key, V value) {
        cache.put(key, new CacheEntry<V>(value));
    }

    public synchronized V get(K key) {
        CacheEntry<V> cacheValue = getOrExpire(key);
        updateAccessTime(cacheValue);
        return (cacheValue == null) ? null : cacheValue.value;
    }

    public synchronized V remove(K key) {
        CacheEntry<V> cacheValue = cache.remove(key);
        return (cacheValue == null) ? null : entryHasExpired(cacheValue) ? null : cacheValue.value;
    }

    public int size() {
        return cache.size();
    }
    
    CacheEntry<V> getOrExpire(K key) {
        CacheEntry<V> cacheValue = cache.get(key);
        if (cacheValue == null) {
            return null;
        } else if (entryHasExpired(cacheValue)) {
            cache.remove(key);
            return null;
        } else {
            return cacheValue;
        }
    }

    boolean entryHasExpired(CacheEntry<V> cacheValue) {
        long elapsed = System.currentTimeMillis() - cacheValue.milliTime;
        return (int)(elapsed / 1_000) > timeToLive;
    }

    void updateAccessTime(CacheEntry<V> cacheValue) {
        if (cacheValue != null  &&  expireType == ExpireType.USE_ACCESS_TIME) {
            cacheValue.milliTime = System.currentTimeMillis();
        }
    }
}
