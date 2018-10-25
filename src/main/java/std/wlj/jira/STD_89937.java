/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.jira;

import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.date.model.GenDateInterpResult;

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
            List<GenDateInterpResult> dates02 = new ArrayList<>();

            System.out.println("\n================================================================================");
            System.out.println(text);
            System.out.println("================================================================================");

            try {
                dates02 = DateUtil.interpDate(text, StdLocale.ENGLISH);
            } catch (GenDateException e) { }

            for (GenDateInterpResult date : dates02) {
                System.out.println("  gx02: " + date.getDate().toGEDCOMX());
            }
        }
    }
}