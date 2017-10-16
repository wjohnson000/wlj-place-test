package std.wlj.cache.lru;

/**
 * The type associated with an expiring cache.  Expiration can be based on:
 * <ul>
 *   <li>Create time -- an element will expire after a fixed duration based on
 *       when it was initially created.</li>
 *   <li>Access time -- an element will expire after a fixed duration based on
 *       when it was last accessed.</li>
 * </ul>
 * @author wjohnson000
 *
 */
public enum ExpireType {

    USE_CREATE_TIME,  // expiration of an entry is based on when it was created

    USE_ACCESS_TIME   // expiration of an entry is based on when it was last access
}
