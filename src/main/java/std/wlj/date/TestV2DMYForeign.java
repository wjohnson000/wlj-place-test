/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.model.DateResult;
import org.familysearch.standards.date.model.GenDateInterpResult;

/**
 * @author wjohnson000
 *
 */
public class TestV2DMYForeign {

    private static final Random random = new Random();

    private static final String[] MONTH_NAMES = {
        "Jan",
        "January",
        "Feb",
        "February",
        "Mar",
        "March",
        "Apr",
        "April",
        "May",
        "May",
        "Jun",
        "June",
        "Jul",
        "July",
        "Aug",
        "August",
        "Sep",
        "September",
        "Oct",
        "October",
        "Nov",
        "November",
        "Dec",
        "December",
    };

    public static void main(String... args) throws Exception {
        List<String> results = new ArrayList<>();

        List<String> textes = textesFromRaw(100_000);
        long time0 = System.nanoTime();
        for (String text : textes) {
            try {
                DateResult dateResult = DateUtil.interpDate(text, StdLocale.ENGLISH);

                results.add("");
                for (GenDateInterpResult date : dateResult.getDates()) {
                    System.out.println("  gx02: " + text + "|" + date.getDate().toGEDCOMX());
                    results.add(text + "|" + date.getDate().toGEDCOMX());
                }
            } catch (Exception e) {
                System.out.println("  V2.ext: " + e.getMessage());
            }
        }
        long time1 = System.nanoTime();
        System.out.println("\nTIME: " + (time1 - time0) / 1_000_000.0);
    }

    static List<String> textesFromRaw(int count) {
        List<String> textes = new ArrayList<>();

        for (int ndx=0;  ndx<count;  ndx++) {
            int yer = random.nextInt(1000) + 1100;
            int mon = random.nextInt(MONTH_NAMES.length);
            int day = random.nextInt(20) + 1;
            if (ndx % 2 == 0) {
                textes.add(day + " " + MONTH_NAMES[mon] + " " + yer);
            } else {
                textes.add(day + "+" + MONTH_NAMES[mon] + "+" + yer);
            }
        }

        return textes;
    }
}