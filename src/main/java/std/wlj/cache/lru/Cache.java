package std.wlj.cache.lru;

import std.wlj.cache.lru.listener.CacheEventListener;

/**
 * Define the basic services provided by a LRU cache;
 * 
 * @author wjohnson000
 *
 * @param <K> type of the KEY
 * @param <V> type of the VALUE
 */
public interface Cache<K, V> {

    /**
     * @return the name of the cache
     */
    String getName();

    /**
     * @return the number of entries in the cache
     */
    int size();

    /**
     * @return cache statistics
     */
    CacheStats getStats();

    /**
     * Add an entry into the cache.
     * 
     * @param key key value
     * @param value value associated with the key
     */
    void put(K key, V value);

    /**
     * Retrieve the value from the cache associated with a given key, or null if there
     * is no associated value.
     * 
     * @param key key value
     * @return associated value
     */
    V get(K key);

    /**
     * Remove an entry from the cache based on a given key.
     * 
     * @param key key value
     * @return value removed, or null
     */
    V remove(K key);

    /**
     * Remove all entries from the cache
     */
    void removeAll();

    /**
     * Add a listener for cache events
     * 
     * @param listener listener to add
     */
    void addListener(CacheEventListener<K, V> listener);

    /**
     * Remove a listener for cache events
     * 
     * @param listener listener to remove
     */
    void removeListener(CacheEventListener<K, V> listener);

}
