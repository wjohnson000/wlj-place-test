package std.wlj.marshal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Do some timing w/ regular "synchronized" block and a DCL block.
 * 
 * @author wjohnson000
 *
 */
public class TestDCL {

    volatile boolean isConfigured = false;

    public static void main(String... args)  throws InterruptedException {
        TestDCL me = new TestDCL();
        me.runDCLSync();
    }

    void runRegularSync() throws InterruptedException {
        System.out.println("Regular 'synch' ...");
        runRunnable(40, () -> {
            regularSynch();
        });
    }

    void runDCLSync() throws InterruptedException {
        System.out.println("DCL 'synch' ...");
        runRunnable(40, () -> {
            dclSynch();
        });
    }

    void runRunnable(int thrCount, Runnable runn) throws InterruptedException {
        ExecutorService exService = Executors.newFixedThreadPool(thrCount);

        long then = System.nanoTime();
        for (int i=0;  i<thrCount;  i++) {
            exService.submit(runn);
        }

        exService.shutdown();
        exService.awaitTermination(320, TimeUnit.SECONDS);

        long nnow = System.nanoTime();
        System.out.println("Time: " + (nnow - then) / 1_000_000.0);
    }

    void regularSynch() {
        synchronized(this) {
            if (! isConfigured) {
                fakeInit();
                isConfigured = true;
            }
        }
    }

    void dclSynch() {
        if (! isConfigured) {
            synchronized(this) {
                if (! isConfigured) {
                    fakeInit();
                    isConfigured = true;
                }
            }
        }
    }

    static void fakeInit() {
        System.out.println(">>>>>>>>> INIT INIT INIT INIT INIT <<<<<<<<<<");
        try { Thread.sleep(1111L); } catch(Exception ex) { }
    }
}
