/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.jira;

import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.date.api.model.GenDateInterpResult;
import org.familysearch.standards.date.api.model.Metadata;
import org.familysearch.standards.date.common.DateUtil;

import std.wlj.date.v1.DateV1Shim;

/**
 * @author wjohnson000
 *
 */
public class Story122236Duration {

    static final String[] testDates = {
        "55 years",

        "About 55 years",
        "55 - 60 years",
        "About 55 - 60 years",

        "About 55",
        "55 - 60",
        "About 55 - 60",

        "55 years - 60 years",
        "About 55 years - 60 years",
        "About 55 years 4 months - 55 years 10 months",

        "60 years - 55 years",
    };

    public static void main(String... args) throws Exception {
        for (String text : testDates) {
            System.out.println();
            System.out.println();
            testDateV1(text, null).forEach(System.out::println);
            testDateV2(text, null).forEach(System.out::println);
            testDateV2(text, "period").forEach(System.out::println);
        }
    }

    static List<String> testDateV1(String dateStr, String typeHint) {
        List<String> results = new ArrayList<>();
        
        try {
            for (GenDateInterpResult date : DateV1Shim.interpDate(dateStr)) {
                results.add(dateStr + "|Date 1.0|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(Metadata.ATTR_MATCH_TYPE));
            }
        } catch (Exception e) { }

        return results;
    }

    static List<String> testDateV2(String dateStr, String typeHint) {
        List<String> results = new ArrayList<>();

        try {
            for (GenDateInterpResult date : DateUtil.interpDate(dateStr, "en", null, typeHint, null).getDates()) {
                results.add(dateStr + "|Date 2.0|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(Metadata.ATTR_MATCH_TYPE) + "|" + typeHint + "|" + date.getDate().toSortableKey());
            }
        } catch (Exception e) { }

        return results;
    }
}