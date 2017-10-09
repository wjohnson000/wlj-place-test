package std.wlj.cache.lru;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Simple LRU cache: once the cache is full, the oldest (least recently used) entry will
 * be removed, thus keeping the cache at a specified maximum size.
 * </p>
 * <strong>NOTE:</strong> this cache is NOT synchronized, and hence should not be use
 * in a multi-threaded environment.  Or used with great care.
 * </p>
 * 
 * @author wjohnson000
 *
 * @param <K> type of the KEY
 * @param <V> type of the VALUE
 */

public class BasicLRUCache<K, V> {

    private   int getCount;
    private   int putCount;
    private   int removeCount;

    protected int discardCount;
    protected Map<K, V> cache;

    public BasicLRUCache(final int cacheSize) {
        this.cache = new LinkedHashMap<K, V>(cacheSize, 0.75F, true) {
            private static final long serialVersionUID = 1L;

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                if (size() <= cacheSize) {
                    return false;
                } else {
                    discardCount++;
                    return true;
                }
            }
        };
    }

    public void put(K key, V value) {
        putCount++;
        cache.put(key, value);
    }

    public V get(K key) {
        getCount++;
        return cache.get(key);
    }

    public V remove(K key) {
        removeCount++;
        return cache.remove(key);
    }

    public int size() {
        return cache.size();
    }

    public int getGetCount() {
        return getCount;
    }
    
    public int getPutCount() {
        return putCount;
    }
    
    public int getRemoveCount() {
        return removeCount;
    }

    public int getDiscardCount() {
        return discardCount;
    }
}
