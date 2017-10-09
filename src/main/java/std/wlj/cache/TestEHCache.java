package std.wlj.cache;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;

public class TestEHCache {

    static CacheManager manager = CacheManager.getInstance();
    static Cache myCache = new Cache(
        new CacheConfiguration("myCache", 10_000)
              .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LFU)
              .eternal(false)
              .timeToLiveSeconds(1)
              .timeToIdleSeconds(6)
              .diskExpiryThreadIntervalSeconds(0));
    static {
        manager.addCache(myCache);
    }

    public static void main(String... args) {
        int  count01 = 0;
        int  count02 = 0;
        int  maxSize = 0;
        long timePut = 0;
        long timeGet = 0;

        long timeStart;
        long timeEnd;
        long time01;
        long time02;

        MyObject what;

        timeStart = System.nanoTime();
        for (int i=0;  i<500_000;  i++) {
            what = MyObject.getInstance();
            String key = String.valueOf(what.key);
            Element element = new Element(key, what);
            time01 = System.nanoTime();
            myCache.put(element);
            time02 = System.nanoTime();
            timePut += (time02 - time01);
            if (i%10_000 == 0) maxSize = Math.max(maxSize, myCache.getSize());

            for (int j=0;  j<20_000;  j+=111) {
                key = String.valueOf(j);
                time01 = System.nanoTime();
                element = myCache.getQuiet(key);
                time02 = System.nanoTime();
                timeGet += (time02 - time01);

                if (element == null) {
                    count01++;
                } else {
                    count02++;
                }
            }
        }
        timeEnd = System.nanoTime();

        System.out.println("EH-CACHE STATISTICS ...");
        System.out.println("-----------------------");
        System.out.println("MaxSize      : " + maxSize);
        System.out.println("Missing count: " + count01);
        System.out.println("  Found count: " + count02);
        System.out.println("     TOT time: " + ((timeEnd - timeStart) / 1_000_000.0));
        System.out.println("     PUT time: " + (timePut / 1_000_000.0));
        System.out.println("     GET time: " + (timeGet / 1_000_000.0));
    }
}
