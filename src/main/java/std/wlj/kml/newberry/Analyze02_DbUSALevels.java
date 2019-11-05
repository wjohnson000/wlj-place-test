package std.wlj.kml.newberry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import std.wlj.util.DbConnectionManager;

/**
 * Pull place-rep data for US from an AWS database, saving the following fields:
 * <ul>
 *   <li>hierarchy level (0=root place-rep)</li>
 *   <li>rep-id</li>
 *   <li>Display name (en)</li>
 *   <li>Place type</li>
 *   <li>Start year</li>
 *   <li>End year</li>
 *   <li>latitude</li>
 *   <li>longitude</li>
 * </ul>
 * 
 * @author wjohnson000
 *
 */
public class Analyze02_DbUSALevels {

    static String baseDir = "D:/postgis/newberry";
    static String outputFileName = "bndy-02-rep.txt";

    static class Type02 {
        int    typeId;
        String code;
        String term;
    }

    static class PlaceRep02 {
        int    repId;
        int    parentId;
        int    typeId;
        int    fromYear;
        int    toYear;
        int    deleteId;
        double centerLong;
        double centerLattd;
        String name;

        @Override public String toString() {
            return name;
        }
    }

    static Map<Integer, Type02> typeMap = new HashMap<>();
    static Map<Integer, PlaceRep02> repMap = new HashMap<>();

    public static void main(String...args) throws IOException {
        try(Connection conn=DbConnectionManager.getConnectionAws()) {
            populateTypes(conn);

            getReps(conn, "rep_id", Arrays.asList(1));
            getReps(conn, "parent_id", Arrays.asList(1));

            List<Integer> parentIds = repMap.values().stream()
                    .map(rep -> rep.repId)
                    .collect(Collectors.toList());
            getReps(conn, "parent_id", parentIds);

            List<Integer> deleteIds = repMap.values().stream()
                    .filter(rep -> rep.deleteId > 1)
                    .map(rep -> rep.deleteId)
                    .collect(Collectors.toList());
            getReps(conn, "rep_id", deleteIds);

        } catch (SQLException ex) {
            System.out.println("SQL-EX: " + ex);
        }

        List<String> treeData = dumpTree();
        treeData.add(0, "");
        Files.write(Paths.get(baseDir, outputFileName), treeData, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static List<String> dumpTree() {
        return dumpTree(0, 1);
    }

    static List<String> dumpTree(int level, int repId) {
        List<String> results = new ArrayList<>();

        PlaceRep02 repX = repMap.get(repId);
        if (repX == null) {
            System.out.println("OUCH!! " + repId);
            return results;
        }

        Type02 typeX = typeMap.get(repX.typeId);

        StringBuilder buff = new StringBuilder();
        buff.append(level);
        buff.append("|").append(repX.repId);
        buff.append("|").append(repX.name);
        buff.append("|").append(typeX.term);
        buff.append("|").append(repX.fromYear);
        buff.append("|").append(repX.toYear);
        buff.append("|").append(repX.centerLattd);
        buff.append("|").append(repX.centerLong);

        results.add(buff.toString());

        // Process the kidlets
        List<PlaceRep02> kids = repMap.values().stream()
            .filter(rep -> rep.parentId == repId)
            .collect(Collectors.toList());
        kids.sort(Comparator.comparing(PlaceRep02::toString));
        kids.forEach(kid -> results.addAll(dumpTree(level+1, kid.repId)));

        return results;
    }

    static void populateTypes(Connection conn) throws SQLException {
        String query =
                "SELECT typ.type_id, typ.code, trm.text " +
                        "  FROM type AS typ " +
                        "  JOIN type_term AS trm ON trm.type_id = typ.type_id " +
                        " WHERE trm.locale = 'en'";

        try (Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(query)) {
            while (rset.next()) {
                Type02 type = new Type02();
                type.typeId = rset.getInt("type_id");
                type.code   = rset.getString("code");
                type.term   = rset.getString("text");
                typeMap.put(type.typeId, type);
            }
        }
    }

    static void getReps(Connection conn, String field, List<Integer> repIds) throws SQLException {
        if (repIds.isEmpty()) {
            System.out.println("NO ids to process ... " + field);
            return;
        }

        String baseQuery =
                "SELECT rep.rep_id, rep.tran_id, rep.parent_id, rep.place_type_id, rep.parent_from_year, " +
                        "       parent_to_year, rep.delete_id, rep.centroid_lattd, rep.centroid_long, nam.text " +
                        "  FROM place_rep AS rep " +
                        "  JOIN rep_display_name AS nam ON nam.rep_id = rep.rep_id " +
                        " WHERE nam.locale = 'en' " +
                        "   AND rep.|field| IN |id_list| " +
                        " ORDER BY rep.tran_id DESC";

        String idList = repIds.stream()
                .map(id -> String.valueOf(id))
                .collect(Collectors.joining(", ", "(", ")"));
        String query = baseQuery.replaceAll("\\|field\\|", field).replaceAll("\\|id_list\\|", idList);

        try (Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(query)) {
            while (rset.next()) {
                PlaceRep02 rep = new PlaceRep02();
                rep.repId = rset.getInt("rep_id");
                if (! repMap.containsKey(rep.repId)) {
                    rep.parentId = rset.getInt("parent_id");
                    rep.typeId = rset.getInt("place_type_id");
                    rep.fromYear = rset.getInt("parent_from_year");
                    rep.toYear = rset.getInt("parent_to_year");
                    rep.centerLattd = rset.getDouble("centroid_lattd");
                    rep.centerLong = rset.getDouble("centroid_long");
                    rep.name = rset.getString("text");
                    repMap.put(rep.repId, rep);
                }
            }
        }
    }
}
