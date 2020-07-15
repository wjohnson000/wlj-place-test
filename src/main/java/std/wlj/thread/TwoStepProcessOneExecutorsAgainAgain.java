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
public class TwoStepProcessOneExecutorsAgainAgain {

     public static void main(String...args) {
        long time0a = System.nanoTime();
        ScheduledExecutorService theService = getExecutor();
        for (int i=0;  i<5000;  i++) {
            theService.execute(() -> task01());
        }

        try {
            theService.shutdown();
            theService.awaitTermination(120, TimeUnit.MINUTES);
        } catch (InterruptedException ex) {
            System.out.println("Step01.Executor Service Error: " + ex.getMessage());
        }
        long time1a = System.nanoTime();
        System.out.println("STEP 01 -- Time: " + (time1a - time0a) / 1_000_000.0);

        long time0b = System.nanoTime();
        theService = getExecutor();
        for (int i=0;  i<5000;  i++) {
            theService.execute(() -> task02());
        }

        try {
            theService.shutdown();
            theService.awaitTermination(120, TimeUnit.MINUTES);
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

    static ScheduledExecutorService getExecutor() {
        return Executors.newScheduledThreadPool(
                                8,
                                runn -> {
                                    Thread thr = Executors.defaultThreadFactory().newThread(runn);
                                    thr.setDaemon(true);
                                    return thr;
                                });

    }
}
