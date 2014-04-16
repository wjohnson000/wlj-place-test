package org.familysearch.std.wlj.cache;


public class TestMyLRUCache {

    static MyLRUCache<String,MyObject> myCache = new MyLRUCache<String,MyObject>(10000, 1, 60);
    static long evictCnt = 0;
    static long expireCnt = 0;

    public static void main(String... args) {

        myCache.addListener(new MyLRUCache.CacheListener<String>() {

            @Override
            public void expiredElement(String key) {
                expireCnt++;
            }

            @Override
            public void evictedElement(String key) {
                evictCnt++;
            }
        });

        Thread[] threads = new Thread[1];
        for (int i=0;  i<threads.length;  i++) {
            threads[i] = new Thread(new Runnable() {
                @Override public void run() {
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
                        nnow = System.nanoTime();
                        myCache.put(key, what, 1);
                        then = System.nanoTime();
                        time01 += (then - nnow);
                        if (i%10000 == 0) maxSize = Math.max(maxSize, myCache.size());

                        for (int j=0;  j<20000;  j+=111) {
                            key = String.valueOf(j);
                            nnow = System.nanoTime();
                            what = myCache.getIfNotExpired(key);
                            then = System.nanoTime();
                            time02 += (then - nnow);

                            if (what == null) {
                                count01++;
                            } else {
                                count02++;
                            }
                        }
                    }

                    System.out.println("MY-LRU-CACHE STATISTICS ...");
                    System.out.println("---------------------------");
                    System.out.println("MaxSize      : " + maxSize);
                    System.out.println("Missing count: " + count01);
                    System.out.println("  Found count: " + count02);
                    System.out.println("  Total count: " + (count01 + count02));
                    System.out.println("     PUT time: " + (time01 / 1000000.0));
                    System.out.println("     GET time: " + (time02 / 1000000.0));
                    System.out.println("    Evict Cnt: " + evictCnt);
                    System.out.println("   Expire Cnt: " + expireCnt);
                }
            });
            threads[i].start();
        }
    }
}
