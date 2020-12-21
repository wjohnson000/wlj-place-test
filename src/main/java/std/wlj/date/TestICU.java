/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.Date;
import java.util.Locale;

import com.ibm.icu.text.DateFormat;
import com.ibm.icu.util.Calendar;
import com.ibm.icu.util.ChineseCalendar;

/**
 * @author wjohnson000
 *
 */
public class TestICU {
    public static void main(String... args) {
        ChineseCalendar cc1 = new ChineseCalendar();
        calendarPP("-- No Params --", cc1);

        ChineseCalendar cc2 = new ChineseCalendar(new Date());
        calendarPP("-- Current Date --", cc2);

        ChineseCalendar cc3 = new ChineseCalendar(1821, 12, 6, 7);
        calendarPP("-- year.month.isLeapMo.date --", cc3);

        ChineseCalendar cc4 = new ChineseCalendar(4, 4, 12, 2, 7);
        calendarPP("-- era.year.month.isLeapMo.date --", cc4);

        for (int era=0;  era<10;  era++) {
            for (int year=-2000;  year<4000;  year+=1000) {
                ChineseCalendar cc7 = new ChineseCalendar(era, year, 12, 6, 7);
                calendarPP("-- era=" + era + ".year=" + year + " --", cc7);
            }
        }
    }

    static void calendarPP(String title, Calendar cal) {
        System.out.println("\n" + title);
        System.out.println("  " + cal.getTime());
        for (int ds=1;  ds<=3;  ds++) {
            for (int ts=1;  ts<=3;  ts++) {
                calendarPP(cal, ds, ts);
            }
        }
    }

    static void calendarPP(Calendar cal, int dateStyle, int timeStyle) {
        DateFormat format = cal.getDateTimeFormat(dateStyle, timeStyle, Locale.ENGLISH);
        System.out.println("  " + dateStyle + "." + timeStyle + " :: " + format.format(cal));
    }
}
