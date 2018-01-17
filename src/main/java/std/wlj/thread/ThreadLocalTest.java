package std.wlj.thread;

public class ThreadLocalTest {

    public static void main(String... args) {
        ThreadLocalOne tto = new ThreadLocalOne();
        ThreadLocalTwo ttt = new ThreadLocalTwo();

        for (int i=0;  i<3;  i++) {
            String name = "thrx." + i;
            Thread thr = new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " --> " + "TT1: " + tto.getThreadValue());
                System.out.println(Thread.currentThread().getName() + " --> " + "TT2: " + ttt.getThreadValueS("first-value"));
                System.out.println(Thread.currentThread().getName() + " --> " + "TT3: " + ttt.getThreadValueC("some-value-C"));
                try { Thread.sleep(55L); } catch(Exception ex) { }
                System.out.println(Thread.currentThread().getName() + " --> " + "TT1: " + tto.getThreadValue());
                System.out.println(Thread.currentThread().getName() + " --> " + "TT2: " + ttt.getThreadValueS("first-other-value"));
                System.out.println(Thread.currentThread().getName() + " --> " + "TT3: " + ttt.getThreadValueC("some-other-value-C"));
                try { Thread.sleep(55L); } catch(Exception ex) { }
                System.out.println(Thread.currentThread().getName() + " --> " + "TT1: " + tto.getThreadValue());
                System.out.println(Thread.currentThread().getName() + " --> " + "TT2: " + ttt.getThreadValueS("first-other-other-value"));
                System.out.println(Thread.currentThread().getName() + " --> " + "TT3: " + ttt.getThreadValueC("some-other-other-value-C"));
            },
            name);
            thr.start();
        }
    }
}
