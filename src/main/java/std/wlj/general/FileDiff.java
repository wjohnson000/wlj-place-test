/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author wjohnson000
 *
 */
public class FileDiff {

    private static final String dir1 = "C:/Users/wjohnson000/git/std-ws-place";
//    private static final String dir2 = "C:/Users/wjohnson000/git/temp/std-ws-place";
    private static final String dir2 = "C:/temp/delete-me/std-ws-place-master";
    private static final String subd = "place-webservice/src/main/java/org/familysearch/standards/place/ws/snapshot";
    private static final String file = "SnapshotsConstants.java";

    public static void main(String... args) throws Exception {
        byte[] one = Files.readAllBytes(Paths.get(dir1, subd, file));
        byte[] two = Files.readAllBytes(Paths.get(dir2, subd, file));

        System.out.println("ONE: " + one.length);
        System.out.println("TWO: " + two.length);

        for (int i=0;  i<one.length;  i++) {
            if (one[i] != two[i]) {
                System.out.println("I." + i + " --> " + one[i] + " vs. " + two[i]);
            }
        }
    }
}
