/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.date.GenDateInterpResult;
import org.familysearch.standards.date.exception.GenDateParseException;
import org.familysearch.standards.date.shared.SharedUtil;
import org.familysearch.standards.date.v1.DateV1Shim;

/**
 * @author wjohnson000
 *
 */
public class TestV1ShimAndV2Sexagenary {

    static String[] textes = {
        "乙巳",
        "乙巳年",
        "乙巳年五",
        "乙巳年五月",
        "乙巳年五月十",
        "乙巳年五月十日",
    };

    public static void main(String... args) throws Exception {
        runTests();
    }

    static void runTests() throws Exception {
        Set<String> dates01GX = new TreeSet<>();
        Set<String> dates02GX = new TreeSet<>();
        
        List<GenDateInterpResult> dates01 = new ArrayList<>();
        List<GenDateInterpResult> dates02 = new ArrayList<>();

        for (String text : textes) {
            try {
                dates01 = DateV1Shim.interpDate(text);
                dates01GX = dates01.stream().map(dd -> dd.getDate().toGEDCOMX()).collect(Collectors.toCollection(TreeSet::new));
            } catch (GenDateParseException e) { }

            try {
                dates02 = DateUtil.interpDate(text, StdLocale.CHINESE);
                dates02GX = dates02.stream().map(dd -> dd.getDate().toGEDCOMX()).collect(Collectors.toCollection(TreeSet::new));
            } catch (GenDateException e) { }

            System.out.println("\n=============================================================\n" + text);
            for (GenDateInterpResult date : dates01) {
                System.out.println("  gx01: " + date.getDate().toGEDCOMX() + "  [" + date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1) + "]  [" + dates02GX.contains(date.getDate().toGEDCOMX()) + "]");
            }
            for (GenDateInterpResult date : dates02) {
                System.out.println("  gx02: " + date.getDate().toGEDCOMX() + "  [" + date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1) + "]  [" + dates01GX.contains(date.getDate().toGEDCOMX()) + "]");
            }
        }
    }
}