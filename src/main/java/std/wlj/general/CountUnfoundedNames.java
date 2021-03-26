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
        int multi  = 0;
        int target = 10000;
        for (String name : names) {
            count++;
            if (isMulti(name)) {
                multi++;
            }

            String[] cols = split(name);
            total += Integer.parseInt(cols[cols.length-1]);
            if (total > target) {
                System.out.println(count + "[total=" + total + "]   [target=" + target + "]");
                target += 10000;
            }
        }
        System.out.println("TOTAL: " + total);
        System.out.println("\nMULTI: " + multi);

        System.exit(0);
    }

    static String[] split(String name) {
        if (name.charAt(0) == '"') {
            int ndx0 = name.indexOf('"', 1);
            String text = name.substring(1, ndx0).replace(',', ' ');
            String newName = text + name.substring(ndx0+1);
            return PlaceHelper.split(newName, ',');
        } else {
            return PlaceHelper.split(name, ',');
        }
    }

    static boolean isMulti(String name) {
        int ndx0, ndx1, ndx2, ndx3;

        ndx0 = name.indexOf('"');
        if (ndx0 == -1) {
            ndx1 = name.indexOf('"', ndx0+1);
            ndx2 = name.indexOf(' ');
            ndx3 = name.indexOf(',');
            return ((ndx2 >= 0  &&  ndx2 < ndx3  &&  ndx2 < ndx1)  ||  ndx3 >= 0  &&  ndx3 < ndx1);
        } else {
            ndx2 = name.indexOf(' ');
            ndx3 = name.indexOf(',');
            return (ndx2 >= 0  &&  ndx2 < ndx3);
        }
    }
}
