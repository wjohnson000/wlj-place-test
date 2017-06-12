package std.wlj.datasource;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class PGConnectionTest {
    public static void main(String... args) throws SQLException {
        Connection conn = DbConnectionManager.getConnectionAwsDev55();
        System.out.println("CONN: " + conn);
        System.out.println("CONN: " + conn.getClass().getName());

        System.out.println("catalog." + conn.getCatalog());
        Properties props = conn.getClientInfo();
        for (String prop : props.stringPropertyNames()) {
            System.out.println("Prop." + prop + " --> " +  props.getProperty(prop));
        }

        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet rset = dbmd.getTables(null, null, "%", new String[] { "TABLE" });
        while (rset.next()) {
            System.out.println("TABLE: " + rset.getString(2) + " --> " + rset.getString(3));
        }
        conn.close();
    }
}
