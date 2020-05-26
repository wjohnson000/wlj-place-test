/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author wjohnson000
 *
 */
public class TestCopyWithout {

    public static void main(String... args) {
        oneOne();
        twoTwo();
        treTre();
        forFor();
        fivFiv();

        oneOne();
        twoTwo();
        treTre();
        forFor();
        fivFiv();

        oneOne();
        twoTwo();
        treTre();
        forFor();
        fivFiv();
    }

    static void oneOne() {
        long time0 = System.nanoTime();
        for (int i=1;  i<100_000;  i++) {
            String rnd = UUID.randomUUID().toString().replace("-", "");
            if (rnd == null) {
                System.out.println("OOPS!!");
            }
        }
        long time1 = System.nanoTime();
        System.out.println("ONE: " + (time1 - time0) / 1_000_000.0);
    }

    static void twoTwo() {
        long time0 = System.nanoTime();
        for (int i=1;  i<100_000;  i++) {
            String rnd = UUID.randomUUID().toString();
            StringBuilder buff = new StringBuilder(32);
            for (int j=0;  j<rnd.length();  j++) {
                if (rnd.charAt(j) != '-') {
                    buff.append(rnd.charAt(j));
                }
            }
        }
        long time1 = System.nanoTime();
        System.out.println("TWO: " + (time1 - time0) / 1_000_000.0);
    }

    static void treTre() {
        long time0 = System.nanoTime();
        for (int i=1;  i<100_000;  i++) {
            String rnd = UUID.randomUUID().toString().chars()
                             .filter(ch -> ch >= '0' && ch <= '9')
                             .mapToObj(ch -> String.valueOf(ch))
                             .collect(Collectors.joining());
            if (rnd == null) {
                System.out.println("OOPS!!");
            }
        }
        long time1 = System.nanoTime();
        System.out.println("TRE: " + (time1 - time0) / 1_000_000.0);
    }

    static void forFor() {
        long time0 = System.nanoTime();
        for (int i=1;  i<100_000;  i++) {
            String rnd = UUID.randomUUID().toString().chars()
                             .filter(ch -> ch >= '0' && ch <= '9')
                             .collect(StringBuilder::new,           // supplier
                                                     StringBuilder::appendCodePoint, // accumulator
                                                     StringBuilder::append)          // combiner
                                                .toString();
            if (rnd == null) {
                System.out.println("OOPS!!");
            }
        }
        long time1 = System.nanoTime();
        System.out.println("FOR: " + (time1 - time0) / 1_000_000.0);
    }

    static void fivFiv() {
        long time0 = System.nanoTime();
        for (int i=1;  i<100_000;  i++) {
            String rnd = String.valueOf(System.nanoTime());
            if (rnd == null) {
                System.out.println("OOPS!!");
            }
        }
        long time1 = System.nanoTime();
        System.out.println("FIV: " + (time1 - time0) / 1_000_000.0);
    }
}
