package std.wlj.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.postgresql.util.PGobject;

import std.wlj.datasource.DbConnectionManager;

public class RawJDBCSelect {

    public static void main(String...args) throws SQLException {
        try (Connection conn = DbConnectionManager.getDataSourceWLJ().getConnection();
             Statement  stmt = conn.createStatement()) {

            ResultSet rset = stmt.executeQuery("SELECT * FROM TestKV");
            ResultSetMetaData rsmd = rset.getMetaData();

            for (int i=1;  i<=rsmd.getColumnCount();  i++) {
                System.out.println("Column " + i + ": " + rsmd.getColumnName(i));
                System.out.println("    type: " + rsmd.getColumnType(i) + " -> " + rsmd.getColumnTypeName(i));
                System.out.println("    clss: " + rsmd.getColumnClassName(i));
            }

            while (rset.next()) {
                String id   = rset.getString("id");
                Object kv   = rset.getObject("kv");
                Array  kkvv = rset.getArray("kkvv");
                Array  tt   = rset.getArray("tt");
                Array  tttt = rset.getArray("tttt");

                System.out.println("\n  ID: " + id);
                System.out.println("  kv: " + ripApart((PGobject)kv));
                System.out.println("kkvv: " + ((kkvv == null) ? "<null>" : Arrays.deepToString(ripApart(kkvv))));
                System.out.println("  tt: " + ((tt == null) ? "<null>" : Arrays.toString(getStrings(tt))));
                System.out.println("tttt: " + ((tttt == null) ? "<null>" : Arrays.deepToString(getStringsStrings(tttt))));
            }
        }

        System.exit(0);
    }

    static KeyValueFancy ripApart(PGobject what) throws SQLException {
        KeyValueFancy kv = new KeyValueFancy();
        kv.setValue(what.getValue());
        return kv;
    }

    static KeyValueFancy[] ripApart(Array kvArray) throws SQLException {
        ResultSet rset = kvArray.getResultSet();

        List<KeyValueFancy> kvList = new ArrayList<>();
        while (rset.next()) {
            Object what = rset.getObject("VALUE");
            kvList.add(ripApart((PGobject)what));
        }

        return kvList.toArray(new KeyValueFancy[kvList.size()]);
    }

    static String[] getStrings(Array stringArray) throws SQLException {
        return (String[])stringArray.getArray();
    }

    static String[][] getStringsStrings(Array stringStringArray) throws SQLException {
        return (String[][])stringStringArray.getArray();
    }
}
