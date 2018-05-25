/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v1;

import java.util.List;

import org.familysearch.standards.date.GenDateInterpResult;
import org.familysearch.standards.date.GenDateParsingException;
import org.familysearch.standards.date.v1.DateV1Shim;

/**
 * @author wjohnson000
 *
 */
public class ZzzTestV1Shim {

//    static String text = "順帝三年七月七日";
    static String text = "順帝丙寅叄年七月七日";

    public static void main(String... args) {
        try {
            List<GenDateInterpResult> dates = DateV1Shim.interpDate(text);
            for (GenDateInterpResult date : dates) {
                System.out.println("DATE: " + date);
                System.out.println(" txt: " + date.getOriginalText());
                System.out.println(" dat: " + date.getDate());
                System.out.println(" dat: " + date.getDate().toGEDCOMX());
                System.out.println();
            }
        } catch (GenDateParsingException e) {
            System.out.println("Oops!! " + e.getMessage());
        }
    }
}
