package std.wlj.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.familysearch.standards.core.logging.Logger;

public class ExecuteQuery {

    /** Logger ... duh !?! */
    private static Logger logger = new Logger(ExecuteQuery.class);

    /** Command-line arguments */

    public static void main(String[] args) throws SQLException, FileNotFoundException, IOException {
    	Connection conn = DbConnectionManager.getConnectionSams();

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
}
