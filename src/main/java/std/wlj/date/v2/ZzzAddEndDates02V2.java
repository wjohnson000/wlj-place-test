/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class ZzzAddEndDates02V2 {

    private static String IMPERIAL_ZH_FILE = "C:/temp/imperial_zh.xml";

    static Map<String, String> rangeData = new HashMap<>();

    public static void main(String...args) throws Exception {
        try {
            findRanges();
            fixRanges();
        } catch (IOException ex) {
            System.out.println("OOPS!! " + ex);
        }

        System.exit(0);
    }

    static void findRanges() throws Exception {
        List<String> data = Files.readAllLines(Paths.get(IMPERIAL_ZH_FILE), StandardCharsets.UTF_8);

        int lineno = 0;
        String  prevKey = null;
        for (String datum : data) {
            lineno++;
            if (datum.trim().startsWith("<word-group ")) {
                prevKey = null;
            } else if (datum.trim().startsWith("<word ")) {
                int pos01 = datum.indexOf("meta");
                if (pos01 > 0) {
                    int pos02 = datum.indexOf(">", pos01);
                    String meta = datum.substring(pos01+6, pos02-1);
                    String[] chunks = PlaceHelper.split(meta, '|');
                    if (chunks.length == 3) {
                        rangeData.put(chunks[0], chunks[2]);
                    } else if (chunks.length == 4) {
                        System.out.println(">>>* " + lineno + "." + datum);
                    } else {
                        System.out.println(">>>? " + lineno + "." + datum);
                    }
                    if (prevKey != null  &&  ! rangeData.containsKey(prevKey)  &&  ! prevKey.equals(chunks[0])) {
                        rangeData.put(prevKey, chunks[1]);
                    }
                    prevKey = chunks[0];
                }
            }
        }
    }

    static void fixRanges() throws Exception {
        List<String> data = Files.readAllLines(Paths.get(IMPERIAL_ZH_FILE), StandardCharsets.UTF_8);

        for (String datum : data) {
            String line = datum;
            if (datum.trim().startsWith("<word ")) {
                int pos01 = datum.indexOf("meta");
                if (pos01 > 0) {
                    int pos02 = datum.indexOf(">", pos01);
                    String meta = datum.substring(pos01+6, pos02-1);
                    String[] chunks = PlaceHelper.split(meta, '|');
                    if (chunks.length == 2) {
                        String endYr = rangeData.get(chunks[0]);
                        if (endYr != null) {
                            line = datum.substring(0, pos02-1) + "|" + endYr + datum.substring(pos02-1);
                        }
                    }
                }
            }

            System.out.println(line);
        }
    }
}
