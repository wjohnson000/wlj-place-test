/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.Map;
import java.util.TreeMap;

import org.familysearch.standards.date.calendar.CalendarUtil;
import org.familysearch.standards.date.calendar.DMY;
import org.familysearch.standards.date.calendar.LunarCalendarUtil;
import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.date.api.model.GenSimpleDate;

/**
 * @author wjohnson000
 *
 */
public class FunWithGregorianJulianLunar {

    static final int DAY = 11;

    public static void main(String...args) throws GenDateException {
        for (int yr=1100;  yr<=1105;  yr++) {
            for (int mon=0;  mon<=12;  mon++) {
                Map<String, DMY> options = new TreeMap<>();
                
                GenSimpleDate gregg = new GenSimpleDate(false, yr, mon, DAY, 0, 0, 0);
                int jdayGG = gregg.getAstroday();
                options.put("GREG", CalendarUtil.dmyGregorian(jdayGG));
                options.put("JULI", CalendarUtil.dmyJulian(jdayGG));

                int jdayL0 = LunarCalendarUtil.calculateJulianDay(yr, mon, DAY, false);
                int jdayL1 = LunarCalendarUtil.calculateJulianDay(yr, mon, DAY, true);
                options.put("LLG1", CalendarUtil.dmyGregorian(jdayL0));
                options.put("LLG2", CalendarUtil.dmyGregorian(jdayL1));
                options.put("LLJ1", CalendarUtil.dmyJulian(jdayL0));
                options.put("LLJ2", CalendarUtil.dmyJulian(jdayL1));

                System.out.println("\n=====================================================================");
                System.out.println("YR=" + yr + ";  MON=" + mon + ";  DAY=" + DAY);
                System.out.println("  GGGG --> " + gregg.toGEDCOMX());
                for (Map.Entry<String, DMY> entry : options.entrySet()) {
                    DMY dmy = entry.getValue();
                    GenSimpleDate simpleDate = new GenSimpleDate(false, dmy.getYear(), (mon==0 ? 0 : dmy.getMonth()), (mon==0 ? 0 : dmy.getDay()), 0, 0, 0);
                    System.out.println("  " + entry.getKey() + " --> " + simpleDate.toGEDCOMX());
                }
            }
        }
    }

}
