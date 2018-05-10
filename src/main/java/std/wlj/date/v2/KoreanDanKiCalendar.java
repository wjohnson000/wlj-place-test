package std.wlj.date.v2;

/**
 * Korean traditional calendar called Dan-Ki (or Tan-Ki).  The Tangun era (Tan-Ki) began with the
 * founded ancient Chosun in 2333 BC.  See Korean Myths and Tales : Tangun, The Father of Our Nation,
 * http://www.clickasia.co.kr/about/m1.htm
 * 
 * Copyright(c) 2005 Intellectual Reserve Inc. All rights reserved. Unauthorized reproduction of this
 * software is prohibited and is in violation of United States copyright laws.
 * 
 * @author Pete Blake Date: Jul 26, 2005
 * @author Wayne Johnson, May, 2018 (reworked)
 * 
 */
public class KoreanDanKiCalendar extends KoreanCalendar {

    private static final int TANGUN_ERA_START = 2333;
    private static final KoreanDanKiCalendar THE_CALENDAR = new KoreanDanKiCalendar();

    private KoreanDanKiCalendar() {
    }

    public static Calendar getInstance() {
        return THE_CALENDAR;
    }

    /**
     * getCalendarType
     *
     * @return the CalendarType of this calendar
     */
    public CalendarType getCalendarType() {
        return CalendarType.KOREAN_DAN_KI;
    }

    public String toString() {
        return "Korean Dan-Ki calendar";
    }

    /**
     * Calculate a Julian day number from the day, month and year in this calendar.  Assume Gregorian
     * rules after the adoption of the Gregorian calendar; use CJK Lunar Calendar prior to that.
     *
     * @param d - the day of the (lunar) month (1-31)
     * @param m - lunar moon or month number (1-12)
     * @param y - the Tangun era year
     * @param i - moon number of the intercalary moon (1-12), or 0 if not intercalary
     * @return the calculated (Julian) Astronomical Day number
     */
    public AstroDay dayFromDayMonthYear(int d, int m, int y, int i) {
        if (y == 0) {
            String msg = "Invalid year " + y;
            throw new IllegalArgumentException(msg);
        }

        int year = convertTangunEraYearToChristianEra(y); // convert to the Christian Era
        if (year < firstGregorianYear() || i != 0) {
            return super.dayFromDayMonthYear(d, m, year, i);
        } else {
            return GregorianCalendar.getInstance().dayFromDayMonthYear(d, m, year);
        }
    }

    /**
     * Calculate Korean Dan-Ki d, m, y from a julian day number
     *
     * @return a DMY object
     */
    public DMY dmyFromDay(AstroDay aday) {
        DMY dmy = aday.value() < firstGregorian()
                ? super.dmyFromDay(aday)
                : GregorianCalendar.getInstance().dmyFromDay(aday);
        dmy.setYear(convertChristianEraYearToTangunEra(dmy.getYear()));
        return dmy;
    }


    /**
     * Determine if y is a valid year in the Tangun era (which begin in 2333BC)
     *
     * @return true if y is valid, else false
     */
    public boolean isValidYear(int y) {
        return y > 0  &&  y < (3000 + TANGUN_ERA_START);
    }

    /**
     * Convert the Tangun era (Tan-Ki) year to the Christian era
     *
     * @param year -- the year in this calendar system
     * @return the Christian equivalent of the parameter year
     */
    protected static int convertTangunEraYearToChristianEra(int y) {
        // Tangun year is always positive
        int year = y - TANGUN_ERA_START;

        // convert eraYear back to external format
        if (year <= 0) {
            year--; // convert 0 to 1BC, -1 to 2 BC, etc.
        }

        return year;
    }

    protected static int convertChristianEraYearToTangunEra(int y) {
        // convert year to internal format for calculations
        int year = y;
        if (year < 0) {
            year++; // convert 1 BC to 0, 2 BC to -1, etc.
        }

        return year + TANGUN_ERA_START;
    }

}
