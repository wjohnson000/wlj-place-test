/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.Random;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.exception.GenDateException;
//import org.familysearch.standards.date.ws.model.Dates;
//import org.familysearch.standards.date.ws.model.LocalizedDateFormat;
//import org.familysearch.standards.date.ws.services.DatesService;

/**
 * @author wjohnson000
 *
 */
public class TestDatesService {

    static final Random random = new Random();

    static final StdLocale[] locales = {
        StdLocale.CHINESE,
        StdLocale.DANISH,
        StdLocale.ENGLISH,
        StdLocale.FRENCH,
        StdLocale.GERMAN,
        StdLocale.JAPANESE,
        StdLocale.KOREAN,
        StdLocale.PORTUGUESE,
        StdLocale.SPANISH
    };

    public static void main(String... args) throws GenDateException {
//        DatesService service = new DatesService();
//
//        for (int i=1;  i<100;  i++) {
//            int yr = random.nextInt(120) + 1900;
//            int mo = random.nextInt(12) + 1;
//            int dy = random.nextInt(28) + 1;
//            StdLocale locale = locales[random.nextInt(locales.length)];
//
//            String gedcomx = "+" + yr + "-" + (mo<10 ? "0" : "") + mo + "-" + (dy<10 ? "0" : "") + dy;
//            System.out.println("\n" + gedcomx + " [" + locale + "]");
//
//            for (int cnt=0;  cnt<2;  cnt++) {
//                long time0 = System.nanoTime();
//                Dates dates = service.getDatesGedcomX(gedcomx, LocalizedDateFormat.localized, locale);
//                long time1 = System.nanoTime();
//
//                if (dates == null  ||  dates.getDates() == null) {
//                    System.out.println("  --> no results" + " ... " + (time1 - time0) / 1_000_000.0);
//                } else {
//                    dates.getDates().forEach(date -> System.out.println("  " + date.getGedcomx() + " . " + date.getLocalizedDate().getValue() + " ... " + (time1 - time0) / 1_000_000.0));
//                }
//            }
//        }
        
    }
}
