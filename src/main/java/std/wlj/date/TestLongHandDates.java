/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.model.DateResult;
import org.familysearch.standards.date.model.GenDateInterpResult;
import org.familysearch.standards.date.shared.SharedUtil;

/**
 * @author wjohnson000
 *
 */
public class TestLongHandDates {

    public static void main(String... args) throws Exception {
        List<String> textes = textesFromRaw();
        for (String text : textes) {

            try {
                DateResult dateRes = DateUtil.interpDate(text, StdLocale.ENGLISH, null, null, null);
                System.out.println("\n" + text);
                if (dateRes.getDates().isEmpty()) {
                    System.out.println("    <none>|<none>");
                } else {
                    for (GenDateInterpResult date : dateRes.getDates()) {
                        System.out.println("    " + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
                    }
                }
            } catch (Exception e) {
                System.out.println("  V2.ext: " + e.getMessage());
            }
        }
    }

    static List<String> textesFromRaw() {
        List<String> textes = new ArrayList<>();
        textes.add("April thirteenth eighteen   hundred and fifty five");
        textes.add("April thirteenth, eighteen   hundred and fifty five");
        textes.add("first day of May one thousand eight hundred & forty four");
        textes.add("seventeenth day of May, one thousand eight hun- dred and twenty");
//        textes.add("Twentieth day of September in the  year one thousand eight hundred and ninety-eight");
//        textes.add("ABOUT 1st day of October in the year of our Lord One Thousand eight hundred and twenty-two"); 
//        textes.add("26th day of February Anno Domini 1870"); 
//        textes.add("Twenty sixth day of February in the year of our Lord one thousand eight hundred and seventy");
//        textes.add("tenth day of September in the year one   thousand Eight hundred and sixty-nine");
//        textes.add("Twenty ninth day of January in the   of our Lord one thousand eight hundred and fifty one");
//        textes.add("Eleventh day of October in the  year of our Lord one thousand Eight hundred and fifteen");
//        textes.add("first day of February  the year one thousand Eight hundred and Seventy nine");
//        textes.add("thirteenth day of April one thousand Eight hundred and thirteen");
//        textes.add("ninth day of February in  the year one thousand eight hundred and fifty four");
//        textes.add("October in the year of our Lord one thousand Eight  hundred and forty nine");
//        textes.add("the year one thousand eight hundred");
//        textes.add("year of our Lord one thousand eight hundred");
//        textes.add("February in the year of our Lord one thousand eight hundred and thirty-seven");
//        textes.add("18 day of March in the year one thousand eight hundred and seventy-nine");
//        textes.add("second day of May in the year one thousand eight  hundred and thirty nine");
//        textes.add("first day of April, in the year one thousand eight hundred and thirty   six");
//        textes.add("1st day of October in the year of our Lord One Thousand eight hundred and twenty-two");
//        textes.add("About First day of October in the year of our Lord One Thousand eight hundred and twenty-two");
//        textes.add("26th day of February Anno Domini 1870");
//        textes.add("Twenty sixth day of February in the year of our Lord one thousand eight hundred and seventy");

        return textes;
    }
}