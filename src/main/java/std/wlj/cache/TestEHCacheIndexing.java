package std.wlj.cache;

public class TestEHCacheIndexing {

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
        Thread[] threads = new Thread[4];
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
                        if (i%10_000 == 0) maxSize = Math.max(maxSize, myCache.size());

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

                    System.out.println();
                    System.out.println("EH-CACHE-IDX STATISTICS ...");
                    System.out.println("---------------------------");
                    System.out.println("MaxSize      : " + maxSize);
                    System.out.println("Missing count: " + count01);
                    System.out.println("  Found count: " + count02);
                    System.out.println("     TOT time: " + ((timeEnd - timeStart) / 1_000_000.0));
                    System.out.println("     PUT time: " + (timePut / 1_000_000.0));
                    System.out.println("     GET time: " + (timeGet / 1_000_000.0));
                }
            });
            threads[i].start();
        }
    }
}
