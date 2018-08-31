/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.date.GenDateInterpResult;
import org.familysearch.standards.date.exception.GenDateParseException;
import org.familysearch.standards.date.parser.handler.FrenchRepublicanHandler;
import org.familysearch.standards.date.shared.SharedUtil;
import org.familysearch.standards.date.shared.ThreadLocalExperiment;
import org.familysearch.standards.date.v1.DateV1Shim;

/**
 * @author wjohnson000
 *
 */
public class TestV1ShimAndV2FRC {

    static String[] textes = {
        "13 Pluviôse VI",
        "13 Pluviôse du VII",
        "13 pluviose AN7",
        "13 pluviose AN8",
        "treizième pluviose AN8",
        "treizième pluviose AN9",
        "treizieme pluviose AN9",
        "treizieme pluviose AN10",
        "fete du travail",
        "fete du travail XII",
        "fete du travail an14",
        "some text or fete du travail",
        "some text or fete du travail XII",
        "some text or fete du travail an14",
        "fete du travail more text",
        "fete du travail XII more text",
        "fete du travail an14 more text",
        "some text fete du travail more text",
        "some text fete du travail XII more text",
        "some text fete du travail an14 more text",
        "3rd complementary du XII",

        "30 Floréal AN11",
        "28 Brumaire AN04",
        "11 Vendémiaire AN04",
        "07 Frimaire AN08",
        "16 Illisible AN02",
        "00 Nivose AN12 ",

        "30 Floréal AN 11",
        "28 Brumaire AN 04",
        "11 Vendémiaire AN 04",
        "07 Frimaire AN 08",
        "16 Illisible AN 02",
        "00 Nivose AN 12 ",
    };

    public static void main(String... args) throws Exception {
//        ThreadLocalExperiment.set(new HashSet<>(Arrays.asList(FrenchRepublicanHandler.EXPERIMENT_ENABLE_V2)));
        runTests();
    }

    static void runTests() throws Exception {
        for (String text : textes) {
            List<GenDateInterpResult> dates01 = new ArrayList<>();
            List<GenDateInterpResult> dates02 = new ArrayList<>();

            System.out.println("\n" + text);
            try {
                dates01 = DateV1Shim.interpDate(text);
            } catch (GenDateParseException e) { }

            try {
                dates02 = DateUtil.interpDate(text, StdLocale.FRENCH);
            } catch (GenDateException e) { }

            for (GenDateInterpResult date : dates01) {
                System.out.println("  gx01: " + date.getDate().toGEDCOMX() + "  [" + date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1) + "]");
            }
            for (GenDateInterpResult date : dates02) {
                System.out.println("  gx02: " + date.getDate().toGEDCOMX() + "  [" + date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1) + "]");
            }
        }
    }
}