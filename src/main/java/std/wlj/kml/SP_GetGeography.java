package std.wlj.kml;

import java.sql.*;

import std.wlj.util.DbConnectionManager;

public class SP_GetGeography {

    static final String boundarySQL =
        "SELECT rb.*, ST_AsKML(rb.boundary_data) AS geography_data FROM rep_boundary AS rb WHERE boundary_id = ";

    public static void main(String... args) {
        try(Connection conn = DbConnectionManager.getConnectionSams()) {
            processBoundary(conn, 1);
            processBoundary(conn, 11);
            processBoundary(conn, 111);
        } catch(SQLException ex) {
            System.out.println("Main@SQL-EX: " + ex.getMessage());
        }
    }
    
    private static void processBoundary(Connection conn, int bdryId) {
        try(Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(boundarySQL + bdryId)) {
            if (rset.next()) {
                System.out.println("\n\n" + rset.getInt("boundary_id") + " --> " + rset.getInt("rep_id"));
                Object whatO = rset.getObject("boundary_data");
                System.out.println("What? " + whatO.getClass().getName());
                String whatS = rset.getString("geography_data");
                System.out.println("Geog:\n" + whatS);
            }
        } catch(SQLException ex) {
            System.out.println("ProcessBdy@SQL-EX: " + ex.getMessage());
        }
    }
}
