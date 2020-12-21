/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.date.api.model.DateResult;
import org.familysearch.standards.date.api.model.GenDateInterpResult;
import org.familysearch.standards.date.common.DateUtil;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * Generate an input file for testing dates against https://sinocal.sinica.edu.tw/.
 * @author wjohnson000
 *
 */
public class SinocalDate20 {

    static List<String> candidates = new ArrayList<>();
    static List<String> results = new ArrayList<>();

    public static void main(String... args) throws Exception {
        List<String> rawData = Files.readAllLines(Paths.get("C:/temp/sinocal-results.csv"), StandardCharsets.UTF_8);
        System.out.println("Size: " + rawData.size());
 
        for (String line : rawData) {
            String[] chunks = PlaceHelper.split(line.trim(), ',');
            if (chunks.length == 7) {
                candidates.add(makeDate(chunks));
            }
        }

        candidates.forEach(SinocalDate20::interpDate);
        results.forEach(System.out::println);
    }

    static String makeDate(String[] dateParts) {
        StringBuilder buff = new StringBuilder();

        buff.append(dateParts[0]);
        buff.append(dateParts[1]);
        buff.append(dateParts[2]);
        if (! dateParts[3].isEmpty()) {
            buff.append(" ").append(dateParts[3]).append("年");
            buff.append(" ").append(dateParts[4]).append("月");
            buff.append(" ").append(dateParts[5]).append("日");
        }

        return buff.toString();
    }

    static void interpDate(String dateStr) {
        try {
            DateResult dates = DateUtil.interpDate(dateStr, "zh", null, null, null);
            for (GenDateInterpResult date : dates.getDates()) {
                results.add(dateStr + "|" + date.getDate().toGEDCOMX());
            }
        } catch(Exception ex) {
            System.out.println("  V2.ext: " + ex.getMessage());
        }
    }
}