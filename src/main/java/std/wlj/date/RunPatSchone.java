/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.familysearch.standards.date.api.model.DateMetadata;
import org.familysearch.standards.date.api.model.DateResult;
import org.familysearch.standards.date.api.model.GenDateInterpResult;
import org.familysearch.standards.date.common.DateUtil;
import org.familysearch.standards.place.util.PlaceHelper;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @author wjohnson000
 *
 */
public class RunPatSchone {

    static final Set<String> monthNames = new HashSet<>();
    static {
        monthNames.add("january");
        monthNames.add("february");
        monthNames.add("march");
        monthNames.add("april");
        monthNames.add("may");
        monthNames.add("june");
        monthNames.add("july");
        monthNames.add("august");
        monthNames.add("september");
        monthNames.add("october");
        monthNames.add("november");
        monthNames.add("december");
    }


    public static void main(String...args) throws Exception {
        int count = 0;
        int index = 0;
        List<String> results = new ArrayList<>(500_000);

        List<String> dates = Files.readAllLines(Paths.get("C:/temp/date-samples/pat-schone-dates.txt"), StandardCharsets.UTF_8);
        for (String dateStr : dates) {
            if (dateStr.contains("hundred")  ||  dateStr.contains("thousand")) {
                String[] chunks = PlaceHelper.split(dateStr, '\t');
                String cleanDate = cleanInput(chunks[0]);
                results.add("");
                try {
                    DateResult dateRes = DateUtil.interpDate(cleanDate, "en", null, null, null);
                    if (dateRes == null) {
                        results.add(cleanDate + "|null");
                    } else if (dateRes.getDates().isEmpty()) {
                        String month = monthNames.stream()
                            .filter(mn -> cleanDate.toLowerCase().contains(mn))
                            .findFirst().orElse(null);
                        if (month != null) {
                            results.add(cleanDate + "|no-results");
                            System.out.println("\n" + cleanDate + " --> no results");
                        }
                    } else {
                        results.add(cleanDate);
                        for (GenDateInterpResult genDate : dateRes.getDates()) {
                            results.add("|" + genDate.getDate().toGEDCOMX() + "|" + genDate.getAttrAsString(DateMetadata.ATTR_MATCH_TYPE));
                        }
                    }
                } catch (Exception e) {
                    System.out.println("  V2.ext: " + e.getMessage());
                }

                if (++count % 100_000 == 0) {
                    index++;
                    Files.write(Paths.get("C:/temp/long-hand-dates-" + index + ".txt"), results, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
                    results.clear();
                }
            }
        }

        index++;
        Files.write(Paths.get("C:/temp/long-hand-dates-" + index + ".txt"), results, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static String cleanInput(String input) {
        return input.replace("\\n", " ").replace('\u2503', ' ');
    }
}
