/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.jira;

import org.familysearch.standards.date.api.model.DateResult;
import org.familysearch.standards.date.api.model.GenDateInterpResult;
import org.familysearch.standards.date.common.DateUtil;
import org.familysearch.standards.date.exception.GenDateException;

/**
 * @author wjohnson000
 *
 */
public class STD_89937 {

    static String[] textes = {
        "22 Feb/5 Mar 1752/3",
        "Sept 3/14, 1752",
        "10/21 Feb 1759/60", 
    };

    public static void main(String... args) throws Exception {
        runTests();
    }

    static void runTests() throws Exception {
        for (String text : textes) {

            System.out.println("\n================================================================================");
            System.out.println(text);
            System.out.println("================================================================================");

            try {
                DateResult dateResult = DateUtil.interpDate(text,"en", null, null, null);
                for (GenDateInterpResult date : dateResult.getDates()) {
                    System.out.println("  gx02: " + date.getDate().toGEDCOMX());
                }
            } catch (GenDateException e) { }

        }
    }
}