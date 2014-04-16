package std.wlj.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.familysearch.standards.core.logging.Logger;

public class ExecuteQuery {

    /** Logger ... duh !?! */
    private static Logger logger = new Logger(ExecuteQuery.class);

    /** Command-line arguments */
    private static String dbDriver         = "org.postgresql.Driver";
    private static String dbHost           = "std-cv-devdb.a.fsglobal.net";
    private static String dbUser           = "sams_place";
    private static String dbPassword       = "sams_place";
    private static String dbSchema         = "p124";

    private static String dbUrl            = null;  //"jdbc:postgresql://fh2-std-place-db-int.cqbtyzjgnvqo.us-east-1.rds.amazonaws.com:5432/p124";

    public static void main(String[] args) throws SQLException, FileNotFoundException, IOException {
    	Connection conn = getConnection();

    	DbUtil.setConnection(conn);

  	    String query = "SELECT rep_id, tran_id, parent_id, owner_id FROM place_rep ORDER BY rep_id";

//    	long nnow = System.currentTimeMillis();
//    	long recCnt = DbUtil.executeQuery(query, new File("C:/temp/rep-data-01.sql"), '|');
//    	System.out.println("Rep-Count: " + recCnt + " --> Time: " + (System.currentTimeMillis() - nnow));

    	long nnow = System.currentTimeMillis();
        long recCnt = DbUtil.executeQuery(query, new File("C:/temp/rep-data-02.sql"), '|');
        System.out.println("Rep-Count: " + recCnt + " --> Time: " + (System.currentTimeMillis() - nnow));

    	conn.close();
    }

    /**
     * Create a connection to the database, or NULL if the connection can't be made.
     * @return
     * @throws SQLException 
     */
    private static Connection getConnection() {
        try {
            Class.forName(dbDriver);
            String url = (dbUrl==null || dbUrl.trim().length() == 0) ? ("jdbc:postgresql://" + dbHost + ":5432/" + dbSchema) : dbUrl;
            logger.info("URL: " + url);
            return DriverManager.getConnection(url, dbUser, dbPassword);
        } catch (ClassNotFoundException ex) {
            logger.error("Driver class not found: " + ex);
            return null;
        } catch (SQLException ex) {
            logger.error("Unable to make db connection: " + ex);
            return null;
        }
    }

}
