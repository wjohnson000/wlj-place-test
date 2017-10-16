package std.wlj.cache.lru;

import java.util.ArrayList;
import java.util.List;

import std.wlj.cache.lru.listener.CacheEventListener;

/**
 * Builder that will create the best type of cache based on the input parameters:
 * <ul>
 *   <li><strong>LRUCache</strong> - if no 'expireType' is specified</li>
 *   <li><strong>ExpiringLRUCache</strong> - if 'expireType' is non-null</li>
 * </ul>
 * 
 * <strong>NOTE: </strong>If no 'maxSize' value is set, the default of 100 will be used.
 * If no 'timeToLive' is set, the default of 600 (seconds) will be used.
 * <p/>
 * 
 * <strong>NOTE: </strong>If a 'timeToLive' value is set, but 'expireType' is not,
 * the 'timeToLive' value will be ignored, and no warning or error will be issued.
 * <p/>
 * 
 * @author wjohnson000
 *
 * @param <K> type of the KEY
 * @param <V> type of the VALUE
 */
public class CacheBuilder<K, V> {

    public static final int DEFAULT_MAX_SIZE    = 100;
    public static final int DEFAULT_TTL_SECONDS = 10 * 60;

    private String  name;
    private boolean isSynchronized = false;
    private int     maxSize = DEFAULT_MAX_SIZE;
    private int     timeToLive = DEFAULT_TTL_SECONDS;
    private ExpireType expireType = null;
    private List<CacheEventListener<K, V>> listeners = new ArrayList<>();

    /**
     * @param name cache name, simply a convenient way for clients to distinguish
     * between multiple caches.  It is allowed to be null.
     * 
     * @return current cache builder
     */
    public CacheBuilder<K, V> setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * @param isSynchronized enable synchronization of the cache, which is recommended
     * for high-volume, multi-threaded usage.
     * 
     * @return current cache builder
     */
    public CacheBuilder<K, V> setSynchronized(boolean isSynchronized) {
        this.isSynchronized = isSynchronized;
        return this;
    }

    /**
     * @param maxSize maximum number of elements that the cache will hold.  If this
     * number is exceeded, the least-recently-used element will be thrown out.
     * 
     * @return current cache builder
     */
    public CacheBuilder<K, V> setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    /**
     * @param timeToLive maximum time in seconds that an element can live in the cache.
     * 
     * @return current cache builder
     */
    public CacheBuilder<K, V> setTimeToLiveInSeconds(int timeToLive) {
        this.timeToLive = timeToLive;
        return this;
    }

    /**
     * @param expireType the expiration type: whether the "time-to-live" is based on the
     * element creation time, or the last access time.
     * 
     * @return current cache builder
     */
    public CacheBuilder<K, V> setExpireType(ExpireType expireType) {
        this.expireType = expireType;
        return this;
    }

    /**
     * @param listener cache event listener.
     * 
     * @return current cache builder
     */
    public CacheBuilder<K, V> addListener(CacheEventListener<K, V> listener) {
        if (! listeners.contains(listener)) {
            listeners.add(listener);
        }
        return this;
    }

    /**
     * Build the optimal cache based on the input parameters.
     * 
     * @return new cache, never null.
     */
    public Cache<K, V> build() {
        Cache<K, V> cache;
        if (expireType == null) {
            cache = new LRUCache<>(name, maxSize, isSynchronized);
        } else {
            cache = new ExpiringLRUCache<>(name, maxSize, timeToLive, expireType, isSynchronized);
        }

        for (CacheEventListener<K, V> listener : listeners) {
            cache.addListener(listener);
        }

        return cache;
    }
}
