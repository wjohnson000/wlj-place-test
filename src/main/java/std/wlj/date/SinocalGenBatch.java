/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Generate an input file for testing dates against https://sinocal.sinica.edu.tw/.
 * @author wjohnson000
 *
 */
public class SinocalGenBatch {

    static List<String> results = new ArrayList<>();

    public static void main(String... args) throws Exception {
        Map<String, Map<String, List<String>>> allData = new LinkedHashMap<>();
        List<String> rawData = Files.readAllLines(Paths.get("C:/temp/sinocal-emp-reigns.txt"), StandardCharsets.UTF_8);
        System.out.println("Size: " + rawData.size());
 
        String dynasty = null;
        String emperor = null;
        String reign = null;

        for (String line : rawData) {
            String tLine = line.trim();
            if (tLine.startsWith("[1")) {
                dynasty = getName(tLine);
                allData.put(dynasty, new LinkedHashMap<>());
            } else if (tLine.startsWith("[2")) {
                emperor = getName(tLine);
                Map<String, List<String>> dynMap = allData.get(dynasty);
                if (dynMap == null) {
                    System.out.println("Emperor with no dynasty: " + emperor + " --> " + tLine);
                } else {
                    dynMap.put(emperor, new ArrayList<>());
                }
            } else if (tLine.startsWith("[3")) {
                reign = getName(tLine);
                Map<String, List<String>> dynMap = allData.get(dynasty);
                if (dynMap == null) {
                    System.out.println("Reign with no dynasty: " + reign + " --> " + tLine);
                } else {
                    List<String> reignList = dynMap.get(emperor);
                    if (reignList == null) {
                        System.out.println("Reign with no emperor: " + reign + " --> " + tLine);
                    } else {
                        reignList.add(reign);
                    }
                }
            }
        }

        for (Map.Entry<String, Map<String, List<String>>> dynEntry : allData.entrySet()) {
            Map<String, List<String>> dynMap = dynEntry.getValue();
            if (dynMap == null  ||  dynMap.isEmpty()) {
                addDate(dynEntry.getKey(), null, null);
            } else {
                for (Map.Entry<String, List<String>> empEntry : dynMap.entrySet()) {
                    List<String> rgnList = empEntry.getValue();
                    addDate(dynEntry.getKey(), empEntry.getKey(), null);
                    if (rgnList != null  &&  ! rgnList.isEmpty()) {
                        for (String rgn : rgnList) {
                            addDate(dynEntry.getKey(), empEntry.getKey(), rgn);
                        }
                    }
                }
            }
        }

        results.forEach(System.out::println);
    }

    static String getName(String line) {
        int ndx0 = line.indexOf('"');
        int ndx1 = line.indexOf('"', ndx0+1);
        if (ndx0 > 0  &&  ndx1 > ndx0) {
            return line.substring(ndx0+1, ndx1);
        } else {
            return null;
        }
    }

    static void addDate(String dynasty, String emperor, String reign) {
        StringBuilder buff = new StringBuilder();

        buff.append(dynasty == null ? "" : dynasty);
        buff.append(",").append(emperor == null ? "" : emperor);
        buff.append(",").append(reign == null ? "" : reign);
        buff.append(",,,");

        results.add(buff.toString());
    }

}