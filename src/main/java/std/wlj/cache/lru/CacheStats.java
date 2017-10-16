package std.wlj.cache.lru;

import java.util.concurrent.atomic.LongAdder;

/**
 * Cache statistics:
 * <ul>
 *   <li><strong>GET count</strong> - the number of 'get' calls</li>
 *   <li><strong>HIT count</strong> - the number of 'get' calls that returned a non-null value</li>
 *   <li><strong>PUT count</strong> - the number of 'put' calls</li>
 *   <li><strong>EVICT count</strong> - the number of entries removed to make room for newer ones</li>
 *   <li><strong>EXPIRE count</strong> - the number of entries removed because of expiration</li>
 *   <li><strong>REMOVE count</strong> - the number of 'remove' calls</li>
 * </ul>
 * @author wjohnson000
 *
 */
public class CacheStats {

    private LongAdder getCount = new LongAdder();
    private LongAdder hitCount = new LongAdder();
    private LongAdder putCount = new LongAdder();
    private LongAdder evictCount = new LongAdder();
    private LongAdder expireCount = new LongAdder();
    private LongAdder removeCount = new LongAdder();

    public void incrGetCount() {
        getCount.increment();
    }

    public void incrHitCount() {
        hitCount.increment();
    }

    public void incrPutCount() {
        putCount.increment();
    }
    
    public void incrEvictCount() {
        evictCount.increment();
    }

    public void incrExpireCount() {
        expireCount.increment();
    }
    
    public void incrRemoveCount() {
        removeCount.increment();
    }

    public long getGetCount() {
        return getCount.longValue();
    }

    public long getHitCount() {
        return hitCount.longValue();
    }
    
    public long getPutCount() {
        return putCount.longValue();
    }
    
    public long getEvictCount() {
        return evictCount.longValue();
    }

    public long getExpireCount() {
        return expireCount.longValue();
    }

    public long getRemoveCount() {
        return removeCount.longValue();
    }

}
