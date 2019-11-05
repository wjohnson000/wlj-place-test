package std.wlj.jdbc;

import java.sql.*;

import std.wlj.util.DbConnectionManager;

public class RawJDBCInsert {

    public static void main(String...args) throws SQLException {

        KeyValue kv = new KeyValue("K-wlj", "V-wlj");

        KeyValue kvA = new KeyValue("K-AAA", "V-aaa");
        KeyValue kvB = new KeyValue("K-BBB", "V-bbb");
        String[] kkvvSS = new String[] { kvA.toStringPG(), kvB.toStringPG() };

        String[] ttSS = new String[] { "abc=HOWDY", "def=DOODY", "ghi=BUCKAROO" };
        String[][] ttttSS = new String[][] { { "ichi", "one" }, { "ni", "two" }, { "san", "three" } };

        String query = "INSERT INTO TestKV(id, kv, kkvv, tt, tttt) VALUES(?, ?::keyvalue, ?, ?, ?)";

        try (Connection conn = DbConnectionManager.getDataSourceWLJ().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            Array kkvv = conn.createArrayOf("keyvalue", kkvvSS);
            Array tt   = conn.createArrayOf("text", ttSS);
            Array tttt = conn.createArrayOf("text", ttttSS);

            stmt.setString(1, "try-H");
            stmt.setString(2, kv.toStringPG());
            stmt.setArray(3, kkvv);
            stmt.setArray(4, tt);
            stmt.setArray(5, tttt);

            int rows = stmt.executeUpdate();
            System.out.println("Rows: " + rows);
        }

        System.exit(0);
    }
}
