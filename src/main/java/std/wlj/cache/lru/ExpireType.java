package std.wlj.cache.lru;

public enum ExpireType {
    USE_CREATE_TIME,  // expiration of an entry is based on when it was created
    USE_ACCESS_TIME   // expiration of an entry is based on when it was last access
}
