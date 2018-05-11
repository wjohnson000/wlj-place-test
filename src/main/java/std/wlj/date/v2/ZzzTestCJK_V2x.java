/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.time.LocalDate;

/**
 * @author wjohnson000
 *
 */
public class ZzzTestCJK_V2x {

    static int[] years = { 128, 1335 };

    public static void main(String...args) {
        CJKCalendarTable cjkTable = CJKCalendarTable.getInstance();

        for (int year : years) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println("YYR: " + year);
            LunarYearDescription lyd = cjkTable.getLunarYear(year);
            AstroDay astro = CJKCalendar.dayFromDayMonthYear(7, 7, year, lyd.getLeapMoon());
            LocalDate date = CalendarUtil.julianToGregorian(astro.value());
            System.out.println("V2 >> YR=" + date.getYear() + ";  MO=" + date.getMonthValue() + ";  DY=" + date.getDayOfMonth() + " --> " + date);
            System.out.println();
        }
    }
}
