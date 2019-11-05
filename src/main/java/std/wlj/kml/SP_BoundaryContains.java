package std.wlj.kml;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import std.wlj.util.DbConnectionManager;

public class SP_BoundaryContains {

    public static void main(String... args) {
        try(Connection conn = DbConnectionManager.getConnectionSams()) {
            Map<Integer,String> boundaryData = getBoundaryData(conn);
            processBoundary(conn, boundaryData, "us", "state-500k");
            processBoundary(conn, boundaryData, "us", "state-5m");
            processBoundary(conn, boundaryData, "state-500k-utah", "county-ut");
            processBoundary(conn, boundaryData, "state-5m-utah", "county-ut");
            processBoundary(conn, boundaryData, "state-500k-indiana", "county-in");
            processBoundary(conn, boundaryData, "state-5m-indiana", "county-in");
            processBoundary(conn, boundaryData, "county-UT_Beaver", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Box Elder", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Cache", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Carbon", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Daggett", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Davis", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Duchesne", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Emery", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Garfield", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Grand", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Iron", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Juab", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Kane", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Millard", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Morgan", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Piute", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Rich", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Salt Lake", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_San Juan", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Sanpete", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Sevier", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Summit", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Tooele", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Uintah", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Utah", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Wasatch", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Washington", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Wayne", "county-ut");
            processBoundary(conn, boundaryData, "county-UT_Weber", "county-ut");
        } catch(SQLException ex) {
            System.out.println("Main@SQL-EX: " + ex.getMessage());
        }
    }

    private static Map<Integer, String> getBoundaryData(Connection conn) {
        Map<Integer, String> results = new HashMap<>();

        try(Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery("SELECT boundary_id, name FROM rep_boundary")) {
            while (rset.next()) {
                results.put(rset.getInt("boundary_id"), rset.getString("name"));
            }
        } catch(SQLException ex) {
            System.out.println("GetBdyData@SQL-EX: " + ex.getMessage());
        }

        return results;
    }

    private static void processBoundary(Connection conn, Map<Integer,String> boundaryData, String outer, String inner) {
        Map.Entry<Integer,String> outerEntry = boundaryData.entrySet()
                .stream()
                .filter(ee -> ee.getValue().toLowerCase().startsWith(outer.toLowerCase()))
                .findFirst().orElse(null);

        List<Map.Entry<Integer,String>> innerEntries = boundaryData.entrySet()
                .stream()
                .filter(ee -> ee.getValue().toLowerCase().startsWith(inner.toLowerCase()))
                .collect(Collectors.toList());

        System.out.println("==============================================================================================");
        System.out.println(PGUtility.readModel(conn, outerEntry.getKey()));
        innerEntries.forEach(
           entry -> {
               System.out.println("  " + PGUtility.readModel(conn, entry.getKey()) + " --> " + PGUtility.isContained(conn, outerEntry.getKey(), entry.getKey()));
           });
        System.out.println();
    }
}
