package std.wlj.dbdump;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import std.wlj.util.DbConnectionManager;

/**
 * One task of the DB-Loader is to generate the place-rep jurisdiction chains.  This process
 * can be done once and the results cached for later use.
 * <p/>
 * 
 * This class compares the performance from the existing query (QUERY_ONE_OLD) with several
 * other approaches, including: 1) splitting it into two queries, and 2) re-formulating it.
 * <p/>
 * 
 * After a few attempts it appears that the existing query does about as well as any other
 * approach.
 * 
 * @author wjohnson000
 *
 */
public class DbDumpRepIdChain {

    static final String fileBase = "C:/temp/db-dump";
    static final String fileName = "rep-chain-all.txt";

    private static final String QUERY_ONE_OLD =
        "SELECT rep_id, parent_id, delete_id " +
        "  FROM place_rep " +
        " WHERE rep_id IN " +
        "       (SELECT DISTINCT parent_id FROM place_rep) " +
        "    OR rep_id IN " + 
        "       (SELECT DISTINCT delete_id FROM place_rep WHERE delete_id IS NOT NULL) " +
        " ORDER BY tran_id ASC";


    private static Map<Integer, String> placeRepChainMap = new TreeMap<Integer, String>();

    public static void main(String...args) throws IOException {
        long time0, time1;
        DataSource ds = DbConnectionManager.getDataSourceAwsDev();
        
        time0 = System.nanoTime();
        seedPlaceChain(ds, QUERY_ONE_OLD);  // Fast -- same results as others
        time1 = System.nanoTime();
        System.out.println("REP-ID-CHAIN.Size: " + placeRepChainMap.size() + " ... Time: " + (time1-time0)/1_000_000.0);
        dumpChain(fileBase + "/" + fileName);

        if (args.length == 0) {
            System.exit(0);
        }
    }

    static void seedPlaceChain(DataSource dataSource, String query) {
        placeRepChainMap.clear();

        // Save all child-parent associations where the child is also a parent
        Map<Integer, Integer> childParentMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> repReplaceMap  = new HashMap<Integer, Integer>();

        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(query)) {
            while (rset.next()) {
                int repId = rset.getInt("rep_id");
                int parId = rset.getInt("parent_id");
                int delId = rset.getInt("delete_id");
                childParentMap.put(repId, parId);
                if (delId > 0) {
                    repReplaceMap.put(repId, delId);
                }
            }
        } catch (SQLException ex) {
            System.out.println("Unable to generate chains --> " + ex.getMessage());
        }

        buildPlaceRepChains(childParentMap, repReplaceMap);
    }

    static void buildPlaceRepChains(Map<Integer, Integer> childParentMap, Map<Integer, Integer> repReplaceMap) {
        for (Integer childId : childParentMap.keySet()) {
            List<Integer> repIdChain = new ArrayList<>();

            Integer repId = childId;
            while (repReplaceMap.containsKey(repId)) {
                repId = repReplaceMap.get(repId);
            }
            repIdChain.add(repId);

            Integer parId = childParentMap.get(repId);
            while (parId != null  &&  parId > 0) {
                while (repReplaceMap.containsKey(parId)) {
                    parId = repReplaceMap.get(parId);
                }
                if (repIdChain.contains(parId)) {
                    System.out.println("Circular reference in place-chain" +
                                       "; ChildId" + String.valueOf(childId) +
                                       "; RepId" + String.valueOf(repId) +
                                       "; ParId" + String.valueOf(parId));
                    repIdChain.clear();
                    repIdChain.add(childId);
                    break;
                } else {
                    repIdChain.add(parId);
                    parId = childParentMap.get(parId);
                }
            }

            String chain = repIdChain
                .stream()
                .map(val -> String.valueOf(val))
                .collect(Collectors.joining(","));
            placeRepChainMap.put(childId, chain);
        }
    }

    static void dumpChain(String fileName) throws IOException {
        List<String> funny = placeRepChainMap.entrySet().stream()
            .map(entry -> entry.getKey() + "|" + entry.getValue())
            .collect(Collectors.toList());
        Files.write(Paths.get(fileName), funny, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
