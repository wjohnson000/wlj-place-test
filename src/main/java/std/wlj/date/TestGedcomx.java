/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.GenDate;

/**
 * @author wjohnson000
 *
 */
public class TestGedcomx {

    static final String[] gedcomxx = {
        "+1780-02-02",
        "+1780-03-03",
        "+1780-04-04",
        "+1780-05-05",
    };

    public static void main(String... args) throws Exception {
        long time0 = System.nanoTime();

        for (String text : gedcomxx) {
            GenDate date = DateUtil.fromGedcomX(text);
            System.out.println("Text:" + text + "  --> Date: " + date);
        }

        long time1 = System.nanoTime();
        System.out.println("  " + (time1 - time0) / 1_000_000.0);
    }
}