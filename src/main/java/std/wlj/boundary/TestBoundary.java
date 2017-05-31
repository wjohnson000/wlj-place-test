package std.wlj.boundary;

import java.sql.*;

import std.wlj.datasource.DbConnectionManager;
import std.wlj.util.DbUtil;

public class TestBoundary {

    public static void main(String... args) throws Exception {
        Connection conn = DbConnectionManager.getConnectionStds();

        int bndryId = insertBoundary(conn, "Silly Boundary", "1900", null);
        System.out.println("BndryId: " + bndryId);

        // These updates should work ...
        DbUtil.getTranxId(conn);
        System.out.println("Update OK? " + updateBoundary(conn, bndryId, "Silly Boundary update 01", "1901", "2020"));
        int transxId = DbUtil.getTranxId(conn);
        System.out.println("TransxId: " + transxId);
        System.out.println("Update OK? " + updateBoundary(conn, bndryId, transxId, "Silly Boundary update 02", "1901", "2020"));

        // These updates should fail!!
        System.out.println("Update OK? " + updateBoundary(conn, 11111, "Silly Boundary update 03x", "1901", "2020"));
        System.out.println("Update OK? " + updateBoundary(conn, bndryId, 11111, "Silly Boundary update 04x", "1901", "2020"));
        System.out.println("Update OK? " + updateBoundary(conn, bndryId, 1, "Silly Boundary update 05x", "1901", "2020"));
        
        conn.close();
    }

    private static int insertBoundary(Connection conn, String descr, String fromStr, String toStr) throws Exception {
        String query = "INSERT INTO sams_place.boundary(description, from_date, to_date, delete_flag) VALUES(?, ?, ?, FALSE)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, descr);
            stmt.setString(2, fromStr);
            stmt.setString(3, toStr);
            stmt.executeUpdate();
        } catch(SQLException ex) {
            System.out.println("OOPS: " + ex.getErrorCode() + "; " + ex.getMessage());
            return -1;
        }

        try (Statement stmt = conn.createStatement()) {
            ResultSet rset = stmt.executeQuery("SELECT MAX(boundary_id) FROM sams_place.boundary");
            if (rset.next()) {
                return rset.getInt(1);
            }
        } catch(SQLException ex) { }

        return -1;
    }

    private static boolean updateBoundary(Connection conn, int bndryId, String descr, String fromStr, String toStr) throws Exception {
        String query = "INSERT INTO sams_place.boundary(boundary_id, description, from_date, to_date, delete_flag) VALUES(?, ?, ?, ?, FALSE)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bndryId);
            stmt.setString(2, descr);
            stmt.setString(3, fromStr);
            stmt.setString(4, toStr);
            int cnt = stmt.executeUpdate();
            return (cnt == 1);
        } catch(SQLException ex) {
            System.out.println("OOPS: " + ex.getErrorCode() + "; " + ex.getMessage());
            return false;
        }
    }

    private static boolean updateBoundary(Connection conn, int bndryId, int transxId, String descr, String fromStr, String toStr) throws Exception {
        String query = "INSERT INTO sams_place.boundary(boundary_id, tran_id, description, from_date, to_date, delete_flag) VALUES(?, ?, ?, ?, ?, FALSE)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bndryId);
            stmt.setInt(2, transxId);
            stmt.setString(3, descr);
            stmt.setString(4, fromStr);
            stmt.setString(5, toStr);
            int cnt = stmt.executeUpdate();
            return (cnt == 1);
        } catch(SQLException ex) {
            System.out.println("OOPS: " + ex.getErrorCode() + "; " + ex.getMessage());
            return false;
        }
    }
}
