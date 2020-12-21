package std.wlj.thread;

public class ThreadLocalOne {

    private static final ThreadLocal<Long> myTLL = new ThreadLocal<>();

    public Long getThreadValue() {
        Long myVal = myTLL.get();
        if (myVal == null) {
            myVal = System.nanoTime();
            myTLL.set(myVal);
        }
        return myVal;
    }
}
