/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import org.familysearch.standards.date.GenSimpleDate;
import org.familysearch.standards.date.exception.GenDateException;

/**
 * @author wjohnson000
 *
 */
public class TestGenDateSimple {

    public static void main(String...args) throws GenDateException {
        GenSimpleDate gdsX = new GenSimpleDate(false, 1999, 11, 12, 0, 0, 0);
        System.out.println("SEED: " + gdsX);
        System.out.println("   G: " + gdsX.toGEDCOMX());
        System.out.println("  K1: " + gdsX.toSortableKey());
        System.out.println("  K2: " + gdsX.toSortableShortKey());

        long total1 = 0L;
        long total2 = 0L;
        long total3 = 0L;

        for (int month=1;  month<12;  month++) {
            for (int day=1;  day<29;  day++) {
                GenSimpleDate gds = new GenSimpleDate(false, 1999, month, day, 0, 0, 0);
                long time0 = System.nanoTime();
                String gedcomx = gds.toGEDCOMX();
                long time1 = System.nanoTime();
                String sort000 = gds.toSortableKey();
                long time2 = System.nanoTime();
                String sort111 = gds.toSortableShortKey();
                long time3 = System.nanoTime();

                total1 += time1 - time0;
                total2 += time2 - time1;
                total3 += time3 - time2;

                System.out.println("\n========================================================");
                System.out.println("GDS: " + gds);
                System.out.println("     " + gedcomx + " --> " + (time1 - time0) / 1_000_000.0);
                System.out.println("     " + sort000 + " --> " + (time2 - time1) / 1_000_000.0);
                System.out.println("     " + sort111 + " --> " + (time3 - time2) / 1_000_000.0);
            }
        }

        System.out.println("\n\n\n");
        System.out.println("TOTAL1: " + total1 / 1_000_000.0);
        System.out.println("TOTAL2: " + total2 / 1_000_000.0);
        System.out.println("TOTAL3: " + total3 / 1_000_000.0);
    }
}
