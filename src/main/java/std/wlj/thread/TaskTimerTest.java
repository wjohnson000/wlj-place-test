package std.wlj.thread;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class TaskTimerTest {

    private Random random = new Random();
    private Timer myTimer = new Timer("my-timer", true);

    public static void main(String...arsg) {
        TaskTimerTest me = new TaskTimerTest();
        me.myTask();

        me.checkThreadCount();
    }

    private void myTask() {
        long sleepTime = random.nextInt(1_000_000) / 100L;
        System.out.println("Running 'myTask()' -- sleep for " + sleepTime);
        try { Thread.sleep(sleepTime); } catch(InterruptedException ex) { }

        TimerTask task = new TimerTask() {
            @Override public void run() {
                myTask();
            }
        };
        myTimer.schedule(task, 5000L);
    }
    
    private void checkThreadCount() {
        while(true) {
            try { Thread.sleep(500L); } catch(InterruptedException ex) { }
            System.out.println("Threads: " + Thread.getAllStackTraces().size());
        }
    }
}
