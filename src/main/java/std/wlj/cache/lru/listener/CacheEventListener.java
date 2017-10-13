package std.wlj.cache.lru.listener;

/**
 * Definition of a Listener to allow a client to be notified of certain events in the cache.
 * The event notifications are:
 * <ul>
 *   <li><strong>EVICTED -</strong> an element has been evicted from the cache</li>
 *   <li><strong>EXPIRED -</strong> an element has expired and is removed from the cache</li>
 *   <li><strong>REMOVED -</strong> an element has been removed from the cache</li>
 *   <li><strong>REMOVEALL -</strong> all elements have been removed from the cache</li>
 *   <li><strong>UPDATED -</strong> an element in the cache has been updated</li>
 * </ul>
 * 
 * @author wjohnson000
 *
 * @param <K>
 * @param <V>
 */
public interface CacheEventListener<K, V> {

    /**
     * Called after an element has been evicted, such as when the oldest element of
     * a cache is thrown out to allow room for a newer element.
     * 
     * @param key the key of the element being evicted
     * @param value the value of the element being evicted
     */
    void notifyElementEvicted(K key, V value);

    /**
     * Called after an element has expired, such as when an element has been in the
     * cache too long.  NOTE: this will only happen as part of an "Expiring" cache.
     * 
     * @param key the key of the element that has expired
     * @param value the value of the element that has expired
     */
    void notifyElementExpired(K key, V value);

    /**
     * Called after an element has been removed, which is an action done explicitly
     * by a client.
     * 
     * @param key the key of the element being removed
     * @param value the value of the element being removed
     */
    void notifyElementRemoved(K key, V value);

    /**
     * Called after all elements in a cache have been removed.
     * 
     */
    void notifyElementRemoveAll();

    /**
     * Called after an element has been updated.
     * 
     * @param key the key of the element being evicted
     * @param oldValue the old (previous) value of the element
     * @param newValue the new value of the element
     */
    void notifyElementUpdated(K key, V oldValue, V newValue);
}
