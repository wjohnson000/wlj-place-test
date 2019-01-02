/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.date.model.DateResult;
import org.familysearch.standards.date.model.GenDateInterpResult;

/**
 * @author wjohnson000
 *
 */
public class TestV2FRC {

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

        "18 Nivose 11",
        "18 Ni 11",
        "18 Nivose 1821",

        // These will be ignored by the FRC handler
        "18 Ni 1911",
        "29 th of December 1931",
        "29 th of December 1931",
    };

    public static void main(String... args) throws Exception {
//        ThreadLocalExperiment.set(new HashSet<>(Arrays.asList(FrenchRepublicanHandler.EXPERIMENT_ENABLE_V2)));
        runTests();
    }

    static void runTests() throws Exception {
        for (String text : textes) {
            System.out.println("\n" + text);
            try {
                DateResult dateResult = DateUtil.interpDate(text, StdLocale.FRENCH, null, null, null);
                for (GenDateInterpResult date : dateResult.getDates()) {
                    System.out.println("  gx02: " + date.getDate().toGEDCOMX());
                }
            } catch (GenDateException e) { }
        }
    }
}