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
public class TestV2Sexagenary {

    static String[] textes = {
//        "乙巳",
//        "乙巳年",
//        "乙巳年五",
//        "乙巳年五月",
//        "乙巳年五月十",
//        "乙巳年五月十日",
        "六十甲子",
    };

    public static void main(String... args) throws Exception {
        runTests();
    }

    static void runTests() throws Exception {
        for (String text : textes) {
            try {
                DateResult dateResult = DateUtil.interpDate(text, StdLocale.CHINESE, null, null, null);
                
                System.out.println("\n=============================================================\n" + text);
                for (GenDateInterpResult date : dateResult.getDates()) {
                    System.out.println("  gx02: " + date.getDate().toGEDCOMX());
                }
            } catch (GenDateException e) { }
        }
    }
}