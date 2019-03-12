/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * Determine how many reps have children, i.e., are parents ...
 * 
 * @author wjohnson000
 *
 */
public class HowManyParents {

    private static final String baseDir  = "C:/temp/db-dump";
    private static final String repFile  = "place-rep-all.txt";

    public static void main(String...args) throws IOException {
        Set<String> parents = new HashSet<>();
        Map<String, String> delStuff = new HashMap<>();

        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("REP.main: " + lineCnt);

                String   rowData   = scan.nextLine();
                String[] rowFields = PlaceHelper.split(rowData, '|');
                if (rowFields.length > 14) {
                    String sRepId = rowFields[0];
                    String sParId = rowFields[2];
                    String sDelId = rowFields[9];
                    if (sDelId.isEmpty()) {
                        parents.add(sParId);
                    } else {
                        delStuff.put(sRepId, sParId);
                    }
                }
            }
        }

        System.out.println("Parent-Count: " + parents.size());
    }
}
