package std.wlj.cache;

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
        builder.setTimetoIdle(6);
        builder.setMaxElements(10_000);
        myCache = builder.build();
    }

    public static void main(String... args) {

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

        Thread[] threads = new Thread[1];
        for (int i=0;  i<threads.length;  i++) {
            threads[i] = new Thread(new Runnable() {
                @Override public void run() {
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
                        if (i%10000 == 0) maxSize = Math.max(maxSize, myCache.size());

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

                    System.out.println("EH-CACHE-IDX-LISTENER STATISTICS ...");
                    System.out.println("------------------------------------");
                    System.out.println("MaxSize      : " + maxSize);
                    System.out.println("Missing count: " + count01);
                    System.out.println("  Found count: " + count02);
                    System.out.println("     TOT time: " + ((timeEnd - timeStart) / 1_000_000.0));
                    System.out.println("     PUT time: " + (timePut / 1_000_000.0));
                    System.out.println("     GET time: " + (timeGet / 1_000_000.0));
                    System.out.println("    Evict Cnt: " + evictCnt);
                    System.out.println("   Expire Cnt: " + expireCnt);
                    System.out.println("      Put Cnt: " + putCnt);
                    System.out.println("   Remove Cnt: " + removeCnt);
                }
            });
            threads[i].start();
        }
    }
}
