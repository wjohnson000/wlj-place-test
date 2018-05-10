package std.wlj.date.v2;

/**
 * Copyright(c) 2004 Intellectual Reserve Inc. All rights reserved. Unauthorized reproduction of this
 * software is prohibited and is in violation of United States copyright laws.
 * 
 * @author Pete Blake
 * @author Wayne Johnson, May, 2018 (reworked)
 */
public class ChineseCalendar extends CJKCalendar {

    private static final ChineseCalendar THE_CALENDAR = new ChineseCalendar();

    private ChineseCalendar() {
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
        return CalendarType.CHINESE_LUNAR;
    }

    public String toString() {
        return "Chinese Lunar calendar";
    }

    /**
     * The julian day number of the first day in the Gregorian Calendar in this calendar system.
     *
     * @return the julian day number of the day that the calendar change officially began.
     */
    public int firstGregorian() {
        // China adopted the Gregorian calendar at the founding of the republic in 1912, but many
        // people continue to use the lunar calendar today.
        return 2419403; // 1 Jan 1912
    }

    public int firstGregorianYear() {
        return 1912;
    }

}
