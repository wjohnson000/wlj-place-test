/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.time.LocalDate;

/**
 * Most of this code is adapted from code Copyright (c) 1992-2001 Dandelion Corporation, Licensed to
 * Intellectual Reserve, Inc.
 * <p/>
 * The Gregorian Calendar was introduced in 1582 by Pope Gregory XIII.  It was legally adopted in Great
 * Britain and the colonies in 1752.
 * <p/>
 * Gregorian algorithms below were developed by P T Blake, Dandelion Corporation. See also Robert Tantzen,
 * Communications of the ACM, v6, #8, August, 1963, p. 444. See also Fliegel and van Flandern,CACM, v11,
 * #10, p. 657. Julian Day 0 == 1 Jan 4713 B.C. (Julian calendar) == 24 Nov -4712 (Gregorian)
 * 
 * @author Pete Blake
 * @author Wayne Johnson, May, 2018 (reworked)
 */

public abstract class CalendarUtil {

    // number of days in each month in this calendar
    protected static final short DAYS_IN_MONTH[] = {
        0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31
    };

    // number of days from the 1st of March to the 1st of month m; March is 0
    protected static final short DAYS_SINCE_MARCH_1[] = {
        0, 31, 61, 92, 122, 153, 184, 214, 245, 275, 306, 337, 367
    };

    /**
     * Disable constructor on a Util class
     */
    private CalendarUtil() { }

    public static LocalDate julianToGregorian(int julianDayNumber) {
        return LocalDate.MIN.with(java.time.temporal.JulianFields.JULIAN_DAY , julianDayNumber);
    }

    /**
     * This overrides the Julian version
     */
    public static boolean isLeapYear(int y) {
        // if BC, convert year to internal numbering
        if (y < 0) {
            y += 1 + 4800; // convert 1 BC from -1 to 0, etc., make it positive
        }

        return y % 400 == 0  ||  (y % 4 == 0  &&  y % 100 != 0);
    }

    public static boolean isValidDay(int d, int m, int y) {
        // return TRUE if d is a valid day in month m and year y
        if (! isValidMonth(m, y)) {
            return false;
        }

        int max = DAYS_IN_MONTH[m];
        if (m == 2  &&  isLeapYear(y)) {
            max++;
        }
        return d >= 1  &&  d <= max;
    }

    public static boolean isValidYear(int y) {
        return y >= -4000  &&  y <= 3000 &&  y != 0;
    }

    public static boolean isValidMonth(int m, int y) {
        return m >= 1  &&  m <= 12;
    }

    public static AstroDay dayFromDayMonth(int day, int month) {
        int d = day;
        int m = month;

        // special case where year is missing -- do a simple calculation for a JDay
        if (m > 2) {
            m -= 3;
        } else {
            m += 9;
        }

        // Jan and Feb are now month 10 and 11.  Calculate the # days from 1st of Mar to 1st of
        // month m.  Then add the # of days in the month (if the day is 0, use 1)
        d = Math.max(d, 1);
        return AstroDay.getInstance(getDaysSinceMarch01(m)+ d);
    }

    public static AstroDay vernalEquinox(int year) {
        int d = 21;
        int m = 0;  // Renumber months, so that March is month 0
        int y = year;

        if (y == 0  ||  y < -4001  ||  y > 3000) {
            return AstroDay.INVALID;
        }

        // convert year to internal numbering
        if (y < 0) {
            y++; // make 1 BC == 0, 2 BC == -1, etc.
        }
        y += 4800; // add a big multiple of 400 so y is positive

        // Leap years happen every 4 years -- 4, 8, etc. so we can shift right 2 bits to divide by 4.
        // Leap year happens every 4 years except not in century years, but century years divisible by
        // 400 are leap years, so 1700, 1800, 1900 are not leap years, but 2000 is.
        int c = y / 100; // # of whole centuries
        return AstroDay.getInstance(d + getDaysSinceMarch01(m) + y * 365 + (y >> 2) - c + (c >> 2)
                + 1721119    // day # of 29 Feb 0000 (1 BC)
                - 1753164);  // remove the 4800 year offset ( = 12*146097 )
    }

    protected static int getDaysSinceMarch01(int m) {
        if (m < DAYS_SINCE_MARCH_1.length) {
            return DAYS_SINCE_MARCH_1[m];
        } else {
            return 0;
        }
    }

    protected static int getDaysInMonth(int m) {
        if (m < DAYS_IN_MONTH.length) {
            return DAYS_IN_MONTH[m];
        }
        return 0;
    }

}
