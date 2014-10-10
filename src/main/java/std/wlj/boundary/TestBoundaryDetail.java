package std.wlj.boundary;

import java.sql.*;

import std.wlj.dbnew.DbBase;

public class TestBoundaryDetail {

    public static void main(String... args) throws Exception {
        Connection conn = DbBase.getConn();

        // These inserts should work ...
        int transxId = DbBase.getTranxId(conn);
        System.out.println("Update OK? " + insertRepBoundary(conn, 9, 11));

        transxId = DbBase.getTranxId(conn);
        System.out.println("TransxId: " + transxId);
        System.out.println("Update OK? " + insertRepBoundary(conn, 9, 11, transxId));

        // These inserts should fail!!
        System.out.println("Update OK? " + insertRepBoundary(conn, 999, 11, transxId));
        System.out.println("Update OK? " + insertRepBoundary(conn, 9, 1111, transxId));
        System.out.println("Update OK? " + insertRepBoundary(conn, 9, 11, 11111));

        conn.close();
    }

    private static boolean insertRepBoundary(Connection conn, int bndryId, int repId) throws Exception {
        String query = "INSERT INTO sams_place.rep_boundary(boundary_id, rep_id) VALUES(?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bndryId);
            stmt.setInt(2, repId);
            int cnt = stmt.executeUpdate();
            return (cnt == 1);
        } catch(SQLException ex) {
            System.out.println("OOPS: " + ex.getErrorCode() + "; " + ex.getMessage());
            return false;
        }
    }

    private static boolean insertRepBoundary(Connection conn, int bndryId, int repId, int transxId) throws Exception {
        String query = "INSERT INTO sams_place.rep_boundary(boundary_id, rep_id, tran_id) VALUES(?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bndryId);
            stmt.setInt(2, repId);
            stmt.setInt(3, transxId);
            int cnt = stmt.executeUpdate();
            return (cnt == 1);
        } catch(SQLException ex) {
            System.out.println("OOPS: " + ex.getErrorCode() + "; " + ex.getMessage());
            return false;
        }
    }
}
