/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import org.familysearch.standards.date.cjk.CJKCalendarUtil;
import org.familysearch.standards.date.cjk.DMY;

/**
 * @author wjohnson000
 *
 */
public class CalcJulianDay {

    public static void main(String...args) {
        // Should be -- 2145641
        // Returns   -- 2145648
        int jday = CJKCalendarUtil.dayFromDayMonthYear(5, 5, 1162);
        System.out.println("JDAY: " + jday);

        DMY dmyJ = CJKCalendarUtil.dmyJulian(jday);
        System.out.println("DMY: " + dmyJ.getYear() + " . " + dmyJ.getMonth() + " . " + dmyJ.getDay() + " . " + dmyJ.isIntercalary());

        DMY dmyG = CJKCalendarUtil.dmyGregorian(jday);
        System.out.println("DMY: " + dmyG.getYear() + " . " + dmyG.getMonth() + " . " + dmyG.getDay() + " . " + dmyG.isIntercalary());
    }
}
