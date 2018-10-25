/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.Random;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateServiceImpl;
import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.date.format.NumberFormatter;
import org.familysearch.standards.date.model.DateResult;
import org.familysearch.standards.date.ws.model.CalendarType;
import org.familysearch.standards.date.ws.model.Dates;
import org.familysearch.standards.date.ws.model.LocalizedDateFormat;
import org.familysearch.standards.date.mapper.DatesMapper;
import org.familysearch.standards.date.ws.services.DatesService;

import std.wlj.marshal.POJOMarshalUtil;

/**
 * @author wjohnson000
 *
 */
public class TestRefactorGedcomx {

    static Random random = new Random();

    static LocalizedDateFormat[] LDF = {
        LocalizedDateFormat.abbreviated,
        LocalizedDateFormat.legacy,
        LocalizedDateFormat.legacyabbreviated,
        LocalizedDateFormat.localized,
        LocalizedDateFormat.longfmt,
    };

    static StdLocale[] LOCALE = {
        StdLocale.CHINESE,
        StdLocale.ENGLISH,
        StdLocale.FRENCH,
        StdLocale.GERMAN,
        StdLocale.JAPANESE,
        StdLocale.RUSSIAN,
        StdLocale.SPANISH,
    };

    public static void main(String...args) throws GenDateException {
        DatesService    dateOld = new DatesService();
        DateServiceImpl dateNew = new DateServiceImpl();
        DatesMapper     dateMap = new DatesMapper();
        
        int badCount = 0;
        for (LocalizedDateFormat ldf : LDF) {
            for (StdLocale locale : LOCALE) {
                for (int cnt=0;  cnt<200;  cnt++) {
                    int switchx = random.nextInt(3);
                    String gedcomx = "";
                    if (switchx == 0) {
                        gedcomx = getDate();
                    } else if (switchx == 1) {
                        gedcomx = getPeriod();
                    } else if (switchx == 2) {
                        gedcomx = getRange();
                    }

                    try {
                        Dates      datesOld = dateOld.getDatesGedcomX(gedcomx, ldf, locale);
                        DateResult dateRes = dateNew.getDatesGedcomX(gedcomx);
                        Dates      datesNew = dateMap.fromDateResult(dateRes, ldf, locale, CalendarType.gregorian);
                        
                        String     xmlOld = POJOMarshalUtil.toXML(datesOld);
                        String     xmlNew = POJOMarshalUtil.toXML(datesNew);
                        System.out.println(gedcomx); 
                        if (! xmlOld.trim().equals(xmlNew.trim())) {
                            badCount++;
                            System.out.println("\n\nOLD:\n" + xmlOld);
                            System.out.println("\nNEW:\n" + xmlNew);
                        }
                    } catch(Exception ex) {
                        System.out.println("    >> " + ex.getMessage());
                    }
                }
            }
        }

        System.out.println("\n\nBadCount=" + badCount);
    }

    static String getDate() {
        int day = random.nextInt(27) + 1;
        int mon = random.nextInt(12) + 1;
        int yrr = random.nextInt(2000) + 1;
        if (random.nextInt(100) > 95) {
            yrr *= -1;
        }
        boolean isAbout = (random.nextInt(100) > 90);

        return (isAbout ? "A" : "" ) +
                ((yrr <= 0) ? "-" : "+") +
               NumberFormatter.padFourDigits(Math.abs(yrr)) + "-" +
               NumberFormatter.padTwoDigits(mon) + "-" +
               NumberFormatter.padTwoDigits(day);
    }

    static String getPeriod() {
        int yrr = random.nextInt(200) + 1;

        String gedcomx = "P" + yrr;
        if (random.nextInt(100) > 15) {
            int mon = random.nextInt(12) + 1;
            gedcomx += mon + "M";
            if (random.nextInt(100) > 20) {
                int day = random.nextInt(27) + 1;
                gedcomx += day + "D";
            }
        }

        return gedcomx;
    }

    static String getRange() {
        return getDate() + "/" + getDate();
    }
}
