package org.familysearch.std.wlj.cache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

public class TestEHCacheIndexingWithListener {

    static long evictCnt = 0;
    static long expireCnt = 0;
    static long putCnt = 0;
    static long removeCnt = 0;

    static EHCacheIndexing.EHCacheBuilder<String,MyObject> builder = new EHCacheIndexing.EHCacheBuilder<String,MyObject>();
    static EHCacheIndexing<String,MyObject> myCache;
    static {
        builder.setCacheName("myCache");
        builder.setTimeToLive(1);
        builder.setTimetoIdle(10);
        builder.setMaxElements(10000);
        myCache = builder.build();
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

        // Create and register a listener ...
        myCache.getCache().getCacheEventNotificationService().registerListener(new CacheEventListener() {

            @Override
            public void dispose() {
            }

            @Override
            public void notifyElementEvicted(Ehcache arg0, Element arg1) {
                evictCnt++;
            }

            @Override
            public void notifyElementExpired(Ehcache arg0, Element arg1) {
                expireCnt++;
            }

            @Override
            public void notifyElementPut(Ehcache arg0, Element arg1) throws CacheException {
                putCnt++;
            }

            @Override
            public void notifyElementRemoved(Ehcache arg0, Element arg1) throws CacheException {
                removeCnt++;
            }

            @Override
            public void notifyElementUpdated(Ehcache arg0, Element arg1) throws CacheException {
            }

            @Override
            public void notifyRemoveAll(Ehcache arg0) {
            }
            @Override
            public Object clone() throws CloneNotSupportedException {
                throw new CloneNotSupportedException("Blah ...");
            };
        });
        for (int i=0;  i<200000;  i++) {
            what = MyObject.getInstance();
            String key = String.valueOf(what.key);
            nnow = System.nanoTime();
            myCache.put(key, what);
            then = System.nanoTime();
            time01 += (then - nnow);
            if (i%10000 == 0) maxSize = Math.max(maxSize, myCache.size());

            for (int j=0;  j<20000;  j+=111) {
                key = String.valueOf(j);
                nnow = System.nanoTime();
                what = myCache.get(key);
                then = System.nanoTime();
                time02 += (then - nnow);

                if (what == null) {
                    count01++;
                } else {
                    count02++;
                }
            }
        }

        System.out.println("EH-CACHE-IDX STATISTICS ...");
        System.out.println("---------------------------");
        System.out.println("MaxSize      : " + maxSize);
        System.out.println("Missing count: " + count01);
        System.out.println("  Found count: " + count02);
        System.out.println("     PUT time: " + (time01 / 1000000.0));
        System.out.println("     GET time: " + (time02 / 1000000.0));
        System.out.println("    Evict Cnt: " + evictCnt);
        System.out.println("   Expire Cnt: " + expireCnt);
        System.out.println("      Put Cnt: " + putCnt);
        System.out.println("   Remove Cnt: " + removeCnt);
    }
}
