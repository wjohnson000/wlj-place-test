/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.date.exception.GenDateParseException;
import org.familysearch.standards.date.GenDateInterpResult;
import org.familysearch.standards.date.shared.SharedUtil;
import org.familysearch.standards.date.v1.DateV1Shim;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class TestV1V2Assisted {

    public static void main(String... args) throws Exception {
        int v1Count = 0;
        int v2Count = 0;
        List<String> v1Date = new ArrayList<>();
        List<String> v2Date = new ArrayList<>();

        List<String> textes = Files.readAllLines(Paths.get("C:/temp/date-2.0-should-handle.txt"), Charset.forName("UTF-8"));
        for (String text : textes) {
            List<GenDateInterpResult> dates01 = new ArrayList<>();
            List<GenDateInterpResult> dates02 = new ArrayList<>();

            String[] chunks = PlaceHelper.split(text, '|');
            try {
                dates01 = DateV1Shim.interpDate(chunks[0]);
            } catch (GenDateParseException e) { }

            try {
                dates02 = DateUtil.interpDate(chunks[0], StdLocale.KOREAN);
            } catch (GenDateException e) { }

            v1Count++;
            List<String> which = v1Date;
            if (dates02.isEmpty()  ||  ! dates02.get(0).getAttrAsBoolean(SharedUtil.ATTR_USED_V1)) {
                v1Count--;
                v2Count++;
                which = v2Date;
            }

            which.add("");
            which.add(chunks[0] + (chunks.length < 2 ? "" : " [" + chunks[1] + "]"));
            for (GenDateInterpResult date : dates01) {
                which.add("  gx01: " + date.getDate().toGEDCOMX() + "  [" + date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1) + "]");
            }
            for (GenDateInterpResult date : dates02) {
                which.add("  gx02: " + date.getDate().toGEDCOMX() + "  [" + date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1) + "]");
            }
        }

        System.out.println("====================================================================================");
        System.out.println("V1 ONLY");
        System.out.println("====================================================================================");
        v1Date.forEach(System.out::println);

        System.out.println();
        System.out.println();
        System.out.println("====================================================================================");
        System.out.println("V2 IS GOOD !!");
        System.out.println("====================================================================================");
        v2Date.forEach(System.out::println);

        System.out.println("\n\nV1-count: " + v1Count);
        System.out.println("V2-count: " + v2Count);
    }
}