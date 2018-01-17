package std.wlj.thread;

public class ThreadLocalTwo {

    private static final ThreadLocal<String> myTLS = new ThreadLocal<>();
    private static final ThreadLocal<String> myTLC = new ThreadLocal<>();

    public String getThreadValueS(String initial) {
        String myVal = myTLS.get();
        if (myVal == null) {
            myVal = initial;
            myTLS.set(myVal);
        }
        return myVal;
    }

    public String getThreadValueC(String newValue) {
        String myVal = myTLC.get();
        if (myVal == null) {
            myVal = newValue;
            myTLC.set(myVal);
        } else {
            myTLC.set(newValue);
        }
        return myVal;
    }
}
