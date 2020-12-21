/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.util.Arrays;
import java.util.List;

/**
 * @author wjohnson000
 *
 */
public class ListToArray {

    public static void main(String...args) {
        long timeA = 0;
        long timeB = 0;
        long timeC = 0;
        long timeD = 0;
        long timeE = 0;
        long timeF = 0;
        long timeG = 0;

        for (int i=0;  i<10_000_000;  i++) {
            List<String> stuff = Arrays.asList("one", String.valueOf(i), "two", String.valueOf(i+1), "three", String.valueOf(i+2), "four", String.valueOf(i+3));

            long time0 = System.nanoTime();

            Object[] array01 = stuff.toArray();
            long time1 = System.nanoTime();

            String[] array02 = stuff.toArray(new String[0]);
            long time2 = System.nanoTime();

            String[] array03 = stuff.toArray(new String[] { });
            long time3 = System.nanoTime();

            String[] array04 = stuff.toArray(new String[stuff.size()]);
            long time4 = System.nanoTime();

            Object[] array05 = stuff.stream().toArray();
            long time5 = System.nanoTime();

            String[] array06 = stuff.stream().toArray(String[]::new);
            long time6 = System.nanoTime();

            String[] array07 = stuff.stream().toArray(n -> new String[n]);
            long time7 = System.nanoTime();

            timeA += (time1 - time0);
            timeB += (time2 - time1);
            timeC += (time3 - time2);
            timeD += (time4 - time3);
            timeE += (time5 - time4);
            timeF += (time6 - time5);
            timeG += (time7 - time6);

            if (array01 == null) System.out.println("OOPS ... A");
            if (array02 == null) System.out.println("OOPS ... B");
            if (array03 == null) System.out.println("OOPS ... C");
            if (array04 == null) System.out.println("OOPS ... D");
            if (array05 == null) System.out.println("OOPS ... E");
            if (array06 == null) System.out.println("OOPS ... F");
            if (array07 == null) System.out.println("OOPS ... G");
        }

        System.out.println("TimeA: " + timeA / 1_000_000.0);
        System.out.println("TimeB: " + timeB / 1_000_000.0);
        System.out.println("TimeC: " + timeC / 1_000_000.0);
        System.out.println("TimeD: " + timeD / 1_000_000.0);
        System.out.println("TimeE: " + timeE / 1_000_000.0);
        System.out.println("TimeF: " + timeF / 1_000_000.0);
        System.out.println("TimeG: " + timeG / 1_000_000.0);
    }
}
