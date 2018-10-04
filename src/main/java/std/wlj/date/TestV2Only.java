/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.GenDateInterpResult;
import org.familysearch.standards.date.parser.GenDateGenerator;
import org.familysearch.standards.date.shared.SharedUtil;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class TestV2Only {
    public static void main(String... args) throws Exception {
        List<String> textes = Files.readAllLines(Paths.get("C:/temp/date-interp.txt"), Charset.forName("UTF-8"));
//        textes.clear();
//        textes.add("21 Feb 1903");
//        textes.add("19050823");

        long time0 = System.nanoTime();

        for (String text : textes) {
//            GenDateGenerator.whatever.add(text);
            String[] datums = PlaceHelper.split(text, '|');

            List<GenDateInterpResult> dates02 = new ArrayList<>();
            try {
                dates02 = DateUtil.interpDate(datums[0], StdLocale.CHINESE);
            } catch (Exception e) {
//                GenDateGenerator.whatever.add("  Exception: " + e.getMessage());
            }


            for (GenDateInterpResult date : dates02) {
                System.out.println("  gx02: " + datums[0] + "|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1));
//                GenDateGenerator.whatever.add("  Result|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1));
            }
        }

        long time1 = System.nanoTime();

        System.out.println();
        System.out.println("Run time: " + (time1-time0)/1_000_000.0);
        System.out.println();
//        GenDateGenerator.whateverCount.entrySet().forEach(System.out::println);

//        Files.write(
//            Paths.get("C:/temp/date-interp-handlers.txt"),
//            GenDateGenerator.whatever,
//            Charset.forName("UTF-8"),
//            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.exit(0);
    }
}