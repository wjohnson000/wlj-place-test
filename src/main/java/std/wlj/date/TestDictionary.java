/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.List;

import org.familysearch.standards.core.lang.dict.Dictionary;
import org.familysearch.standards.core.lang.dict.Word;
import org.familysearch.standards.date.shared.MonthDictionary;

/**
 * @author wjohnson000
 *
 */
public class TestDictionary {

    static String[] moNams = {
        "January",
        "February",
        "March",
        "April",
        "May",
        "June",
        "July",
        "August",
        "September",
        "October",
        "November",
        "December",
    };

    static String[] moNums = {
        "01",
        "02",
        "03",
        "04",
        "05",
        "06",
        "07",
        "08",
        "09",
        "10",
        "11",
        "12",
    };

    public static void main(String...args) {
        Dictionary dict = MonthDictionary.getMonthDictionary();

        // Seed the silly thing ...
        @SuppressWarnings("unused")
        List<Word> feb = dict.findWords(null, "02", "en");

        for (String name : moNams) {
            long time0 = System.nanoTime();
            List<Word> mo = dict.findWords(name, null, "en");
            long time1 = System.nanoTime();

            System.out.println("\n>>>>>> NAME: " + name + " --> " + (time1 - time0) / 1_000_000.0);
            mo.forEach(System.out::println);
        }

        for (String num : moNums) {
            long time0 = System.nanoTime();
            List<Word> mo = dict.findWords(null, num, "en");
            long time1 = System.nanoTime();

            System.out.println("\n>>>>>> NUMB: " + num + " --> " + (time1 - time0) / 1_000_000.0);
            mo.forEach(System.out::println);
        }

        for (String num : moNums) {
            long time0 = System.nanoTime();
            List<Word> mo = dict.findWords(null, num, "en");
            long time1 = System.nanoTime();

            System.out.println("\n>>>>>> NUMB: " + num + " --> " + (time1 - time0) / 1_000_000.0);
            mo.forEach(System.out::println);
        }
    }
}
