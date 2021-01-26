/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import org.familysearch.standards.loader.sql.FileResultSet;

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
        findAllDeletes(deleteData);
    }

    static Map<Integer, Integer> getDeleteData() throws Exception {
        Map<Integer, Integer> results = new HashMap<>();

        int okCount = 0;
        int delCount = 0;
        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(dataDir, repFile));

            while(rset.next()) {
                int repId = rset.getInt("rep_id");
                int delId = rset.getInt("delete_id");
                if (delId > 0) {
                    delCount++;
                    results.put(repId, delId);
                } else {
                    okCount++;
                }
            }
        }

        System.out.println("OK-COUNT: " + okCount);
        System.out.println("DEL-COUNT: " + delCount);

        return results;
    }

    static void findAllDeletes(Map<Integer, Integer> deleteData) {
        Map<Integer, Integer> depths     = new TreeMap<>();
        Map<Integer, String>  deepest    = new TreeMap<>();
        Map<Integer, Integer> delInto    = new TreeMap<>();
        Map<Integer, Long>    delIntoCnt = new TreeMap<>();

        List<Integer> idChain = new ArrayList<>(10_000);
        for (Map.Entry<Integer, Integer> entry : deleteData.entrySet()) {
            int oldId = entry.getKey();
            int newId = entry.getValue();

            idChain.clear();
            idChain.add(oldId);
            idChain.add(newId);
            while (deleteData.containsKey(newId)) {
                newId = deleteData.get(newId);
                idChain.add(newId);
            }

            Integer count01 = depths.getOrDefault(idChain.size(), new Integer(0));
            depths.put(idChain.size(), count01+1);
            if (idChain.size() > 10) {
                deepest.put(oldId, idChain.stream().map(ii -> String.valueOf(ii)).collect(Collectors.joining(",")));
            }

            Integer count02 = delInto.getOrDefault(newId, new Integer(0));
            delInto.put(newId, count02+1);
        }
        delIntoCnt.putAll(delInto.values().stream().collect(Collectors.groupingBy(vv -> vv, Collectors.counting())));

        // Print the depths, and count of each chain of deletions
        System.out.println("\nChain depths, and # of such chains ...");
        depths.entrySet().forEach(System.out::println);

        // Print the deepest chains
        System.out.println("\nDeepest chains ...");
        deepest.entrySet().forEach(System.out::println);

        // Print the chain depth and how many of them there are
        System.out.println("\nDelete-into, and # of such chains ...");
        delIntoCnt.entrySet().forEach(System.out::println);

        // Print details about deletions INTO
        System.out.println("\nMost delete-into ... " + delInto.size());
        delInto.entrySet().stream().filter(ee -> ee.getValue() >= 2500).forEach(System.out::println);
    }
}
