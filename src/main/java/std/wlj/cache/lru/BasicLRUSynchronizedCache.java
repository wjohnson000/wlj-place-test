package std.wlj.cache.lru;

import java.util.Collections;

/**
 * Simple LRU cache: once the cache is full, the oldest (least recently used) entry will
 * be removed, thus keeping the cache at a specified maximum size.
 * </p>
 * <strong>NOTE:</strong> this cache is synchronized, and can be used in a multi-threaded
 * environment.
 * </p>

 * @author wjohnson000
 *
 * @param <K> type of the KEY
 * @param <V> type of the VALUE
 */

public class BasicLRUSynchronizedCache<K, V> extends BasicLRUCache<K, V> {

    public BasicLRUSynchronizedCache(final int cacheSize) {
        super(cacheSize);

        this.cache = Collections.synchronizedMap(this.cache);
    }
}
