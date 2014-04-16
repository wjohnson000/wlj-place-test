package org.familysearch.std.wlj.cache;

public class TestEHCacheIndexing {

    static EHCacheIndexing.EHCacheBuilder<String,MyObject> builder = new EHCacheIndexing.EHCacheBuilder<String,MyObject>();
    static EHCacheIndexing<String,MyObject> myCache;
    static {
        builder.setCacheName("myCache");
        builder.setTimeToLive(1);
        builder.setTimetoIdle(6);
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
    }
}
