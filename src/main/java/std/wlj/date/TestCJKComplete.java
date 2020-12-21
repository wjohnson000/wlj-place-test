/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.familysearch.standards.date.api.model.DateResult;
import org.familysearch.standards.date.api.model.GenDateInterpResult;
import org.familysearch.standards.date.api.model.GenSimpleDate;
import org.familysearch.standards.date.common.DateUtil;
import org.familysearch.standards.date.common.ImperialDictionary;
import org.familysearch.standards.date.common.ImperialDictionary.Dynasty;
import org.familysearch.standards.date.common.ImperialDictionary.Emperor;
import org.familysearch.standards.date.common.ImperialDictionary.Reign;

//import std.wlj.date.v1.DateV1Shim;

/**
 * @author wjohnson000
 *
 */
public class TestCJKComplete {

    static Map<String, Dynasty> dynMap;
    static Map<String, Emperor> empMap;
    static Map<String, Reign> rgnMap;

    static List<String> results = new ArrayList<>();

    public static void main(String... args) throws Exception {
        setupCJKMaps();

        for (Dynasty dynasty : dynMap.values()) {
            String base   = dynasty.getName();
            int    start  = dynasty.getStartYear();
            int    end    = dynasty.getEndYear();
            String locale = dynasty.getLang();
            if (locale.startsWith("zh")) {
                test("dynasty", base, start, end);
            }
        }

        for (Emperor emperor : empMap.values()) {
            String base   = emperor.getName();
            int    start  = emperor.getStartYear();
            int    end    = emperor.getEndYear();
            String locale = emperor.getLang();
            if (locale.startsWith("zh")) {
                test("emperor", base, start, end);
            }
        }

        for (Reign reign : rgnMap.values()) {
            String base   = reign.getName();
            int    start  = reign.getStartYear();
            int    end    = reign.getEndYear();
            String locale = reign.getLang();
            if (locale.startsWith("zh")) {
                test("reign", base, start, end);
            }
        }

        System.out.println();
        System.out.println("========================================================================================================================");
        System.out.println("========================================================================================================================");
        System.out.println();
        results.forEach(System.out::println);
    }

    @SuppressWarnings("unchecked")
    static void setupCJKMaps() throws Exception {
//        ImperialDictionary.getImperialDictionary();
        Field dynMapF = ImperialDictionary.class.getDeclaredField("dynastyMap");
        dynMapF.setAccessible(true);
        Field empMapF = ImperialDictionary.class.getDeclaredField("emperorMap");
        empMapF.setAccessible(true);
        Field rgnMapF = ImperialDictionary.class.getDeclaredField("reignMap");
        rgnMapF.setAccessible(true);

        System.out.println("DDD: " + dynMapF + " .. " + dynMapF.getClass());
        System.out.println("DDD: " + empMapF + " .. " + empMapF.getClass());
        System.out.println("DDD: " + rgnMapF + " .. " + rgnMapF.getClass());

        dynMap = (Map<String, Dynasty>)dynMapF.get(null);
        empMap = (Map<String, Emperor>)empMapF.get(null);
        rgnMap = (Map<String, Reign>)rgnMapF.get(null);
    }

    static void test(String type, String name, int startYear, int endYear) {
//        List<GenDateInterpResult> dates01 = new ArrayList<>();
        DateResult  dates02 = new DateResult();

        String text = name + " 1年 2月 3日";
        System.out.println(text);

//        try {
//            dates01 = DateV1Shim.interpDate(text);
//        } catch (Exception e) {
//            System.out.println("  V1.ext: " + e.getMessage());
//        }

        try {
            dates02 = DateUtil.interpDate(text, "zh", null, null, null);
        } catch (Exception e) {
            System.out.println("  V2.ext: " + e.getMessage());
        }

        results.add("");
//        for (GenDateInterpResult date : dates01) {
//            results.add(text + "|Date 1.0|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(DateMetadata.ATTR_MATCH_TYPE));
//            results.add(text + "|Date 1.0|" + type + "|" + date.getDate().toGEDCOMX() + "|" + startYear + "|" + endYear);
//        }

        for (GenDateInterpResult date : dates02.getDates()) {
//            results.add(text + "|Date 2.0|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(DateMetadata.ATTR_MATCH_TYPE));
            if (date.getDate() instanceof GenSimpleDate) {
                GenSimpleDate sDate = (GenSimpleDate)date.getDate();
                if (sDate.getYear() >= startYear-1  &&  sDate.getYear() <= endYear+1) {
//                    results.add(text + "|Date 2.0|" + type + "|" +  date.getDate().toGEDCOMX() + "|" + startYear + "|" + endYear);
                    results.add(text + "|" + type + "|" +  date.getDate().toGEDCOMX() + "|" + startYear + "|" + endYear);
                }
            }
        }
    }
}