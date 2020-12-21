/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.familysearch.standards.core.StdLocale;

/**
 * @author wjohnson000
 *
 */
public class TestThreadedMap {

    private static ExecutorService execService;

    private static final Map<String, StdLocale> localeCacheNT = new HashMap<>();
    private static final Map<String, StdLocale> localeCacheTH = new ConcurrentHashMap<>();

    public static void main(String...args) throws InterruptedException {
        seedSeed();

        long time0 = System.nanoTime();
        testThreaded();
        long time1 = System.nanoTime();
        testNormal();
        long time2 = System.nanoTime();

        System.out.println("TIME.thrd=" + (time1 - time0) / 1_000_000.0);
        System.out.println("TIME.norm=" + (time2 - time1) / 1_000_000.0);
    }

    static void seedSeed() {
        long time0 = System.nanoTime();
        for (char ch='a';  ch<='z';  ch++) {
            StdLocale locale = new StdLocale("" + ch + ch);
        }
        long time1 = System.nanoTime();
        System.out.println("TIME.seed=" + (time1 - time0) / 1_000_000.0);
    }

    static void testNormal() throws InterruptedException {
        testWith(localeCacheNT);
    }

    static void testThreaded() throws InterruptedException {
        testWith(localeCacheTH);
    }

    static void testWith(Map<String, StdLocale> cache) throws InterruptedException {
        execService = Executors.newFixedThreadPool(32);

        for (int i=0;  i<32;  i++) {
            execService.submit(() -> testCache(cache));
        }

        execService.shutdown();
        execService.awaitTermination(60, TimeUnit.MINUTES);
        execService = null;
    }

    static void testCache(Map<String, StdLocale> cache) {
        int localeCnt = 0;

        for (int cnt=0;  cnt<10_000;  cnt++) {
            for (char ch01='a';  ch01<='z';  ch01++) {
                for (char ch02='a';  ch02<='z';  ch02++) {
                    String localeStr = "" + ch01 + ch01;
                    StdLocale locale = cache.getOrDefault(localeStr, new StdLocale(localeStr));
                    if (locale == null) {
                        localeCnt++;
                    }
                }
            }
        }

        if (localeCnt > 0) System.out.println("LC: " + localeCnt);
    }
}
