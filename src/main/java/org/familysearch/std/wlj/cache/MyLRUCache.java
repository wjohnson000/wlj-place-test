package org.familysearch.std.wlj.cache;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.familysearch.standards.core.logging.Logger;


/**
 * Implement a LRU (least-recently-used) and expiring (time-to-live) cache.  This
 * is based on {@link org.familysearch.standards.core.LRUCache} but can't directly
 * extend it because of the lack of a "remove(...)" method.
 * <p/>
 * If the cache limit is reached, all stale [expired] values will be removed.  If
 * the cache is still full, the oldest [least recently used] value will be removed.
 * A background thread will be used to clean up stale/expired values.
 * <p/>
 * If no "time-to-live" value is supplied, a default value of 10 minutes will be
 * applied.  If no "cleanup-delay" is supplied, a default value of 30 seconds will
 * be used.
 * <p/>
 * Note: the class has been copied from {@link org.familysearch.standards.core.LRUCache},
 * which in turn was based on a LRUCache created by Christian d'Heureuse.  However a
 * "remove" method was added so that elements can be "un-cached" when required.
 * <p/>
 * Author: Christian d'Heureuse (<a href="http://www.source-code.biz">www.source-code.biz</a>)<br>
 * License: <a href="http://www.gnu.org/licenses/lgpl.html">LGPL</a>.
 * 
 * @author wjohnson000
 *
 */
public class MyLRUCache<K,V> {

    public static interface CacheListener<T> {

        void expiredElement(T key);

        void evictedElement(T key);
    }

    /** LOGGER */
    private static final Logger logger = new Logger(MyLRUCache.class);

    /** Fixed load-factor for the underlying map */
    private static final float mapLoadFactor = 0.75f;

    /** Scheduler to run periodic clean-up of stale/expired values -- register a shutdown hook */
    private static final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(2, new ThreadFactory() {
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setDaemon(true);
                    return t;
                }
            });

    /** Register a shut-down hook */
    static {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                shutdown();
            }
        });
    }

    /** Helper class that keep track of a value size and create-time */
    private class CacheableObject {
        private final V cachedObject;
        private final int containmentCount;
        private final long createTime;

        public CacheableObject(V o, int containmentCount) {
            this.cachedObject = o;
            this.containmentCount = containmentCount;
            this.createTime = System.currentTimeMillis();
        }
    }

    /** Map that represents the actual storage for the key/value pairs */
    private LinkedHashMap<K, CacheableObject> valueMap;

    /** Maximum number of values that can be stored */
    private final int cacheSizeLimit;

    /**  Maximum time before a value becomes stale; default to 600 seconds */
    private final long timeToLive;  // in milli-seconds

    /**  Time between attempts to clean up stale values */
    private final long cleanupDelay; // in seconds

    /** Current number of values stored */
    private int currentCacheSize = 0;

    /** Cache listeners */
    private List<CacheListener<K>> listeners = new ArrayList<CacheListener<K>>();

    /** Lock used for all synchronized access */
    private Object theLock = new Object();


    /**
     * Force a shutdown of all scheduled tasks
     */
    public static void shutdown() {
        logger.warn("Attempting to shut down scheduled LRU cache tasks ...");
        scheduler.shutdown();
        try {
            if (! scheduler.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
                logger.warn("Attempting to force shut down scheduled LRU cache tasks ...");
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.warn("Exception shutting down tasks ... " + e.getMessage());
        }
    }

    /**
     * Create a new LRU cache with a "time-to-live" of 10 minutes (600 seconds),
     * and a 30 second delay in cleaning up expired values.
     * 
     * @param cacheSizeLimit the maximum number of entries that will be kept in this cache.
     */
    public MyLRUCache(int cacheSizeLimit) {
        this(cacheSizeLimit, 600);
    }

    /**
     * Create a new LRU cache with a given "time-to-live" in seconds, and a 30
     * second delay in cleaning up expired values.
     * 
     * @param cacheSizeLimit the maximum number of entries that will be kept in this cache.
     * @param timeToLive maximum "time-to-live", must be > 0
     */
    public MyLRUCache(int cacheSizeLimit, int timeToLive) {
        this(cacheSizeLimit, timeToLive, 30);
    }


    /**
     * Create a new LRU cache with a given "time-to-live" in seconds.  If a client wants
     * a non-expiring cache, simply use the default "LRUCache".
     * <p/>
     * A negative or zero value for the "cacheSizeLimit", "timeToLive" or "cleanupDelay"
     * will result in an exception.
     * 
     * @param cacheSizeLimit the maximum number of entries that will be kept in this cache.
     * @param cleanupDelay delay between process to clean up stale values.
     * @param timeToLive maximum "time-to-live", must be > 0
     */
    public MyLRUCache(int cacheSizeLimit, int timeToLive, int cleanupDelay) {
        if (cacheSizeLimit <= 0) {
            throw new IllegalArgumentException("Cache-Size-Limit value must be > 0");
        } else if (timeToLive <= 0) {
            throw new IllegalArgumentException("Time-To-Live value must be > 0");
        } else if (cleanupDelay <= 0) {
            throw new IllegalArgumentException("Cleanup-Delay value must be > 0");
        }

        this.cacheSizeLimit = cacheSizeLimit;
        this.cleanupDelay = cleanupDelay;
        this.timeToLive = timeToLive * 1000;

        int hashTableCapacity = (int) Math.ceil(cacheSizeLimit / mapLoadFactor) + 1;
        valueMap = new LinkedHashMap<K, CacheableObject>(hashTableCapacity, mapLoadFactor, true) {
            // (an anonymous inner class)
            private static final long serialVersionUID = 1;

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, CacheableObject> eldest) {
                boolean retVal = MyLRUCache.this.currentCacheSize > MyLRUCache.this.cacheSizeLimit;
                if (retVal) {
                    MyLRUCache.this.currentCacheSize -= eldest.getValue().containmentCount;
                    synchronized(listeners) {
                        for (CacheListener<K> listener : listeners) {
                            listener.evictedElement(eldest.getKey());
                        }
                    }

                }
                return retVal;
            }
        };

        // Run a cache clean-up every 10 seconds, or whatever the user has specified
        scheduler.schedule(
            new Runnable() {
                @Override public void run() {
                    removeExpired();
                }
            },
            this.cleanupDelay,
            TimeUnit.SECONDS
        );
    }

    /**
     * Retrieves an entry from the cache, regardless of its "Expired" status.
     *
     * @param key the key whose associated value is to be returned.
     * @return the value associated to this key, or null if the key isn't matched
     */
    public V get(K key) {
        CacheableObject cObj;

        synchronized(theLock) {
            cObj = valueMap.get(key);
        }

        return cObj == null ? null : cObj.cachedObject;
    }

    /**
     * Retrieves an entry from the cache.  The retrieved entry becomes the MRU
     * (most recently used) entry.
     *
     * @param key the key whose associated value is to be returned.
     * @return the value associated to this key, or null if no value with this key
     *         exists in the cache or the value is expired.
     */
    public V getIfNotExpired(K key) {
        CacheableObject cObj;

        synchronized(theLock) {
            cObj = valueMap.get(key);
        }

        if (null != cObj) {
            if (isExpired(cObj)) {
                synchronized(theLock) {
                    currentCacheSize -= cObj.containmentCount;
                    valueMap.remove(key);
                    for (CacheListener<K> listener : listeners) {
                        listener.expiredElement(key);
                    }
                }
                return null;
            } else {
                return cObj.cachedObject;
            }
        } else {
            return null;
        }
    }

    /**
     * Adds an entry to this cache.  If the cache is full, the LRU (least recently
     * used) entry is dropped.
     *
     * @param key                  the key with which the specified value is to be associated.
     * @param value                a value to be associated with the specified key.
     * @param containedObjectCount a value describing how many contained objects are in the value
     */
    public void put(K key, V value, int containedObjectCount) {
        synchronized(theLock) {
            if (! valueMap.containsKey(key)) {
                currentCacheSize += containedObjectCount;
            }
            valueMap.put(key, new CacheableObject(value, containedObjectCount));
        }
    }

    /**
     * Remove an entry from the cache, returning the object to which it was associated.
     *  
     * @param key the key of the object which is to be removed
     * @return object removed, or NULL if the key wasn't found
     */
    public V remove(K key) {
        CacheableObject cObj;

        synchronized(theLock) {
            cObj = valueMap.remove(key);
            if (null != cObj) {
                currentCacheSize -= cObj.containmentCount;
            }
        }

        return (cObj == null) ? null : cObj.cachedObject;
    }

    /**
     * Clear the cache.
     */
    public void clear() {
        synchronized(theLock) {
            valueMap.clear();
            currentCacheSize = 0;
        }
    }

    /**
     * Return the number of non-expired values in the cache.
     * 
     * @return the number of non-expired values in the cache.
     */
    public int size() {
        int size = 0;

        Collection<CacheableObject> values = new HashSet<CacheableObject>();
        synchronized(theLock) {
            values.addAll(valueMap.values());
        }

        for (CacheableObject cObj : values) {
            if (! this.isExpired(cObj)) {
                size++;
            }
        }

        return size;
    }

    /**
     * Determines if the cache contains the given key.
     * 
     * @param key key to check
     * @return TRUE if the cache has the key and the value is not expired; FALSE
     *         otherwise.
     */
    public boolean containsKey(K key) {
        synchronized(theLock) {
            return valueMap.containsKey(key)  &&  ! isExpired(valueMap.get(key));            
        }
    }

    /**
     * Returns a <code>Map</code> that contains a copy of all cache entries that
     * aren't expired.  NOTE: This is a potentially expensive operation and should
     * be avoided at all costs.
     *
     * @return a <code>Map</code> with a shallow copy of the cache content.
     */
    public Map<K,V> getMap() {
        Map<K,V> results = new HashMap<K,V>(valueMap.size());

        synchronized(theLock) {
            for (Map.Entry<K,CacheableObject> entry : valueMap.entrySet()) {
                if (! isExpired(entry.getValue())) {
                    results.put(entry.getKey(), entry.getValue().cachedObject);
                }
            }
        }

        return results;
    }

    /**
     * Add a {@link CachListener} that can be notified of certain cache events.
     * 
     * @param listener cache listener
     */
    public void addListener(CacheListener<K> listener) {
        synchronized(theLock) {
            listeners.add(listener);
        }
    }

    /**
     * Remove a {@link CachListener} from the list of listeners.
     * 
     * @param listener cache listener
     */
    public void removeListener(CacheListener<K> listener) {
        synchronized(theLock) {
            listeners.remove(listener);
        }
    }

    /**
     * Determine if a value has expired, i.e., become stale.
     * 
     * @param value cached value
     * @return TRUE if the instance is stale, FALSE otherwise
     */
    private boolean isExpired(CacheableObject value) {
        if (value == null) {
            return false;
        } else {
            return System.currentTimeMillis() - value.createTime > timeToLive;
        }
    }

    /**
     * Remove all expired entries from the underlying map.  This process runs on a
     * separate thread, and cedes control of lock between each attempt to remove a
     * value from the map.
     */
    private void removeExpired() {
        Map<K,CacheableObject> removeMap = new HashMap<K,CacheableObject>(valueMap.size()/2);
        List<CacheListener<K>> tempListeners = new ArrayList<CacheListener<K>>();

        // Retrieve a list of everything that's to be removed
        synchronized(theLock) {
            for (Map.Entry<K,CacheableObject> entry : valueMap.entrySet()) {
                if (isExpired(entry.getValue())) {
                    removeMap.put(entry.getKey(), entry.getValue());
                }
            }
            tempListeners.addAll(listeners);
        }

        // Remove the entries one-at-a-time, notifying the listener[s] in the process
        for (Map.Entry<K,CacheableObject> entry : removeMap.entrySet()) {
            synchronized(theLock) {
                currentCacheSize -= entry.getValue().containmentCount;
                valueMap.remove(entry.getKey());
            }
            Thread.yield();

            for (CacheListener<K> listener : tempListeners) {
                listener.expiredElement(entry.getKey());
            }
        }
    }
}