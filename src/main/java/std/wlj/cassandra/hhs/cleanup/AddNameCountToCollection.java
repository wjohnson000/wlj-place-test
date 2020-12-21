/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs.cleanup;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class AddNameCountToCollection {

    public static void main(String...ars) throws Exception {
        List<String> rows = Files.readAllLines(Paths.get("C:/temp/dev-all-collection.txt"), StandardCharsets.UTF_8);
        Map<String, String[]> rowData = rows.stream()
                                            .map(row -> PlaceHelper.split(row, '|'))
                                            .collect(Collectors.toMap(rr -> rr[0], rr -> rr));

        List<String> names = Files.readAllLines(Paths.get("C:/temp/dev-name-details-all.txt"), StandardCharsets.UTF_8);
        Map<String, Long> collCount = names.stream()
                                              .map(row -> PlaceHelper.split(row, '|'))
                                              .filter(row -> row.length == 5)
                                              .collect(Collectors.groupingBy(rr -> rr[4], Collectors.counting()));
        
        rowData.values().stream()
                        .forEach(rr -> System.out.println(rr[0] + "|" + rr[1] + "|" + collCount.getOrDefault(rr[0], 0L) +"|" + rr[2] + "|" + rr[3] + "|" + rr[4] + "|" + rr[5]));

        Set<String> missingCollIds = new TreeSet<>();
        missingCollIds.addAll(collCount.keySet());
        missingCollIds.removeAll(rowData.keySet());
        missingCollIds.stream().forEach(rr -> System.out.println(rr + "|Unknown|" + collCount.getOrDefault(rr, 0L)));
    }
}
