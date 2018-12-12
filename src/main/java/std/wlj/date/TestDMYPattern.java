/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.model.GenSimpleDate;
import org.familysearch.standards.date.shortcut.DMYGenSimpleDate;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class TestDMYPattern {

    public static void main(String... args) throws Exception {
        int hitCount = 0;
//        List<String> lines = Files.readAllLines(Paths.get("C:/temp/date-interp-simple-dmy.txt"), Charset.forName("UTF-8"));
        List<String> lines = Files.readAllLines(Paths.get("C:/temp/date-interp-lots.txt"), Charset.forName("UTF-8"));

        long time0 = System.nanoTime();
        for (String line : lines) {
            String[] data = getValues(line);
            if (data != null) {
                String date   = data[0];
                String locale = data[1];
                String target = data[2];
                try {
                    List<GenSimpleDate> genDates = DMYGenSimpleDate.fromDMY(date, StdLocale.makeLocale(locale), null, null, null);
                    if (genDates != null  &&  genDates.stream().anyMatch(genDate -> genDate.toGEDCOMX().equals(target))) {
                        hitCount++;
                    }
                } catch(Exception ex) {
                    ;  // Do nothing ...
                }
            }
        }
        long time1 = System.nanoTime();

        System.out.println("Hits: " + hitCount);
        System.out.println("Time: " + (time1 - time0) / 1_000_000.0);
    }

    static String[] getValues(String line) {
        String[] chunks = PlaceHelper.split(line, ',');
        if (chunks.length < 3) {
            return null;
        } else {
            String text    = chunks[0];
            for (int i=1;  i<chunks.length-2;  i++) {
                text += chunks[i];
            }
            text = text.replace('"', ' ').trim();
            String locale  = chunks[chunks.length-2].replace('"', ' ').trim();
            String gedcomx = chunks[chunks.length-1].replace('"', ' ').trim();
            return new String[] { text, locale, gedcomx };
        }
    }
}
