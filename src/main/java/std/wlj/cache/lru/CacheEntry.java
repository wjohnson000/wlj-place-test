package std.wlj.cache.lru;

/**
 * Represents an entry in an expiring cache.
 * 
 * @author wjohnson000
 *
 * @param <V> type of the VALUE
 */
public class CacheEntry<V> {

    private long lastAccessMillis;
    private V    value;

    public CacheEntry(V value) {
        this.value = value;
        this.lastAccessMillis = System.currentTimeMillis();
    }

    public V getValue() {
        return value;
    }

    public void updateAccessTime() {
        lastAccessMillis = System.currentTimeMillis();
    }

    public boolean isExpired(int ttlSeconds) {
        long elapsed = System.currentTimeMillis() - lastAccessMillis;
        return (elapsed / 1_000) > ttlSeconds;
    }
}
