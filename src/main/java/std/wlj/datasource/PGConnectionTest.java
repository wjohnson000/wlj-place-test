package std.wlj.datasource;

import java.sql.Connection;
import java.sql.SQLException;

public class PGConnectionTest {
    public static void main(String... args) throws SQLException {
        Connection conn = DbConnectionManager.getConnectionAwsDev55();
        System.out.println("CONN: " + conn);
        conn.close();
    }
}
