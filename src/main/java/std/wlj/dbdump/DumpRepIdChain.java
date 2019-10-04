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

import org.familysearch.standards.core.logging.Logger;
import org.familysearch.standards.loader.AppConstants;

import std.wlj.datasource.DbConnectionManager;

/**
 * One task of the DB-Loader is to generate the place-rep jurisdiction chains.  This process
 * can be done once and the results cached for later use.
 * <p/>
 * This class compares the performance from the existing query (QUERY_ONE_OLD) with several
 * other approaches, including: 1) splitting it into two queries, and 2) re-formulating it.
 * <p/>
 * After a few attempts it appears that the existing query does about as well as any other
 * approach.
 * 
 * @author wjohnson000
 *
 */
public class DumpRepIdChain {

    private static Logger logger = new Logger(DumpRepIdChain.class);

    private static final String QUERY_ONE_OLD =
        "SELECT rep_id, parent_id, delete_id " +
        "  FROM place_rep " +
        " WHERE rep_id IN " +
        "       (SELECT DISTINCT parent_id FROM place_rep) " +
        "    OR rep_id IN " + 
        "       (SELECT DISTINCT delete_id FROM place_rep WHERE delete_id IS NOT NULL) " +
        " ORDER BY tran_id ASC";

    private static final String QUERY_ONE_OLD_A =
        "SELECT rep_id, parent_id, delete_id " +
        "  FROM place_rep " +
        " WHERE rep_id IN (SELECT DISTINCT parent_id FROM place_rep) " +
        " ORDER BY tran_id ASC";

    private static final String QUERY_ONE_OLD_B =
        "SELECT rep_id, parent_id, delete_id " +
        "  FROM place_rep " +
        " WHERE rep_id IN (SELECT DISTINCT delete_id FROM place_rep WHERE delete_id IS NOT NULL) " +
        " ORDER BY tran_id ASC";

//    private static final String QUERY_TWO_NEW =
//        "SELECT DISTINCT rep.rep_id, rep.parent_id, rep.delete_id " + 
//        "  FROM place_rep AS rep " + 
//        "  JOIN place_rep AS par ON par.parent_id = rep.rep_id OR par.delete_id = rep.rep_id " + 
//        " WHERE rep.tran_id = (SELECT MAX(tran_id) FROM place_rep AS repx WHERE repx.rep_id = rep.rep_id)";

//    private static final String QUERY_THREE_NEW =
//        "SELECT rep.rep_id, rep.parent_id, rep.delete_id " + 
//        "  FROM place_rep AS rep " + 
//        "  JOIN place_rep AS par ON par.parent_id = rep.rep_id OR par.delete_id = rep.rep_id " + 
//        " ORDER BY rep.tran_id ASC";

    private static final String QUERY_THREE_NEW_A =
        "SELECT rep.rep_id, rep.parent_id, rep.delete_id " + 
        "  FROM place_rep AS rep " + 
        "  JOIN (SELECT DISTINCT parent_id FROM place_rep) AS par ON par.parent_id = rep.rep_id " + 
        " ORDER BY rep.tran_id ASC";

    private static final String QUERY_THREE_NEW_B =
        "SELECT rep.rep_id, rep.parent_id, rep.delete_id " + 
        "  FROM place_rep AS rep " + 
        "  JOIN (SELECT DISTINCT delete_id FROM place_rep WHERE delete_id IS NOT NULL) AS del ON del.delete_id = rep.rep_id " + 
        " ORDER BY rep.tran_id ASC";

    private static Map<Integer, String> placeRepChainMap = new TreeMap<Integer, String>();

    public static void main(String...args) throws IOException {
        long time0, time1;
//        DataSource ds = DbConnectionManager.getDataSourceSams();
        DataSource ds = DbConnectionManager.getDataSourceAwsDev();
        
        time0 = System.nanoTime();
        seedPlaceChain(ds, QUERY_THREE_NEW_A, QUERY_THREE_NEW_B);  // Slowest [maybe because it's first] -- same results as others
        time1 = System.nanoTime();
        System.out.println("Time: " + (time1-time0)/1_000_000.0);
        System.out.println("Size: " + placeRepChainMap.size());
        dumpChain("C:/temp/chain-03-three-ab.txt");
        
        time0 = System.nanoTime();
        seedPlaceChain(ds, QUERY_ONE_OLD_A, QUERY_ONE_OLD_B);  // Fast -- same results as others
        time1 = System.nanoTime();
        System.out.println("Time: " + (time1-time0)/1_000_000.0);
        System.out.println("Size: " + placeRepChainMap.size());
        dumpChain("C:/temp/chain-01-one-ab.txt");

        time0 = System.nanoTime();
        seedPlaceChain(ds, QUERY_ONE_OLD);  // Fast -- same results as others
        time1 = System.nanoTime();
        System.out.println("Time: " + (time1-time0)/1_000_000.0);
        System.out.println("Size: " + placeRepChainMap.size());
        dumpChain("C:/temp/chain-01-one.txt");

//        time0 = System.nanoTime();
//        seedPlaceChain(ds, QUERY_TWO_NEW);
//        time1 = System.nanoTime();
//        System.out.println("Time: " + (time1-time0)/1_000_000.0);
//        System.out.println("Size: " + placeRepChainMap.size());
//        dumpChain("C:/temp/chain-02-two.txt");

//        time0 = System.nanoTime();
//        seedPlaceChain(ds, QUERY_THREE_NEW);
//        time1 = System.nanoTime();
//        System.out.println("Time: " + (time1-time0)/1_000_000.0);
//        System.out.println("Size: " + placeRepChainMap.size());
//        dumpChain("C:/temp/chain-03-three.txt");
    }

    static void seedPlaceChain(DataSource dataSource, String... queries) {
        placeRepChainMap.clear();

        // Save all child-parent associations where the child is also a parent
        Map<Integer, Integer> childParentMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> repReplaceMap  = new HashMap<Integer, Integer>();

        for (String query : queries) {
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
                logger.error(ex, AppConstants.MODULE_NAME, "Unable to generate chains");
            }
        }

        logger.info(null, AppConstants.MODULE_NAME, "Chain data retrieved ... start generation of chains");
        buildPlaceRepChains(childParentMap, repReplaceMap);
        logger.info(null, AppConstants.MODULE_NAME, "Place-Rep ID chain generation complete");
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
                    logger.error(null, AppConstants.MODULE_NAME, "Circular reference in place-chain",
                                       "ChildId", String.valueOf(childId),
                                       "RepId", String.valueOf(repId),
                                       "ParId", String.valueOf(parId));
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
