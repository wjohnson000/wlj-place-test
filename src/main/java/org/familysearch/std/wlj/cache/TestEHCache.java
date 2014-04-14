package org.familysearch.std.wlj.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public class TestEHCache {

    static CacheManager manager = CacheManager.getInstance();
    static Cache myCache = new Cache(
        new CacheConfiguration("myCache", 10000)
              .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
              .eternal(false)
              .timeToLiveSeconds(1)
              .timeToIdleSeconds(10)
              .diskExpiryThreadIntervalSeconds(0));
    static {
        manager.addCache(myCache);
    }

    public static void main(String... args) {
        int  count01 = 0;
        int  count02 = 0;
        int  maxSize = 0;
        long time01  = 0;
        long time02  = 0;

        long     nnow;
        long     then;
        MyObject what;

        for (int i=0;  i<200000;  i++) {
            what = MyObject.getInstance();
            String key = String.valueOf(what.key);
            Element element = new Element(key, what);
            nnow = System.nanoTime();
            myCache.put(element);
            then = System.nanoTime();
            time01 += (then - nnow);
            if (i%10000 == 0) maxSize = Math.max(maxSize, myCache.getSize());

            for (int j=0;  j<20000;  j+=111) {
                key = String.valueOf(j);
                nnow = System.nanoTime();
                element = myCache.get(key);
                then = System.nanoTime();
                time02 += (then - nnow);

                if (element == null) {
                    count01++;
                } else {
                    count02++;
                }
            }
        }

        System.out.println("EH-CACHE STATISTICS ...");
        System.out.println("-----------------------");
        System.out.println("MaxSize      : " + maxSize);
        System.out.println("Missing count: " + count01);
        System.out.println("  Found count: " + count02);
        System.out.println("     PUT time: " + (time01 / 1000000.0));
        System.out.println("     GET time: " + (time02 / 1000000.0));
    }
}
