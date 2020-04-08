/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import org.familysearch.standards.GenealogicalDate;

/**
 * @author wjohnson000
 *
 */
public class TestGenealogicalDate {

    public static void main(String...args) {
        System.out.println("Here Goes ... Nothing!!");

        tryThis("25 December 1900");
        tryThis("25 Dec 1900");
        tryThis("December 25, 1900");
        tryThis("Dec 25, 1900");
        tryThis("12/25/1900");
        tryThis("25/12/1900");
        tryThis("1900-12-25");
        tryThis("December 1900");
        tryThis("1900");
        tryThis("From December 1900 to March 1910");
        tryThis("From 1900 to 2000");
        tryThis("1900-2000");
    }

    static void tryThis(String dateStr) {
        System.out.println("\n=============================================================================");
        System.out.println("STR: " + dateStr);

        try {
            GenealogicalDate date = GenealogicalDate.getInstance(dateStr);
            System.out.println(" GD: " + date);
        } catch(Exception ex) {
            System.out.println(" EX: " + ex.getMessage());
        }
    }
}
