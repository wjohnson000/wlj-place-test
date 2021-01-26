/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * Parse the results of running "   " by TYPE and by COUNTRY.
 * @author wjohnson000
 *
 */
public class FindTimelineAttributesCountsParseResults {

    private static final String dataFile = "C:/temp/place-attr-counts.txt";

    public static void main(String...args) throws Exception {
        Map<String, String> id2name    = new HashMap<>();
        Map<String, int[]>  typeCount  = new TreeMap<>();
        Map<String, List<Map<String, Integer>>> nameCount = new TreeMap<>();  
        int[] total = { 0, 0, 0 };

        List<String> lines = Files.readAllLines(Paths.get(dataFile), StandardCharsets.UTF_8);

        // First path -- do stuff by attribute type
        for (String line : lines) {
            String[] fields = PlaceHelper.split(line, '|');
            if (fields.length == 7) {
                String id1  = fields[0];
                String id2  = fields[1];
                String id3  = fields[2];
                String name = fields[3];
                String type = fields[4];
                int    cnt  = Integer.parseInt(fields[6]);

                if (id2.isEmpty()  &&  id3.isEmpty()) {
                    id2name.put(id1, name);
                }

                int[] count = typeCount.computeIfAbsent(type, kk -> new int[] { 0, 0, 0});
                if (id2.isEmpty()  &&  id3.isEmpty()) {
                    count[0] += cnt;
                    total[0] += cnt;
                } else if (id3.isEmpty()) {
                    count[1] += cnt;
                    total[1] += cnt;
                } else {
                    count[2] += cnt;
                    total[2] += cnt;
                }
            }
        }
        typeCount.entrySet().stream()
                         .forEach(ee -> System.out.println(ee.getKey() + "  " + ee.getValue()[0] + "  " + ee.getValue()[1] + "  " + ee.getValue()[2]));
        System.out.println("TOTAL  " + total[0] + "  " + total[1] + "  " + total[2]);

        // Second path -- do stuff by place-name
        for (String line : lines) {
            String[] fields = PlaceHelper.split(line, '|');
            if (fields.length == 7) {
                String id1  = fields[0];
                String id2  = fields[1];
                String id3  = fields[2];
                String name = fields[3];
                String lang = fields[5];
                int    cnt  = Integer.parseInt(fields[6]);

                if (id2.isEmpty()  &&  id3.isEmpty()) {
                    id2name.put(id1, name);
                }

                String key = id2name.computeIfAbsent(id1, kk -> "UNKNOWN") + "|" + id1;
                List<Map<String, Integer>> langCount = nameCount.computeIfAbsent(key, kk -> Arrays.asList(new TreeMap<>(), new TreeMap<>(), new TreeMap<>()));
                int ndx = 0;
                if (id2.isEmpty()  &&  id3.isEmpty()) {
                    ndx = 0;
                } else if (id3.isEmpty()) {
                    ndx = 1;
                } else {
                    ndx = 2;
                }
                Map<String, Integer> lCount = langCount.get(ndx);
                Integer currCnt = lCount.computeIfAbsent(lang, kk -> new Integer(0));
                lCount.put(lang, currCnt+cnt);
            }
        }

        System.out.println("\n\n");
        for (Map.Entry<String, List<Map<String, Integer>>> repEntry : nameCount.entrySet()) {
            String[] temp = PlaceHelper.split(repEntry.getKey(), '|');
            for (int lvl=0;  lvl<repEntry.getValue().size();  lvl++) {
                Map<String, Integer> langCount = repEntry.getValue().get(lvl);
                for (Map.Entry<String, Integer> cntEntry : langCount.entrySet()) {
                    System.out.println(temp[0] + "  " + temp[1] + "  " + lvl + "  " + cntEntry.getKey() + "  " + cntEntry.getValue());
                }
            }
        }

        dumpCountryDataAsTable(nameCount);
    }

    static void dumpCountryDataAsTable(Map<String, List<Map<String, Integer>>> nameCount) {
        System.out.println("\n\n");
        System.out.println("<table class=\"wrapped\">");
        System.out.println("  <colgroup>");
        System.out.println("    <col/>");
        System.out.println("    <col/>");
        System.out.println("    <col/>");
        System.out.println("    <col/>");
        System.out.println("    <col/>");
        System.out.println("  </colgroup>");
        System.out.println("  <tbody>");
        System.out.println("    <tr>");
        System.out.println("      <th>Country</th>");
        System.out.println("      <th>Rep ID</th>");
        System.out.println("      <th>Top-Level</th>");
        System.out.println("      <th>Level-02</th>");
        System.out.println("      <th>Level-03<br/>and Below</th>");
        System.out.println("    </tr>");

        for (Map.Entry<String, List<Map<String, Integer>>> repEntry : nameCount.entrySet()) {
            System.out.println("    <tr>");

            String[] temp = PlaceHelper.split(repEntry.getKey(), '|');
            System.out.println("      <td>" + temp[0] + "</td>");
            System.out.println("      <td>" + temp[1] + "</td>");
            for (int lvl=0;  lvl<repEntry.getValue().size();  lvl++) {
                Map<String, Integer> langCount = repEntry.getValue().get(lvl);
                String counts = langCount.entrySet().stream()
                                                    .map(ee -> ("\"" + (ee.getKey().isEmpty() ? "--" : ee.getKey())) + "\": " + ee.getValue())
                                                    .collect(Collectors.joining("<br/>"));
                System.out.println("      <td>" + counts + "</td>");
            }

            System.out.println("    </tr>");
        }

        System.out.println("  </tbody>");
        System.out.println("  </table>");

    }
}
