package std.wlj.cache.lru;

import java.util.ArrayList;
import java.util.List;

import std.wlj.cache.lru.listener.CacheEventListener;

/**
 * Builder that will create the best type of cache based on the input parameters.
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
    private boolean isSynchronized;
    private int     maxSize = DEFAULT_MAX_SIZE;
    private int     timeToLive = DEFAULT_TTL_SECONDS;
    private ExpireType expireType = null;
    private List<CacheEventListener<K, V>> listeners = new ArrayList<>();

    public CacheBuilder<K, V> setName(String name) {
        this.name = name;
        return this;
    }

    public CacheBuilder<K, V> setSynchronized(boolean isSynchronized) {
        this.isSynchronized = isSynchronized;
        return this;
    }

    public CacheBuilder<K, V> setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public CacheBuilder<K, V> setTimeToLiveInSeconds(int timeToLive) {
        this.timeToLive = timeToLive;
        return this;
    }

    public CacheBuilder<K, V> setExpireType(ExpireType expireType) {
        this.expireType = expireType;
        return this;
    }

    public CacheBuilder<K, V> addListener(CacheEventListener<K, V> listener) {
        if (! listeners.contains(listener)) {
            listeners.add(listener);
        }
        return this;
    }

    public Cache<K, V> build() {
        if (expireType == null) {
            return new LRUCache<>(name, maxSize, isSynchronized);
        } else {
            return new ExpiringLRUCache<>(name, maxSize, timeToLive, expireType, isSynchronized);
        }
    }
}
