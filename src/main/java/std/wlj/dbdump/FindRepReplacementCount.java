/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class FindRepReplacementCount {

    static final String baseDir  = "C:/temp/db-dump";
    static final String repFile  = "place-rep-all.txt";

    static final Map<String, String>      delRepId   = new HashMap<>();
    static final Map<String, Set<String>> delToRepId = new TreeMap<>();

    public static void main(String... args) throws Exception {

        // Step 00 -- Read place-reps and save id + replacementId for deleted reps
        int lineCnt = 0;
        System.out.println();
        try(FileInputStream fis = new FileInputStream(new File(baseDir, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("REP.read: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 11) {
                    String repId  = chunks[0];
                    String newId  = chunks[9];

                    if (!newId.trim().isEmpty()) {
                        delRepId.put(repId, newId);
                        Set<String> delToList = delToRepId.computeIfAbsent(newId, kk -> new TreeSet<>());
                        delToList.add(repId);
                    }
                }
            }
        }
        System.out.println();
        System.out.println("DelCount: " + delRepId.size());
        System.out.println("NewCount: " + delToRepId.size());

        // Step 01 -- Transitively associate deleted reps to new parents if their replacement
        //            has also been deleted ...
        System.out.println();
        boolean again = true;
        while (again) {
            System.out.println(" ... again ...");
            Set<String> removeIds = new HashSet<>();
            for (Map.Entry<String, Set<String>> entry : delToRepId.entrySet()) {
                String newId = delRepId.get(entry.getKey());
                if (newId != null) {
                    Set<String> tFromIds = delToRepId.get(entry.getKey());
                    Set<String> tToIds   = delToRepId.get(newId);
                    if (tFromIds == null  ||  tToIds == null) {
                        System.out.println("??? " + entry.getKey() + " --> " + newId);
                    } else {
                        tToIds.addAll(tFromIds);
                        removeIds.add(entry.getKey());
                    }
                }
            }

            if (removeIds.isEmpty()) {
                again = false;
            } else {
                delToRepId.keySet().removeAll(removeIds);
            }
        }
        System.out.println();
        System.out.println("DelCount: " + delRepId.size());
        System.out.println("NewCount: " + delToRepId.size());

        // Step 02 -- Print those reps that have the most deleted ids associated with it
        System.out.println("\n\n");
        for (Map.Entry<String, Set<String>> entry : delToRepId.entrySet()) {
            if (entry.getValue().size() > 100) {
                System.out.println("REP: " + entry.getKey() + " --> " + entry.getValue().size());
            }
        }        
    }
}
