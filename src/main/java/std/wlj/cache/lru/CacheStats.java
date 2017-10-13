package std.wlj.cache.lru;

import java.util.concurrent.atomic.LongAdder;

/**
 * Cache statistics:
 * <ul>
 *   <li><strong>GET count</strong> - the number of 'get' calls</li>
 *   <li><strong>PUT count</strong> - the number of 'put' calls</li>
 *   <li><strong>REMOVE count</strong> - the number of 'remove' calls</li>
 *   <li><strong>EXPIRE count</strong> - the number of entries removed because of expiration</li>
 *   <li><strong>DISCARD count</strong> - the number of entries removed to make room for newer ones</li>
 * </ul>
 * @author wjohnson000
 *
 */
public class CacheStats {

    private LongAdder getCount = new LongAdder();
    private LongAdder putCount = new LongAdder();
    private LongAdder removeCount = new LongAdder();
    private LongAdder expireCount = new LongAdder();
    private LongAdder discardCount = new LongAdder();

    public void incrGetCount() {
        getCount.increment();
    }

    public void incrPutCount() {
        putCount.increment();
    }

    public void incrRemoveCount() {
        removeCount.increment();
    }

    public void incrExpireCount() {
        expireCount.increment();
    }

    public void incrDiscardCount() {
        discardCount.increment();
    }

    public long getGetCount() {
        return getCount.longValue();
    }
    
    public long getPutCount() {
        return putCount.longValue();
    }

    public long getExpireCount() {
        return expireCount.longValue();
    }

    public long getRemoveCount() {
        return removeCount.longValue();
    }

    public long getDiscardCount() {
        return discardCount.longValue();
    }

}
