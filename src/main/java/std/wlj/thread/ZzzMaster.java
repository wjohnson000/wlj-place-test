package std.wlj.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class ZzzMaster {
    public static void main(String... args) throws InterruptedException {
        ExecutorService exSvc = Executors.newFixedThreadPool(10);

        for (int i=0;  i<100;  i++) {
            exSvc.submit(new ZzzWorker(1250));
        }

        exSvc.shutdown();
        System.out.println("SHUT? " + exSvc.isShutdown());
        System.out.println("TERM? " + exSvc.isTerminated());

        exSvc.awaitTermination(10, TimeUnit.MINUTES);

        System.out.println("SHUT? " + exSvc.isShutdown());
        System.out.println("TERM? " + exSvc.isTerminated());
        System.out.println("ALL DONE DONE DONE ...");
    }
}
