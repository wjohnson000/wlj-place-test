/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import org.familysearch.standards.date.calendar.CalendarUtil;
import org.familysearch.standards.date.engine.CJKCalendarTable;

/**
 * @author wjohnson000
 *
 */
public class TestJulianDayOfVernalEquinox {

    public static void main(String...args) {
        System.out.println("-3: " + CalendarUtil.julianDayOfVernalEquinox(-3));
        System.out.println("-2: " + CalendarUtil.julianDayOfVernalEquinox(-2));
        System.out.println("-1: " + CalendarUtil.julianDayOfVernalEquinox(-1));
        System.out.println(" 0: " + CalendarUtil.julianDayOfVernalEquinox( 0));
        System.out.println(" 1: " + CalendarUtil.julianDayOfVernalEquinox( 1));
        System.out.println(" 2: " + CalendarUtil.julianDayOfVernalEquinox( 2));

        System.out.println("-3: " + CJKCalendarTable.getInstance().getLunarYear(-3).getJDay());
        System.out.println("-2: " + CJKCalendarTable.getInstance().getLunarYear(-2).getJDay());
        System.out.println("-1: " + CJKCalendarTable.getInstance().getLunarYear(-1).getJDay());
        System.out.println(" 0: " + CJKCalendarTable.getInstance().getLunarYear( 0).getJDay());
        System.out.println(" 1: " + CJKCalendarTable.getInstance().getLunarYear( 1).getJDay());
        System.out.println(" 2: " + CJKCalendarTable.getInstance().getLunarYear( 2).getJDay());
    }
}
