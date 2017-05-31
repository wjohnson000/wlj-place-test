package std.wlj.kml;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import std.wlj.datasource.DbConnectionManager;

public class SP_Simple {

    static final String boundarySQL =
        "SELECT boundary_id, ST_Area(boundary_data), ST_Length(boundary_data), " +
        "                    ST_Perimeter(boundary_data), ST_Area(boundary_datax), " +
        "                    ST_Length(boundary_datax), ST_Perimeter(boundary_datax) " +
        "  FROM rep_boundary WHERE boundary_id = ";

    public static void main(String... args) {
        try(Connection conn = DbConnectionManager.getConnectionSams()) {
            Map<Integer,String> boundaryData = getBoundaryData(conn);
            boundaryData.entrySet().forEach(entry -> processBoundary(conn, entry.getKey(), entry.getValue()));
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
    
    private static void processBoundary(Connection conn, Integer bdryId, String value) {
        System.out.println("\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(bdryId + " --> " + value);

        try(Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(boundarySQL + bdryId)) {
            if (rset.next()) {
                System.out.println("  Boundry-ID: " + bdryId);
                System.out.println("     Area: " + rset.getDouble(2));
                System.out.println("   Length: " + rset.getDouble(3));
                System.out.println("    Perim: " + rset.getDouble(4));
                System.out.println("    XArea: " + rset.getDouble(5));
                System.out.println("  XLength: " + rset.getDouble(6));
                System.out.println("   XPerim: " + rset.getDouble(7));
            }
                    
        } catch(SQLException ex) {
            System.out.println("ProcessBdy@SQL-EX: " + ex.getMessage());
        }
    }
}
