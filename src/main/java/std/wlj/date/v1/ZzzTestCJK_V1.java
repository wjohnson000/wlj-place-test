/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v1;

/**
 * @author wjohnson000
 *
 */
public class ZzzTestCJK_V1 {

    static int year = 1335;

    public static void main(String...args) {
        CJKCalendarTable cjkTable = CJKCalendarTable.getInstance();
        LunarYearDescription lyd = cjkTable.getLunarYear(year);
        System.out.println("LYD: " + lyd);
        System.out.println("  J: " + lyd.getJDay());
        System.out.println("  L: " + lyd.getLeapMoon());
        System.out.println("  M: " + lyd.getMoonBits());

        Calendar cal = ChineseCalendar.getInstance();
        AstroDay astro = cal.dayFromDayMonthYear(7, 7, year, lyd.getLeapMoon());
        System.out.println("AST: " + astro);
        System.out.println("  Y: " + cal.getYear(astro));
        System.out.println("  M: " + cal.getMonth(astro));
        System.out.println("  D: " + cal.getDay(astro));

        Calendar gCal = GregorianCalendar.getInstance();
        System.out.println("AST: " + astro);
        System.out.println("  Y: " + gCal.getYear(astro));
        System.out.println("  M: " + gCal.getMonth(astro));
        System.out.println("  D: " + gCal.getDay(astro));
    }
}
