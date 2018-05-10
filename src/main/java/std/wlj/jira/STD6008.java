/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.jira;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.familysearch.standards.loader.sql.FileResultSet;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * The input 'Lamar, Texas' results in 'Lamar, Republic of Texas'.  It does NOT return the
 * desired result of 'Lamar, Texas, USA'.
 * 
 * The cause is the new "FullParsePathSearch" which finds a perfect match in the former, so
 * never looks for the latter.
 * 
 * This little application will look for cases where a top-level place and a second-level
 * place share the same variant name.  Sigh ...
 * 
 * @author wjohnson000
 *
 */
public class STD6008 {

    static final String REP_FILE = "C:/temp/db-dump/place-rep-all.txt";
    static final String VARIANT_FILE = "C:/temp/db-dump/variant-name-all.txt";

    static Set<Integer> topPlaceIds = new HashSet<>();
    static Set<Integer> secondPlaceIds = new HashSet<>();
    static Set<String>  topLevelNames = new HashSet<>();
    static Map<Integer, Integer> topLevel = new HashMap<>();
    static Map<Integer, Integer> secondLevel = new HashMap<>();
    static Map<Integer, Set<String>> varNames = new HashMap<>();

    public static void main(String...args) throws SQLException {
        loadTopLevelReps();
        loadSecondLevelReps();
        loadVariantNames();
        findCollisions();
    }

    static void loadTopLevelReps() throws SQLException {
        try(FileResultSet rset = new FileResultSet()) {
            rset.setSeparator("\\|");
            rset.openFile(REP_FILE);
            while(rset.next()) {
                int repId    = rset.getInt("rep_id");
                int placeId  = rset.getInt("owner_id");
                int parentId = rset.getInt("parent_id");
                int deleteId = rset.getInt("delete_id");

                if (parentId < 1  &&  deleteId <= 0) {
                    topLevel.put(repId, placeId);
                    topPlaceIds.add(placeId);
                }
            }
        }

        System.out.println("Top-Level count: " + topLevel.size());
        System.out.println("    Place count: " + topPlaceIds.size());
        System.out.println("    Place count: " + secondPlaceIds.size());
    }

    static void loadSecondLevelReps() throws SQLException {
        try(FileResultSet rset = new FileResultSet()) {
            rset.setSeparator("\\|");
            rset.openFile(REP_FILE);
            while(rset.next()) {
                int repId    = rset.getInt("rep_id");
                int placeId  = rset.getInt("owner_id");
                int parentId = rset.getInt("parent_id");
                int deleteId = rset.getInt("delete_id");

                if (parentId >= 1  &&  topLevel.containsKey(parentId)  &&  deleteId <= 0) {
                    secondLevel.put(repId, placeId);
                    secondPlaceIds.add(placeId);
                }
            }
        }

        System.out.println("2nd-Level count: " + secondLevel.size());
        System.out.println("    Place count: " + topPlaceIds.size());
        System.out.println("    Place count: " + secondPlaceIds.size());
    }

    static void loadVariantNames() throws SQLException {
        try(FileResultSet rset = new FileResultSet()) {
            rset.setSeparator("\\|");
            rset.openFile(VARIANT_FILE);
            while(rset.next()) {
                int placeId   = rset.getInt("place_id");
                int deleteId  = rset.getInt("delete_id");
                String text   = rset.getString("text");
                boolean isDel = rset.getBoolean("delete_flag");

                if ((topPlaceIds.contains(placeId) || secondPlaceIds.contains(placeId))  &&  deleteId <= 0  &&  ! isDel) {
                    String normText = PlaceHelper.normalize(text).toLowerCase();
                    if (topPlaceIds.contains(placeId)) {
                        topLevelNames.add(normText);
                    }

                    Set<String> varNameList = varNames.get(placeId);
                    if (varNameList == null) {
                        varNameList = new HashSet<>();
                        varNames.put(placeId, varNameList);
                    }
                    varNameList.add(normText);
                }
            }
        }
        System.out.println("Top1-Name count: " + topLevelNames.size());
        System.out.println("    VName count: " + varNames.size());
    }
    
    static void findCollisions() {
        Set<String> badNames = new TreeSet<>();

        for (Map.Entry<Integer, Integer> entry : secondLevel.entrySet()) {
            int repId   = entry.getKey();
            int placeId = entry.getValue();
            Set<String> varName = varNames.get(placeId);
            if (varName == null  ||  varName.isEmpty()) {
                System.out.println("No VAR names for place=" + placeId + " (rep=" + repId + ")");
            } else {
                String inBoth = varName.stream()
                    .filter(vName -> topLevelNames.contains(vName))
                    .findFirst().orElse(null);
                if (inBoth != null) {
                    badNames.add(inBoth);
                    System.out.println("BAD VAR names for place=" + placeId + " (rep=" + repId + ")" + " --> " + inBoth);
                    Set<int[]> matches = getTopLevelMatches(inBoth);
                    matches.forEach(match -> System.out.println("         found in place=" + match[0] + " (rep=" + match[1] + ")"));
                }
            }
        }

        System.out.println("\n\n");
        System.out.println("Bad-name count: " + badNames.size());
        badNames.forEach(System.out::println);
    }

    static Set<int[]> getTopLevelMatches(String inBoth) {
        Set<int[]> matches = new HashSet<>();

        Set<Integer> placeIds = topPlaceIds.stream()
            .filter(placeId -> varNames.getOrDefault(placeId, Collections.emptySet()).contains(inBoth))
            .collect(Collectors.toSet());
        for (int placeId : placeIds) {
            Set<Integer> repIds = topLevel.entrySet().stream()
                .filter(entry -> entry.getValue() == placeId)
                .map(entry -> entry.getKey())
                .collect(Collectors.toSet());
            repIds.forEach(repId -> matches.add(new int[] { placeId, repId } ));
        }

        return matches;
    }
}
