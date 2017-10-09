package std.wlj.cache;

import java.util.concurrent.Executors;

import org.ehcache.UserManagedCache;
import org.ehcache.config.builders.CacheEventListenerConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.builders.UserManagedCacheBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.ehcache.event.EventType;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;

public class TestEHUserManagedCacheWithListener {

    static long evictCnt = 0;
    static long expireCnt = 0;

    static protected UserManagedCache<String, MyObject>  myCache;

    static class MyListener implements CacheEventListener<String, MyObject> {
        
        public MyListener() {
            super();
        }

        @Override
        public void onEvent(CacheEvent<String, MyObject> event) {
            if (event.getType() == EventType.EVICTED) {
                evictCnt++;
            } else if (event.getType() == EventType.EXPIRED) {
                expireCnt++;
            }
        }
    }

    static {
        myCache =
                UserManagedCacheBuilder.newUserManagedCacheBuilder(String.class, MyObject.class)
                    .identifier("MyCache." + System.nanoTime())
                    .withResourcePools(ResourcePoolsBuilder.newResourcePoolsBuilder().heap(10_000L, EntryUnit.ENTRIES))
                    .withEventExecutors(Executors.newSingleThreadExecutor(), Executors.newFixedThreadPool(5))
                    .withExpiry(Expirations.timeToLiveExpiration(new Duration(1L, java.util.concurrent.TimeUnit.SECONDS)))
                    .withEventListeners(CacheEventListenerConfigurationBuilder
                            .newEventListenerConfiguration(MyListener.class, EventType.EVICTED, EventType.EXPIRED)
                            .asynchronous()
                            .unordered())
                    .build(true);
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
            time01 = System.nanoTime();
            myCache.put(key, what);
            time02 = System.nanoTime();
            timePut += (time02 - time01);
            if (i % 25_000 == 0) myCache.forEach(entry -> { });
//            if (i%10_000 == 0) maxSize = Math.max(maxSize, myCache.getSize());

            for (int j=0;  j<20_000;  j+=111) {
                key = String.valueOf(j);
                time01 = System.nanoTime();
                what = myCache.get(key);
                time02 = System.nanoTime();
                timeGet += (time02 - time01);

                if (what == null) {
                    count01++;
                } else {
                    count02++;
                }
            }
        }
        timeEnd = System.nanoTime();

        System.out.println("EH-USER-MANAGED-CACHE-WITH-LISTENERS STATISTICS ...");
        System.out.println("---------------------------------------------------");
        System.out.println("MaxSize      : " + maxSize);
        System.out.println("Missing count: " + count01);
        System.out.println("  Found count: " + count02);
        System.out.println("     TOT time: " + ((timeEnd - timeStart) / 1_000_000.0));
        System.out.println("     PUT time: " + (timePut / 1_000_000.0));
        System.out.println("     GET time: " + (timeGet / 1_000_000.0));
        System.out.println("    Evict Cnt: " + evictCnt);
        System.out.println("   Expire Cnt: " + expireCnt);
    }
}
