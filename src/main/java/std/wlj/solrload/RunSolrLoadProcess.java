package std.wlj.solrload;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.solr.load.LoadSolrProcessor;


/**
 * 
 * @author wjohnson000
 *
 */
public class RunSolrLoadProcess {
    /** Command-line arguments */
    private static String dbDriver       = "org.postgresql.Driver";
    private static String dbHost         = "localhost";
    private static String dbUser         = "place20";
    private static String dbPassword     = "place20";
    private static String dbSchema       = "place20";
    private static String solrDirectory  = "C:/Users/wjohnson000/.places/tokoro-ni";
    private static String tempDirectory  = "C:/temp/place-extract/tokoro";


    public static void main(String... args) throws Exception {
        // Environment set-up
        System.setProperty("solr.master.url", "");
        System.setProperty("solr.solr.home", solrDirectory);
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");

        // Define the SOLR and Database connections
        SolrConnection solrConn = SolrConnection.connectToEmbeddedInstance(solrDirectory);
        Connection conn = getConnection();

        // Crank up the data load process
        long then = System.nanoTime();
        LoadSolrProcessor solrProcessor = new LoadSolrProcessor(conn, solrConn);
        solrProcessor.runLoad(tempDirectory, false, false, true);
        long nnow = System.nanoTime();

        // Go bye-bye ...
        solrConn.shutdown();
        conn.close();
        System.out.println("Total run time: " + (nnow - then) / 1000000);
        System.exit(0);
    }

    /**
     * Create a connection to the database, or NULL if the connection can't be made.
     * @return
     * @throws SQLException 
     */
    private static Connection getConnection() {
        try {
            Class.forName(dbDriver);
            String url = "jdbc:postgresql://" + dbHost + ":5432/" + dbSchema;
            System.out.println("URL: " + url);
            return DriverManager.getConnection(url, dbUser, dbPassword);
        } catch (ClassNotFoundException ex) {
            System.out.println("Driver class not found: " + ex);
            return null;
        } catch (SQLException ex) {
            System.out.println("Unable to make db connection: " + ex);
            return null;
        }
    }

}
