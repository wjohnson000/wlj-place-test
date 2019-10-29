/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.util.*;

import org.familysearch.standards.loader.sql.FileResultSet;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * Create a structure with a key of (rep-id) and a value of (all rep-ids that have been transitively
 * deleted into this one ...).  As of October 2019 there are:
 * <ul>
 *   <li>5058026 deleted reps</li>
 *   <li>499382 unique reps that have been deleted into</li>
 *   <li>The longest delete chain is
 *       SELECT * FROM place_rep WHERE rep_id = 8606423;
 *       SELECT * FROM place_rep WHERE rep_id = 5369684;
 *       SELECT * FROM place_rep WHERE rep_id = 5369685;
 *       SELECT * FROM place_rep WHERE rep_id = 5369686;
 *       SELECT * FROM place_rep WHERE rep_id = 5369687;
 *       SELECT * FROM place_rep WHERE rep_id = 5369688;
 *       SELECT * FROM place_rep WHERE rep_id = 5369694;
 *       SELECT * FROM place_rep WHERE rep_id = 5369695;
 *       SELECT * FROM place_rep WHERE rep_id = 5369696;
 *       SELECT * FROM place_rep WHERE rep_id = 5369697;
 *       SELECT * FROM place_rep WHERE rep_id = 5369698;
 *       SELECT * FROM place_rep WHERE rep_id = 5369699;
 *       SELECT * FROM place_rep WHERE rep_id = 396401; (still active!!)
 *   </li>
 *   <li>Rep "4156" (Bosnia & Herzegovina) has nearly 44,400 reps deleted into it, of
 *       which about 14,800 are direct deletes, the others transitively deleted!!</li>
 * </ul>
 * 
 * @author wjohnson000
 *
 */
public class FindTransitiveDeletes {

    private static final String dataDir   = "C:/temp/db-dump";
    private static final String repFile   = "place-rep-all.txt";
    private static final String DELIMITER = "\\|";

    public static void main(String... args) throws Exception {
        Map<Integer, Integer> deleteData = getDeleteData();
        System.out.println("DEL-COUNT: " + deleteData.size());

        Map<Integer, String> allDeleteData = findAllDeletes(deleteData);
        System.out.println("TRX-COUNT: " + allDeleteData.size());
//        allDeleteData.entrySet().forEach(System.out::println);
    }

    static Map<Integer, Integer> getDeleteData() throws Exception {
        Map<Integer, Integer> results = new HashMap<>();

        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(dataDir, repFile));

            while(rset.next()) {
                int repId = rset.getInt("rep_id");
                int delId = rset.getInt("delete_id");
                if (delId > 0) {
                    results.put(repId, delId);
                }
            }
        }

        return results;
    }

    static Map<Integer, String> findAllDeletes(Map<Integer, Integer> deleteData) {
        Map<Integer, String> results = new HashMap<>();

        int deepest = 0;
        String longest = "";
        for (Map.Entry<Integer, Integer> entry : deleteData.entrySet()) {
            int oldId = entry.getKey();
            int newId = entry.getValue();

            int depth = 1;
            while (deleteData.containsKey(newId)) {
                depth++;
                newId = deleteData.get(newId);
            }

            if (depth > deepest) {
                deepest = depth;
                System.out.println("  Depth: " + depth + " --> " + oldId);
            }

            String delIds = results.getOrDefault(newId, null);
            delIds = (delIds == null) ? String.valueOf(oldId) : delIds + "," + oldId;
            if (delIds.length() > longest.length()) {
                longest = delIds;
                String[] blah = PlaceHelper.split(longest, ',');
                System.out.println("  Longest: " + newId + " --> " + blah.length);
//                Arrays.stream(blah).forEach(System.out::println);
            }

            results.put(newId, delIds);
        }


        return results;
    }
}
