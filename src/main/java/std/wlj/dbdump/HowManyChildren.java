/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class HowManyChildren {

    private static final String baseDir  = "C:/temp/db-dump";
    private static final String repFile  = "place-rep-all.txt";

    public static void main(String...args) throws IOException {
        Map<Integer, Integer> repParent = new HashMap<>();
        Map<Integer, Integer> repDelId  = new HashMap<>();

        Map<Integer, Integer> repChildCount = new TreeMap<>();

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

                    try {
                        int repId = Integer.parseInt(sRepId);
                        try {
                            int parId = Integer.parseInt(sParId);
                            if (parId > 0) {
                                repParent.put(repId, parId);
                            }

                            int delId = Integer.parseInt(sDelId);
                            if (delId > 0) {
                                repDelId.put(repId, delId);
                            }
                        } catch(Exception ex) {
                            // This is fine -- there is no parent or delete-id
                        }
                    } catch(Exception ex) {
                        // This is fine -- there is no rep-id (first line of the file ...)
                    }
                }
            }
        }

        // Map reps to their parent (or parent replacement ...)
        for (Map.Entry<Integer, Integer> entry : repParent.entrySet()) {
            Integer repId = entry.getKey();

            if (! repDelId.containsKey(repId)) {
                Integer parId = entry.getValue();
                while (repDelId.containsKey(parId)) {
                    parId = repDelId.get(parId);
                }

                Integer count = repChildCount.getOrDefault(parId, Integer.valueOf(0));
                repChildCount.put(parId, count+1);
            }
        }

//        printEm(repChildCount, 250);
//        printEm(repChildCount, 500);
        printEm(repChildCount,  1_000,   2_499);
        printEm(repChildCount,  2_500,   4_999);
        printEm(repChildCount,  5_000,   9_999);
        printEm(repChildCount, 10_000, 100_000);
    }

    static void printEm(Map<Integer, Integer> childCount, int lowerLimit, int upperLimit) {
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("====================================================");
        System.out.println("  " + lowerLimit + "-" + upperLimit); 
        System.out.println("====================================================");

        childCount.entrySet().stream()
            .filter(entry -> entry.getValue() >= lowerLimit)
            .filter(entry -> entry.getValue() <= upperLimit)
            .forEach(System.out::println);
    }
}
