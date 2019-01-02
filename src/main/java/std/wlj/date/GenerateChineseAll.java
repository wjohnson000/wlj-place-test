/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ChineseCalendar;

/**
 * @author wjohnson000
 *
 */
public class GenerateChineseAll {

    static final long MILLIS_PER_DAY = 60 * 60 * 24 * 1000L;
    static final long START_DAY = 0L - MILLIS_PER_DAY * 365 * 5000;
    static final long END_DAY = 0L + MILLIS_PER_DAY * 365 * 1020;

    static final Map<Integer, String> monthName = new HashMap<>();
    static {
        monthName.put(Calendar.JANUARY,    "January");
        monthName.put(Calendar.FEBRUARY,   "February");
        monthName.put(Calendar.MARCH,      "March");
        monthName.put(Calendar.APRIL,      "April");
        monthName.put(Calendar.MAY,        "May");
        monthName.put(Calendar.JUNE,       "June");
        monthName.put(Calendar.JULY,       "July");
        monthName.put(Calendar.AUGUST,     "August");
        monthName.put(Calendar.SEPTEMBER,  "September");
        monthName.put(Calendar.OCTOBER,    "October");
        monthName.put(Calendar.NOVEMBER,   "November");
        monthName.put(Calendar.DECEMBER,   "December");
        monthName.put(Calendar.UNDECIMBER, "UnDecimber");
    }

    static final List<String> allDates = new ArrayList<>(365 * 6200);

    static DateFormat calFormat   = null;
    static int        currentEra  = Integer.MIN_VALUE;
    static int        currentYear = Integer.MIN_VALUE;

    public static void main(String... args) throws IOException {
        for (long time=START_DAY;  time<=END_DAY;  time+=MILLIS_PER_DAY) {
            Date zeroX = new Date(time);
            calendarPP(zeroX);
        }
        Files.write(Paths.get("C:/temp/chinese-dates.txt"), allDates, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static void calendarPP(Date date) {
        ChineseCalendar cal = new ChineseCalendar(date);
        if (calFormat == null) {
            calFormat = cal.getDateTimeFormat(0, -1, Locale.ENGLISH);
        }

        boolean ppp = false;
        int era = cal.get(Calendar.ERA);
        int year = cal.get(Calendar.YEAR);
        if (era != currentEra) {
            ppp = true;
            currentEra = era;
            currentYear = year;
            allDates.add("");
            allDates.add("");
            allDates.add("");
            allDates.add("");
        } else if (year != currentYear) {
            currentYear = year;
            allDates.add("");
        }

        Date toDate = new Date(cal.getTimeInMillis());
        StringBuilder buff = new StringBuilder(256);
        buff.append(String.valueOf(date));
        buff.append("|").append(String.valueOf(toDate));
        buff.append("|").append(calFormat.format(cal));
        buff.append("|").append(currentEra);
        buff.append("|").append(currentYear);
        buff.append("|").append(cal.get(Calendar.EXTENDED_YEAR));
        buff.append("|").append(cal.get(Calendar.IS_LEAP_MONTH));
        buff.append("|").append(cal.get(Calendar.MONTH));
        buff.append("|").append(monthName.get(cal.get(Calendar.MONTH)));
        buff.append("|").append(cal.get(Calendar.DAY_OF_MONTH));
        buff.append("|").append(cal.get(Calendar.DAY_OF_YEAR));
        allDates.add(buff.toString());
        if (ppp) System.out.println(buff.toString());
    }
}
