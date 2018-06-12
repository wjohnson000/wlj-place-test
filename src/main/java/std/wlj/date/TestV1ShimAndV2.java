/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.GenDateException;
import org.familysearch.standards.date.GenDateInterpResult;
import org.familysearch.standards.date.GenDateParsingException;
import org.familysearch.standards.date.shared.SharedUtil;
import org.familysearch.standards.date.v1.DateV1Shim;

/**
 * @author wjohnson000
 *
 */
public class TestV1ShimAndV2 {

    static String[] textes = {
        "順帝三年七月七日",
        "順帝丙寅叄年七月七日",
        "金世宗大定2年5月5日",
        "安政5年6月8日",
        "清世祖順治元年1月1日",
        "清世祖順治1年1月1日",
        "陳文帝天嘉年1月1日",
        "吳大帝嘉禾年1月1日",
        "民國10年10月10日",
        "安政5年6月8",
        "西元1921年11月9日"
    };

    public static void main(String... args) {
        runTests();
    }

    static void runTests() {
        try {
            for (String text : textes) {
                List<GenDateInterpResult> dates01 = DateV1Shim.interpDate(text);
                List<GenDateInterpResult> dates02 = DateUtil.interpDate(text, StdLocale.CHINESE);

                System.out.println("\n" + text);
                for (GenDateInterpResult date : dates01) {
                    System.out.println("  gx01: " + date.getDate().toGEDCOMX() + "  [" + date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1) + "]");
                }
                for (GenDateInterpResult date : dates02) {
                    System.out.println("  gx02: " + date.getDate().toGEDCOMX() + "  [" + date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1) + "]");
                }
            }
        } catch (GenDateParsingException | GenDateException ex) {
            System.out.println("Oops!! " + ex.getMessage());
        }
    }
}