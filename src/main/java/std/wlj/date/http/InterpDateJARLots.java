/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.http;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.familysearch.standards.date.api.model.DateResult;
import org.familysearch.standards.date.common.DateUtil;
import org.familysearch.standards.date.exception.GenDateException;

/**
 * @author wjohnson000
 *
 */
public class InterpDateJARLots {

    private static Random            RANDOM = new Random();
    private static DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH);


    public static void main(String... args) throws GenDateException {
        List<String> dateText = IntStream.range(0, 10000).mapToObj(ii -> createDate()).collect(Collectors.toList());
        System.out.println("Dates to test: " + dateText.size());

        long timeX = 0L;
        long timeA = System.nanoTime();
        for (String dateStr : dateText) {
            long time0 = System.nanoTime();
            DateResult dateRes = DateUtil.interpDate(dateStr, "en", null, null, null);
            long time1 = System.nanoTime();
            timeX += (time1 - time0);

//            System.out.println("Text: " + dateStr + ";  Dates: " + dates.getCount());
//            if (dates.getCount() > 0) {
//                System.out.println(" Date: " + dates.getDates().get(0).getGedcomx());
//            }
        }
        long timeB = System.nanoTime();

        System.out.println("TIME: " + (timeX) / 1_000_000.0);
        System.out.println("TIME: " + (timeB - timeA) / 1_000_000.0);
    }

    static String createDate() {
        int year  = RANDOM.nextInt(1100) + 900;
        int month = RANDOM.nextInt(12) + 1;
        int dofm  = RANDOM.nextInt(28) + 1;

        LocalDate date = LocalDate.of(year, month, dofm);
        return FORMAT.format(date);
    }
}
