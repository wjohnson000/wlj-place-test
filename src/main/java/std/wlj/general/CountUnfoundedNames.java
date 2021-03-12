/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * Parse the file "C:/temp/unfounded-names.csv" to determine how many names need to be defined to reach
 * certain milestones.
 * 
 * @author wjohnson000
 *
 */
public class CountUnfoundedNames {

    public static void main(String... args) throws Exception {
        List<String> names = Files.readAllLines(Paths.get("C:/temp", "unfounded-names.csv"), StandardCharsets.UTF_8);
        names.remove(0);
        System.out.println("Rows: " + names.size());

        int count  = 0;
        int total  = 0;
        int target = 1000;
        for (String name : names) {
            count++;
            String[] cols = PlaceHelper.split(name, ',');
            total += Integer.parseInt(cols[cols.length-1]);
            if (total > target) {
                System.out.println(count + "[total=" + total + "]   [target=" + target + "]");
                target += 1000;
            }
        }
        System.out.println("TOTAL: " + total);

        System.exit(0);
    }
}
