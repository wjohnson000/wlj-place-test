package std.wlj.date.v2;

/**
 * Copyright(c) 2004 Intellectual Reserve Inc. All rights reserved. Unauthorized reproduction of this
 * software is prohibited and is in violation of United States copyright laws.
 * 
 * @author Pete Blake
 * @author Wayne Johnson, May, 2018 (reworked)
 */
public class JapaneseCalendar extends CJKCalendar {

    private static final JapaneseCalendar THE_CALENDAR = new JapaneseCalendar();

    private JapaneseCalendar() {
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
        return CalendarType.JAPANESE_LUNAR;
    }

    public String toString() {
        return "Japanese Lunar calendar";
    }

    /**
     * The julian day number of the first day in the Gregorian Calendar in this calendar system.
     *
     * @return the julian day number of the day that the calendar change officially began.
     */
    public int firstGregorian() {
        // The lunar calendar was abolished and the Gregorian Calendar introduced in Meiji 5 (1872)
        // when the 3rd day of the 12th month was declared to be 1 January 1873. ("Japanese
        // Chronological Tables", p.4).  Our lunar tables end at Meiji 5.12.30.  Use Gregorian rules
        // after that.  Process any date before Meiji 6 (2405188) as lunar, consequently, julian day
        // numbers from 2405160 to 2405187, inclusive, are ambiguous as to whether we used lunar or
        // Gregorian rules.
        return 2405160; // 1 Jan 1873
    }

    public int firstGregorianYear() {
        return 1873;
    }
}
