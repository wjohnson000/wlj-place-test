package org.familysearch.std.wlj.cache;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

import org.apache.commons.configuration.BaseConfiguration;
import org.apache.commons.configuration.Configuration;


/**
 * (In memory) EHCache implementation of GenericCache.
 *
 * @param <K> type for the key
 * @param <T> type for the value
 */
public class EHCacheIndexing<K,T> {

    private final CacheManager cacheManager;
    private final Cache cache;
    private final String cacheName;

    /**
     * Builder for an EHCacheGenricCache. This uses the following defaults:
     * <ul>
     *  <li>maxElements = 1000</li>
     *  <li>timeToLive = 10 * 60 seconds (10 minutes)</li>
     *  <li>timeToIdle = 0 seconds</li>
     *  <li>evictionPolicy = MemoryStoreEvictionPolicy.LRU</li>
     * </ul>
     * @author fransonsr
     *
     * @param <K> type for the key
     * @param <T> type for the value
     */
    public static class EHCacheBuilder<K, T> {
        String cacheName;
        int maxElements = 1000;
        int timeToLive = 10 * 60;   // 10 minutes
        int timetoIdle = 0;         // 0 minutes
        MemoryStoreEvictionPolicy evictionPolicy = MemoryStoreEvictionPolicy.LRU;
        List<CacheEventListener> eventListeners = new ArrayList<CacheEventListener>();

        public String getCacheName() {
            return cacheName;
        }

        public EHCacheBuilder<K, T> setCacheName(String cacheName) {
            this.cacheName = cacheName;
            return this;
        }

        public int getMaxElements() {
            return maxElements;
        }

        public EHCacheBuilder<K, T> setMaxElements(int maxElements) {
            this.maxElements = maxElements;
            return this;
        }

        public int getTimeToLive() {
            return timeToLive;
        }

        public EHCacheBuilder<K, T> setTimeToLive(int timeToLive) {
            this.timeToLive = timeToLive;
            return this;
        }

        public int getTimetoIdle() {
            return timetoIdle;
        }

        public EHCacheBuilder<K, T> setTimetoIdle(int timetoIdle) {
            this.timetoIdle = timetoIdle;
            return this;
        }

        public MemoryStoreEvictionPolicy getEvictionPolicy() {
            return evictionPolicy;
        }

        public EHCacheBuilder<K, T> setEvictionPolicy(MemoryStoreEvictionPolicy evictionPolicy) {
            this.evictionPolicy = evictionPolicy;
            return this;
        }

        public List<CacheEventListener> getEventListeners() {
            return eventListeners;
        }

        public EHCacheBuilder<K, T> setEventListeners(List<CacheEventListener> eventListeners) {
            this.eventListeners = eventListeners;
            return this;
        }

        public EHCacheBuilder<K, T> addEventListener(CacheEventListener eventListener) {
            this.eventListeners.add(eventListener);
            return this;
        }

        public EHCacheBuilder<K, T> removeEventListener(CacheEventListener eventListener) {
            this.eventListeners.remove(eventListener);
            return this;
        }

        public EHCacheIndexing<K, T> build() {
            return new EHCacheIndexing<K, T>(this);
        }
    }

    /**
     * Static factory method to create an instance based on a pre-configured
     * EHCache instance (i.e., defined in ehcache.xml).
     * @param <K> type for the key
     * @param <T> type for the value
     * @param cacheName the name for the cache
     * @return the cacne
     */
    public static <K, T> EHCacheIndexing<K, T> getConfiguredCache(String cacheName) {
        return new EHCacheIndexing<K, T>(CacheManager.getInstance(), cacheName);
    }

    /**
     * Instantiate and instance using default values.
     * @param cacheName
     */
    public EHCacheIndexing(String cacheName) {
        this(cacheName, new BaseConfiguration());
    }

    /**
     * Instantiate an instance using a Configuration. The expected keys are:
     * <ul>
     * <li><code>maxElements</code> - maximum number of elements (integer; optional; default: 10000)</li>
     * <li><code>timeToLive</code> - time to live value (long; optional; default: 10 minutes)</li>
     * <li><code>timeToIdle</code> - time to idle value (long; optional; default: 0 minutes)</li>
     * <li><code></code></li>
     * <li><code></code></li>
     * <li><code></code></li>
     * </ul>
     * @param cacheName
     * @param config
     * @return
     */
    public EHCacheIndexing(String cacheName, Configuration config) {
        this.cacheName = cacheName;
        int maxElements = config.getInt("maxElements", 1000);
        MemoryStoreEvictionPolicy policy = MemoryStoreEvictionPolicy.LRU;
        long ttl = config.getLong("timeToLive", (10 * 60)); // Time to live: 10  min
        long tti = config.getLong("timeToIdle", (0 * 60)); // Time to idle: 0 min

        cacheManager = CacheManager.getInstance();
        Cache tempCache = new Cache(cacheName, // cache name
                maxElements, // max elements in memory
                policy, // eviction policy
                false, // overflow to disk?
                "", // path for disk file (ignored)
                false, // eternal?
                ttl, // time to live (sec)
                tti, // time to idle (sec)
                false, // disk persistent?
                0l, // disk persistent flush (sec)
                null // listeners
        );
        cacheManager.addCache(tempCache);
        cache = cacheManager.getCache(cacheName);
    }

    @SuppressWarnings("unchecked")
    private EHCacheIndexing(EHCacheBuilder<K, T> builder) {
        this.cacheName = builder.getCacheName();
        cacheManager = CacheManager.getInstance();
        Cache tempCache = new Cache(builder.getCacheName(), // cache name
                builder.getMaxElements(), // max elements in memory
                builder.getEvictionPolicy(), // eviction policy
                false, // overflow to disk?
                "", // path for disk file (ignored)
                false, // eternal?
                builder.getTimeToLive(), // time to live (sec)
                builder.getTimetoIdle(), // time to idle (sec)
                false, // disk persistent?
                0l, // disk persistent flush (sec)
                null // listeners
        );
        cacheManager.addCache(tempCache);
        cache = cacheManager.getCache(cacheName);
        for(CacheEventListener listener : builder.getEventListeners()) {
            cache.getCacheEventNotificationService().getCacheEventListeners().add(listener);
        }
    }

    /**
     * Private constructor to create an instance based on a pre-configured EHCache.
     *
     * @param cacheManager
     * @param cacheName
     */
    private EHCacheIndexing(CacheManager cacheManager, String cacheName) {
        this.cacheName = cacheName;
        this.cacheManager = cacheManager;
        this.cache = cacheManager.getCache(cacheName);
    }

    /**
     * Get the EHCache CacheManager instance.
     * @return
     */
    public CacheManager getCacheManager() {
        return cacheManager;
    }

    /**
     * Get the EHCache Cache instance.
     * @return
     */
    public Cache getCache() {
        return cache;
    }

    /* (non-Javadoc)
     * @see org.familysearch.idx.server.util.cache.GenericCache#getCacheName()
     */
    public String getCacheName() {
        return cacheName;
    }

    /* (non-Javadoc)
     * @see org.familysearch.idx.server.util.cache.GenericCache#exists(java.lang.Object)
     */
    public boolean exists(K key) {
        return cache.isKeyInCache(key);
    }

    /* (non-Javadoc)
     * @see org.familysearch.idx.server.util.cache.GenericCache#get(java.lang.Object)
     */
    @SuppressWarnings("unchecked")
    public T get(K key) {
        T entry = null;

        Element el = cache.get(key);
        if(el != null) {
            entry = (T)el.getObjectValue();
        }

        return entry;
    }

    /* (non-Javadoc)
     * @see org.familysearch.idx.server.util.cache.GenericCache#put(java.lang.Object, java.lang.Object)
     */
    public void put(K key, T entry) {
        cache.put(new Element(key, entry));
    }

    /* (non-Javadoc)
     * @see org.familysearch.idx.server.util.cache.GenericCache#remove(java.lang.Object)
     */
    public void remove(K key) {
        cache.remove(key);
    }

    /* (non-Javadoc)
     * @see org.familysearch.idx.server.util.cache.GenericCache#removeAll()
     */
    public void removeAll() {
        cache.removeAll();
    }

    /* (non-Javadoc)
     * @see org.familysearch.idx.server.util.cache.GenericCache#isEmpty()
     */
    public boolean isEmpty() {
        return cache.getSize() == 0;
    }

    /* (non-Javadoc)
     * @see org.familysearch.idx.server.util.cache.GenericCache#size()
     */
    public int size() {
        return cache.getSize();
    }

    /* (non-Javadoc)
     * @see org.familysearch.idx.server.util.cache.GenericCache#shutdown()
     */
    public void shutdown() {
        cacheManager.removeCache(getCacheName());
    }

    /* (non-Javadoc)
     * @see org.familysearch.idx.server.util.cache.GenericCache#iterator()
     */
    public Iterator<T> iterator() {
        return new EntryIterator();
    }

    /**
     * To iterate over the entries in the cache. The iterator's
     * {@link #remove()} will remove the element from the cache.
     * NOTE: this is NOT backed by the cache, but maintains a list copy
     * of the elements in the cache.
     *
     * WARNING: the cache may have entries that have null values! Thus,
     * the iterator may return null when next() is called.
     * @author fransonsr
     *
     */
    class EntryIterator implements Iterator<T> {

        final List<Map.Entry<K, T>> entries;
        final Iterator<Map.Entry<K, T>> iterator;
        Map.Entry<K, T> currentEntry = null;

        @SuppressWarnings("unchecked")
        public EntryIterator() {
            Map<K, T> entryMap = cache.getAllWithLoader(cache.getKeys(), null);
            entries = new ArrayList<Map.Entry<K,T>>(entryMap.entrySet());

            // Remove any nulls in the list; this is possible because entries in the
            // cache may have been removed while the map was being created.
            Iterator<Map.Entry<K, T>> nullCheckIterator = entries.iterator();
            while(nullCheckIterator.hasNext()) {
                Map.Entry<K, T> entry = nullCheckIterator.next();
                if(entry == null) {
                    nullCheckIterator.remove();
                }
            }

            iterator = entries.iterator();
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public T next() {
            currentEntry = iterator.next();
            return currentEntry.getValue();
        }

        public void remove() {
            cache.remove(currentEntry.getKey());
            iterator.remove();
        }
    }
}
