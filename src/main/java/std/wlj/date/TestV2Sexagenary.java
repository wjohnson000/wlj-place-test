/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.date.api.model.DateResult;
import org.familysearch.standards.date.api.model.GenDateInterpResult;
import org.familysearch.standards.date.common.DateUtil;

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
                DateResult dateResult = DateUtil.interpDate(text, "zh", null, null, null);
                
                System.out.println("\n=============================================================\n" + text);
                for (GenDateInterpResult date : dateResult.getDates()) {
                    System.out.println("  gx02: " + date.getDate().toGEDCOMX());
                }
            } catch (GenDateException e) { }
        }
    }
}