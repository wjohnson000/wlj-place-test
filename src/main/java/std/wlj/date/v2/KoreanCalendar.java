package std.wlj.date.v2;

/**
 * Copyright(c) 2004 Intellectual Reserve Inc. All rights reserved. Unauthorized reproduction of this
 * software is prohibited and is in violation of United States copyright laws.
 * 
 * @author Pete Blake
 * @author Wayne Johnson, May, 2018 (reworked)
 */
public class KoreanCalendar extends CJKCalendar {

    private static final KoreanCalendar THE_CALENDAR = new KoreanCalendar();

    protected KoreanCalendar() {
    }

    public static Calendar getInstance() {
        return THE_CALENDAR;
    }

    /**
     * getCalendarType
     *
     * @return the type of this calendar
     */
    public CalendarType getCalendarType() {
        return CalendarType.KOREAN_LUNAR;
    }

    public String toString() {
        return "Korean Lunar calendar";
    }

    /**
     * The julian day number of the first day in the Gregorian Calendar in this calendar system.
     *
     * @return the julian day number of the day that the calendar change officially began.
     */
    public int firstGregorian() {
        // Korea officially adopted the Gregorian calendar in 朝鮮高宗 32 (1895), when the 17th day
        // of the 11th lunar month became 1 Jan 1896.
        return 2413560; // 1 Jan 1896
    }

    public int firstGregorianYear() {
        return 1896;
    }
}
