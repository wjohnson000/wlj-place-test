package std.wlj.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public class TestEHCacheTTL {

    static CacheManager manager = CacheManager.getInstance();
    static Cache myCache = new Cache(
        new CacheConfiguration("myCache", 10000)
              .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU)
              .eternal(false)
              .timeToLiveSeconds(10)
              .timeToIdleSeconds(5)
              .diskExpiryThreadIntervalSeconds(0));

    static {
        manager.addCache(myCache);
    }

    public static void main(String... args) throws Exception {
        MyObject what = MyObject.getInstance();
        String   key = String.valueOf(what.key);
        myCache.put(new Element(key, what));

        for (int i=0;  i<100;  i++) {
            System.out.println(i + " . " + key + " --> " + myCache.get(key));
            Thread.sleep(250L);
        }
    }
}
