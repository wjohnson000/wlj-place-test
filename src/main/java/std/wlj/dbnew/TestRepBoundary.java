package std.wlj.dbnew;

import java.sql.*;

public class TestRepBoundary {

    private static final String SIMPLE_BOUNDARY =
        "<Polygon><outerBoundaryIs><LinearRing><coordinates>" +
        "60.803193,34.404102 60.52843,33.676446 60.9637,33.528832 60.536078,32.981269 60.863655,32.18292 " +
        "60.941945,31.548075 61.699314,31.379506 61.781221,30.73585 60.874248,29.829239 62.549857,29.318572 " +
        "63.550261,29.468331 64.148002,29.340819 64.350419,29.560031 65.046862,29.472181 66.346473,29.887943 " +
        "66.381457,30.738899 66.938891,31.304911 67.683393,31.303154 67.792689,31.58293 68.556932,31.71331 " +
        "68.926677,31.620189 69.317764,31.901412 69.262522,32.501944 69.687147,33.105499 70.323594,33.358532 " +
        "69.930543,34.02012 70.881803,33.988856 71.156773,34.348911 71.115019,34.733126 71.613076,35.153203 " +
        "71.498768,35.650563 71.262348,36.074388 71.846292,36.509942 72.920025,36.720007 74.067552,36.836176 " +
        "74.575893,37.020841 75.158028,37.133031 74.980002,37.41999 73.948696,37.421566 73.260056,37.495257 " +
        "72.63689,37.047558 72.193041,36.948288 71.844638,36.738171 71.448693,37.065645 71.541918,37.905774 " +
        "71.239404,37.953265 71.348131,38.258905 70.806821,38.486282 70.376304,38.138396 70.270574,37.735165 " + 
        "70.116578,37.588223 69.518785,37.608997 69.196273,37.151143 68.859446,37.344336 68.135562,37.023115 " + 
        "67.829999,37.144994 67.075782,37.356144 66.518607,37.362784 66.217385,37.39379 65.745631,37.661164 " +
        "65.588948,37.305217 64.746105,37.111818 64.546479,36.312073 63.982896,36.007957 63.193538,35.857166 " +
        "62.984662,35.404041 62.230651,35.270664 61.210817,35.650072 60.803193,34.404102" +
        "</coordinates></LinearRing></outerBoundaryIs></Polygon>";


    public static void main(String... args) throws Exception {
        Connection conn = DbBase.getConn();

        // These inserts should work ...
        int transxId = DbBase.getTranxId(conn);
        System.out.println("Update OK? " + insertBndyDetail(conn, 9, 1060, 68));
        System.out.println("Update OK? " + insertBndyDetail(conn, 9, 1061, 68));
        System.out.println("Update OK? " + insertBndyDetail(conn, 9, 1062, 68));

        transxId = DbBase.getTranxId(conn);
        System.out.println("TransxId: " + transxId);
        System.out.println("Update OK? " + insertBndyDetail(conn, 9, transxId, 1060, 68));

        // These inserts should fail!!
        transxId = DbBase.getTranxId(conn);
        System.out.println("Update OK? " + insertBndyDetail(conn, 11, transxId, 1060, 68));
        System.out.println("Update OK? " + insertBndyDetail(conn, 9, 11111, 1060, 68));
        System.out.println("Update OK? " + insertBndyDetail(conn, 9, 1, 1060, 68));

        conn.close();
    }

    private static boolean insertBndyDetail(Connection conn, int bndryId, int typeId, int pointCnt) throws Exception {
        String query = "INSERT INTO sams_place.boundary_detail(boundary_id, type_id, point_count, boundary_data) VALUES(?, ?, ?, sams_place.ST_GeomFromKML(?))";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bndryId);
            stmt.setInt(2, typeId);
            stmt.setInt(3, pointCnt);
            stmt.setString(4, SIMPLE_BOUNDARY);
            int cnt = stmt.executeUpdate();
            return (cnt == 1);
        } catch(SQLException ex) {
            System.out.println("OOPS: " + ex.getErrorCode() + "; " + ex.getMessage());
            return false;
        }
    }

    private static boolean insertBndyDetail(Connection conn, int bndryId, int transxId, int typeId, int pointCnt) throws Exception {
        String query = "INSERT INTO sams_place.boundary_detail(boundary_id, tran_id, type_id, point_count, boundary_data) VALUES(?, ?, ?, ?, sams_place.ST_GeomFromKML(?))";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, bndryId);
            stmt.setInt(2, transxId);
            stmt.setInt(3, typeId);
            stmt.setInt(4, pointCnt);
            stmt.setString(5, SIMPLE_BOUNDARY);
            int cnt = stmt.executeUpdate();
            return (cnt == 1);
        } catch(SQLException ex) {
            System.out.println("OOPS: " + ex.getErrorCode() + "; " + ex.getMessage());
            return false;
        }
    }
}
