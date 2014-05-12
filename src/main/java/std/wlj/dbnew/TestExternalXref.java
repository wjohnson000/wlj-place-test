package std.wlj.dbnew;

import java.sql.*;

public class TestExternalXref {

    public static void main(String... args) throws Exception {
        Connection conn = DbBase.getConn();

        insertXref(conn, 1, 1063, "123-456-ABC");
        insertXref(conn, 1, 1063, "123-456-DEF");
        insertXref(conn, 111, 1063, "123-456-DEF");

        conn.close();
    }

    private static boolean insertXref(Connection conn, int repId, int typeId, String extKey) throws Exception {
        String query = "INSERT INTO sams_place.external_xref(rep_id, type_id, external_key, pub_flag) VALUES(?, ?, ?, TRUE)";

        try (PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, repId);
            stmt.setInt(2, typeId);
            stmt.setString(3, extKey);
            int cnt = stmt.executeUpdate();
            return (cnt == 1);
        } catch(SQLException ex) {
            System.out.println("OOPS: " + ex.getErrorCode() + "; " + ex.getMessage());
        }

        return false;
    }
}
