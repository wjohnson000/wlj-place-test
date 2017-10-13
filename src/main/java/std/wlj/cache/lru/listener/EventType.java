package std.wlj.cache.lru.listener;

public enum EventType {

    /** An entry in the cache has been evicted */
    EVICTED,

    /** And entry in the cache has expired */
    EXPIRED,

    /** An entry in the cache has been removed */
    REMOVED,

    /** The cache is being cleared of all elements */
    REMOVE_ALL,

    /** An entry in the cache has been updated */
    UPDATED
}
