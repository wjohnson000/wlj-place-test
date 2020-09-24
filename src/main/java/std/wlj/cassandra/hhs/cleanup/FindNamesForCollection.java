/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.cassandra.hhs.cleanup;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.familysearch.standards.place.util.PlaceHelper;


/**
 * @author wjohnson000
 *
 */
public class FindNamesForCollection {

    public static void main(String...args) throws Exception {
        List<String> names = Files.readAllLines(Paths.get("C:/temp/dev-name-details-all.txt"), StandardCharsets.UTF_8);

        System.out.println(">>>>> Start ...");
        List<String> oxford = names.stream()
             .map(row -> PlaceHelper.split(row, '|'))
             .filter(rr -> rr.length == 5)
             .filter(rr -> rr[4].equals("MMM9-FRZ"))
             .map(rr -> rr[1] + " [" + rr[0] + "]")
             .collect(Collectors.toList());
        Collections.sort(oxford);
        oxford.forEach(System.out::println);
    }
}
