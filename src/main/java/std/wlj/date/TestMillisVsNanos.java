/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

/**
 * @author wjohnson000
 *
 */
public class TestMillisVsNanos {

    public static void main(String...args) {
        for (int i=1;  i<1111;  i++) {
            long millis0 = System.currentTimeMillis();
            long nanos0  = System.nanoTime();

            try { Thread.sleep(123L); } catch(Exception ex) { }

            long millis1 = System.currentTimeMillis();
            long nanos1  = System.nanoTime();
            double nanosD  = (nanos1 - nanos0) / 1_000_000.0;
            double nanos2  = (long)(nanosD * 100) / 100.0;

            double nanosDD = (nanos1 - nanos0) / 10_000 / 100.0;

            System.out.println("MMM: " + (millis1 - millis0));
            System.out.println(" NN: " + nanosD);
            System.out.println(" NN: " + nanos2);
            System.out.println(" NX: " + nanosDD);
        }
    }
}
