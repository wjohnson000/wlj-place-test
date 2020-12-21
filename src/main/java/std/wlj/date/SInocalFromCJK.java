/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.familysearch.standards.date.common.ImperialDictionary;
import org.familysearch.standards.date.common.ImperialDictionary.Dynasty;
import org.familysearch.standards.date.common.ImperialDictionary.Emperor;
import org.familysearch.standards.date.common.ImperialDictionary.Reign;

/**
 * Generate an input file for testing dates against https://sinocal.sinica.edu.tw/.
 * @author wjohnson000
 *
 */
public class SInocalFromCJK {

    static Map<String, Dynasty> dynMap;
    static Map<String, Emperor> empMap;
    static Map<String, Reign> rgnMap;

    static List<String> results = new ArrayList<>();

    public static void main(String... args) throws Exception {
        setupCJKMaps();

        for (Dynasty dynasty : dynMap.values()) {
            if (dynasty.getLang().startsWith("zh")) {
                addDate(dynasty.getName(), null, null, 1, 2, 3);
            }
        }

        for (Emperor emperor : empMap.values()) {
            if (emperor.getLang().startsWith("zh")) {
                if (emperor.getDynasty() != null) {
                    addDate(emperor.getDynasty().getName(), emperor.getName(), null, 1, 2, 3);
                } else {
                    System.out.println("EMP.no-dynasty: " + emperor.getName() + " .. " + emperor.getStartYear() + " . " + emperor.getEndYear());
                }
//                addDate(null, emperor.getName(), null, 1, 2, 3);
            }
        }

        for (Reign reign : rgnMap.values()) {
            if (reign.getLang().startsWith("zh")) {
                if (reign.getDynasty() != null  &&  reign.getEmperor() != null) {
                    addDate(reign.getDynasty().getName(), reign.getEmperor().getName(), reign.getName(), 1, 2, 3);
                } else {
//                    addDate(null, null, reign.getName(), 1, 2, 3);
                }
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

    static void addDate(String dynasty, String emperor, String reign, int year, int month, int day) {
        StringBuilder buff = new StringBuilder();

        buff.append(dynasty == null ? "" : dynasty);
        buff.append(",").append(emperor == null ? "" : emperor);
        buff.append(",").append(reign == null ? "" : reign);
        buff.append(",").append(year);
        buff.append(",").append(month);
        buff.append(",").append(day);

        results.add(buff.toString());
    }
}