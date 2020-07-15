/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.thread;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Run a bunch of processes using one executor service, then run a bunch of processes using a
 * second executor service.
 * 
 * @author wjohnson000
 *
 */
public class TwoStepProcessTwoExecutors {

    /** Create a pool for an executor service, with all threads being "daemon=true" */
    private static final ScheduledExecutorService step01Service =
                            Executors.newScheduledThreadPool(
                                8,
                                runn -> {
                                    Thread thr = Executors.defaultThreadFactory().newThread(runn);
                                    thr.setDaemon(true);
                                    return thr;
                                });

    /** Create a pool for an executor service, with all threads being "daemon=true" */
    private static final ScheduledExecutorService step02Service =
                            Executors.newScheduledThreadPool(
                                8,
                                runn -> {
                                    Thread thr = Executors.defaultThreadFactory().newThread(runn);
                                    thr.setDaemon(true);
                                    return thr;
                                });

    public static void main(String...args) {
        long time0a = System.nanoTime();
        for (int i=0;  i<5000;  i++) {
            step01Service.execute(() -> task01());
        }

        try {
            step01Service.shutdown();
            step01Service.awaitTermination(120, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            System.out.println("Step01.Executor Service Error: " + ex.getMessage());
        }
        long time1a = System.nanoTime();
        System.out.println("STEP 01 -- Time: " + (time1a - time0a) / 1_000_000.0);

        long time0b = System.nanoTime();
        for (int i=0;  i<5000;  i++) {
            step02Service.execute(() -> task02());
        }

        try {
            step02Service.shutdown();
            step02Service.awaitTermination(120, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            System.out.println("Step02.Executor Service Error: " + ex.getMessage());
        }
        long time1b = System.nanoTime();
        System.out.println("STEP 02 -- Time: " + (time1b - time0b) / 1_000_000.0);
    }

    static void task01() {
        int sum = 0;
        for (int i=1;  i<=100_000;  i++) {
            sum += i;
        }
        System.out.println(Thread.currentThread().getName() + ".sum: " + sum);
        try { Thread.sleep(5L); } catch(Exception ex) { }
    }

    static void task02() {
        int mult = 1;
        for (int i=1;  i<=100_000;  i++) {
            mult *= i;
        }
        try { Thread.sleep(5L); } catch(Exception ex) { }
        System.out.println(Thread.currentThread().getName() + ".mult: " + mult);
    }
}
