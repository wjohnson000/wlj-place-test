package std.wlj.dbnew;

import java.sql.*;
import java.sql.DriverManager;

public abstract class DbBase {

    private static final String jdbcDriver = "org.postgresql.Driver";
    private static final String jdbcURL    = "jdbc:postgresql://localhost:5432/standards";
    private static final String jdbcUser   = "sams_place";
    private static final String jdbcPswd   = "sams_place";


    /**
     * Create and return a database connection ...
     * @return
     */
    public static Connection getConn() {
        try {
            Class.forName(jdbcDriver);
            return DriverManager.getConnection(jdbcURL, jdbcUser, jdbcPswd);
        } catch(Exception ex) {
            System.out.println("Unable to get connection ... " + ex.getMessage());
        }

        return null;
    }

    public static int getTranxId(Connection conn) {
        try(Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO sams_place.transaction DEFAULT VALUES");

            ResultSet rset = stmt.executeQuery("SELECT MAX(tran_id) FROM sams_place.transaction");
            if (rset.next()) {
                return rset.getInt(1);
            }
        } catch(SQLException ex) { 
            System.out.println("Unable to create a transaction ... ");
        }

        return -1;
    }
}
