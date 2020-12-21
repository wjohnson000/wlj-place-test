package std.wlj.date.v2;

/**
 * Most of this code is adapted from code Copyright (c) 1992-2001 Dandelion Corporation, Licensed
 * to Intellectual Reserve, Inc.
 * 
 * @author Pete Blake
 * @author Wayne Johnson, May, 2018 (reworked)
 */
public abstract class CJKCalendar {

    /**
     * Determine of the year is a leap year, i.e. a year containing a leap month
     *
     * @return true if the year contains a leap month, else false
     */
    public static boolean isLeapYear(int y) {
        AstroDay astro = dayFromDayMonthYear(1, 1, y);
        return CJKCalendarTable.getInstance().getLunarYear(astro).getLeapMoon() > 0;
    }

    public static boolean isValidYear(int y) {
        return y >= -4000  &&  y < 3000;
    }

    public static boolean isValidMonth(int m, int y) {
        // in the cjk calendar, the year is irrelevant
        return m >= 1  &&  m <= 12;
    }

    /**
     * Determine if d is a valid day in m and y
     *
     * @return true if d is a valid day in month m and year y, else false
     */
    public static boolean isValidDay(int d, int m, int y) {
        if (!isValidMonth(m, y)) {
            return false;
        } else if (d < 1  ||  d > 30) {
            return false;
        } else if (d != 30) {
            return true;
        }

        // The only case left is d=30
        AstroDay astro = dayFromDayMonthYear(d, m, y);
        LunarYearDescription lyd = CJKCalendarTable.getInstance().getLunarYear(astro);
        int bits = lyd.getMoonBits(); // something like 0000101011010101

        // get the bit for month m, and shift right m-1 bits
        int bit = (bits >> (m - 1)) & 1;
        return 1 == bit; // bit will be 1 if month m has 30 days
    }

    public static AstroDay dayFromDayMonthYear(int d, int m, int y) {
        return dayFromDayMonthYear(d, m, y, 0);
    }

    /**
     * Calculate a Julian day number from the day, month and year in this calendar
     *
     * @param day - the day of the lunar month (1-30)
     * @param month - lunar month (moon) number (1-12)
     * @param year - the Christian era year of the first day of the lunar year, in external form: 1 BC == -1, 1 AD == 1
     * @param i - month number of the intercalary month, or 0 (0-12)
     * @return the Astronomical (Julian) Day number from CJK day, month, year, intercalary month
     */
    public static AstroDay dayFromDayMonthYear(int day, int month, int year, int i) {
        if (year == 0) {
            throw new IllegalArgumentException("Invalid year " + year);
        } else if (month < 0  ||  month > 12) {
            throw new IllegalArgumentException("Invalid month " + month);
        } else if (day < 0  ||  day > 30) {
            throw new IllegalArgumentException("invalid day " + day);
        }

        // ==== lunar calendar calculations ====
        int d = day;
        int m = month;
        int y = year;
        LunarYearDescription lyd = CJKCalendarTable.getInstance().getLunarYear(y);

        boolean missingDay = false;
        boolean missingMonth = false;

        // use 1 for missing month
        if (m == 0) {
            missingMonth = true;
            m = 1;
            d = 0; // ignore any day value if the month is missing
        }

        // use 1 for missing day
        if (d == 0) {
            missingDay = true;
            d = 1;
        }

        // begin calculations at the first day of the year
        int jday = lyd.getJDay();

        // add the number of days in each month
        int moon = m; // the month number from the original string
        int leapMonth = lyd.getLeapMoon();

        // if the intercalary month from the date does not agree with the intercalary month from the
        // calendar, ignore the intercalary character in the date
        if (i != 0   &&   leapMonth != m) {
            // TODO: throw error if given and calculated intercalary moons not equal?
            i = 0; // ignore the intercalary character
        }

        // Add one to the month for the intercalary month or any subsequent month
        if (leapMonth > 0  &&  m > leapMonth) {
            moon++ ;
        } else if (i != 0  &&  leapMonth == m) {
            moon++;
        }

        // get the first day of the month by adding 29 or 30 for each month previous to the target month
        int bits = lyd.getMoonBits();
        while (--moon > 0) {
            jday += 29 + (bits & 1);
            bits >>= 1;
        }

        // add the day of the month
        jday += d > 0 ? d - 1 : 0;

        // if month or day are missing, adjust the jday to be the mid-point of the next larger piece
        if (missingMonth) {
            jday += lyd.nDaysInYear() / 2; // adjust the jday to the middle of the year
        } else  if (missingDay) {
            jday += lyd.nDaysInMonth(m, i > 0) / 2; // adjust the jday to the middle of the month;
        }

        return AstroDay.getInstance(jday);
    }

    public static int lastMonth() {
        return 12;
    }

}
