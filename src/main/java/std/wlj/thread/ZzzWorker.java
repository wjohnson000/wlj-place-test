package std.wlj.thread;

public class ZzzWorker implements Runnable {
    long sleepTime;

    public ZzzWorker(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public void run() {
        System.out.println("THR." + Thread.currentThread().getName() + " doing some work ...");
        try { Thread.sleep(sleepTime); } catch(Exception ex) { }
        System.out.println("THR." + Thread.currentThread().getName() + " done with its work ...");
    }
}
