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
        List<String> textes = Files.readAllLines(Paths.get("C:/temp/date-assisted.txt"), Charset.forName("UTF-8"));
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

            System.out.println("\n" + chunks[0] + " [" + chunks[1] + "]");
            for (GenDateInterpResult date : dates01) {
                System.out.println("  gx01: " + date.getDate().toGEDCOMX() + "  [" + date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1) + "]");
            }
            for (GenDateInterpResult date : dates02) {
                System.out.println("  gx02: " + date.getDate().toGEDCOMX() + "  [" + date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1) + "]");
            }
        }
    }
}