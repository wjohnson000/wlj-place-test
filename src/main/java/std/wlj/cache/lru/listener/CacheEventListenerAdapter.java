package std.wlj.cache.lru.listener;

/**
 * An abstract adapter class for listening to cache-events.  The methods in this class are empty,
 * serving as a convenience for creating listeners.
 * <p/>
 * Any listener class that directly implements the {@link CacheEventListener} interface must
 * implement every method.  However by extending this class, a new listener need only implement
 * the methods they care about. 
 * 
 * @author wjohnson000
 *
 * @param <K> type of the KEY
 * @param <V> type of the VALUE
 */
public abstract class CacheEventListenerAdapter<K, V> implements CacheEventListener<K, V> {

    @Override
    public void notifyElementEvicted(K key, V value) {
    }

    @Override
    public void notifyElementExpired(K key, V value) {
    }

    @Override
    public void notifyElementRemoved(K key, V value) {
    }

    @Override
    public void notifyElementRemoveAll() {
    }

    @Override
    public void notifyElementUpdated(K key, V oldValue, V newValue) {
    }

}
