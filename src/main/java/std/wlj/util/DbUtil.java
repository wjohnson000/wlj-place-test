package std.wlj.util;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.familysearch.standards.core.logging.Logger;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;


/**
 * Base class that has convenience methods for dealing with result-sets and
 * "PlaceRepDoc" mapping. 
 * @author wjohnson000
 *
 */
public class DbUtil {

    /** Logger ... duh !?! */
    private static Logger logger = new Logger(DbUtil.class);

    /** Db connection */
    private static Connection conn = null;

    /** Map of place-rep ID chain, both forwards and reverse */
    private static final Map<Integer,String> chainMap = new TreeMap<Integer,String>();
    private static final Map<Integer,String> reverseChainMap = new TreeMap<Integer,String>();


    /**
     * Set the connection for doing all database queries
     * @param aConn db connection
     */
    public static void setConnection(Connection aConn) {
        conn = aConn;
    }

    /**
     * Get the next available transaction
     * 
     * @param conn db connection
     * @return
     */
    public static int getTranxId(Connection conn) {
        try(Statement stmt = conn.createStatement()) {
            stmt.execute("INSERT INTO transaction DEFAULT VALUES");

            ResultSet rset = stmt.executeQuery("SELECT MAX(tran_id) FROM transaction");
            if (rset.next()) {
                return rset.getInt(1);
            }
        } catch(SQLException ex) { 
            System.out.println("Unable to create a transaction ... ");
        }

        return -1;
    }

    /**
     * Seed the place-chain data with all chains involving every place-rep that
     * is a parent to at least one other place-rep
     * @throws Exception
     */
    public static void seedPlaceChain() {
        logger.info("Starting the seed of the place-rep ID chains ...");

        // Save all child-parent associations where the child is also a parent 
        Map<Integer,Integer> childParentMap = new HashMap<Integer,Integer>();
        String query =
            "SELECT pr01.rep_id, pr01.parent_id " +
            "  FROM place_rep AS pr01 " +
            " WHERE EXISTS (SELECT 1 FROM place_rep AS pr02 WHERE pr02.parent_id = pr01.rep_id)";

        Statement stmt = null;
        ResultSet rset = null;

        try {
            stmt = conn.createStatement();
            rset = stmt.executeQuery(query);
            while (rset.next()) {
                int repId = rset.getInt("rep_id");
                int parId = rset.getInt("parent_id");
                childParentMap.put(repId, parId);
            }
            logger.info("Chain data retrieved ... start generation of chains");

            // Create chains for all parents
            for (Integer childId : childParentMap.keySet()) {
                String chain = "" + childId;
                String revChain = "" + childId;
                int parId = childId;
                while (childParentMap.containsKey(parId)) {
                    parId = childParentMap.get(parId);
                    if (parId > 0) {
                        chain = chain + "," + parId;
                        revChain = parId + "," + revChain;
                    } else {
                        break;
                    }
                }
                chainMap.put(childId, chain);
                reverseChainMap.put(childId, revChain);
            }
        } catch(SQLException ex) {
            logger.warn("Unable to generate chains: " + ex.getMessage());
        } finally {
            if (stmt  != null) try { stmt.close(); } catch(Exception ex) { }
            if (rset  != null) try { rset.close(); } catch(Exception ex) { }
        }

        logger.info("Place-Rep ID chain generation complete");
    }

    /**
     * Return the chain for a parent rep-id
     * @param parentId parent rep-id
     * @return chain
     */
    public static String getChain(int parentId) {
        return chainMap.get(parentId);
    }

    /**
     * Return the reverse chain for a parent rep-id
     * @param parentId parent rep-id
     * @return chain
     */
    public static String getReverseChain(int parentId) {
        return reverseChainMap.get(parentId);
    }

    /**
     * Return an iterator for the forward chain
     * @return entry set iterator
     */
    public static Iterator<Map.Entry<Integer,String>>  getChainIterator() {
        return chainMap.entrySet().iterator();
    }

    /**
     * Return an iterator for the forward chain
     * @return entry set iterator
     */
    public static Iterator<Map.Entry<Integer,String>>  getReverseChainIterator() {
        return reverseChainMap.entrySet().iterator();
    }

    /**
     * Dump the entire contents of DB table to the target location.  The first record
     * will contain a list of column names only.
     * 
     * @param tableName name of table to be dumped
     * @param targetLocation file where data is to be written
     * @param delimiter field delimiter character
     * 
     * @return number of rows in output file, or -1 if an error occurred
     */
    public static long dumpTable(String tableName, File targetLocation, char delimiter) {
        long rowCnt = -1;
        PrintWriter pwOut = null;

        try {
            pwOut = new PrintWriter(targetLocation, "UTF-8");
            CopyManager mgr = new CopyManager((BaseConnection)conn);

            rowCnt = mgr.copyOut("COPY " + tableName + " TO STDOUT WITH csv HEADER DELIMITER '|'", pwOut);
        } catch(IOException ex) {
            logger.warn("Unable to dump table '" + tableName + "' -- " + ex.getMessage(), ex);
        } catch (SQLException ex) {
            logger.warn("Unable to dump table '" + tableName + "' -- " + ex.getMessage(), ex);
        } finally {
            if (pwOut != null) try { pwOut.close(); } catch(Exception ex) { }
        }

        return rowCnt;
    }

    /**
     * Dump the results of a query to the target location.  The first record will be an
     * extended header containing column names and definitions.  The given delimiter
     * will be used as a field separator.
     * 
     * @param query SQL query to execute
     * @param targetLocation file where data is to be written
     * @param delimiter field delimiter character
     * 
     * @return number of rows in output file, or -1 if an error occurred
     */
    public static long executeQuery(String query, File targetLocation, char delimiter) {
        long rowCnt = -1;
        PrintWriter pwOut = null;

        try {
            pwOut = new PrintWriter(targetLocation, "UTF-8");
            pwOut.println(getHeader(query, delimiter));
            CopyManager mgr = new CopyManager((BaseConnection)conn);

            rowCnt = mgr.copyOut("COPY (" + query + ") TO STDOUT WITH csv DELIMITER '|'", pwOut);
        } catch(IOException ex) {
            logger.warn("Unable to execute query '" + query + "' -- " + ex.getMessage(), ex);
        } catch (SQLException ex) {
            logger.warn("Unable to execute query '" + query + "' -- " + ex.getMessage(), ex);
        } finally {
            if (pwOut != null) try { pwOut.close(); } catch(Exception ex) { }
        }

        return rowCnt;
    }

    /**
     * Execute a given query, saving the results in a specified location.  The first
     * row will be a header describing the columns.  Subsequent rows will contain
     * all of the query data.  The given delimiter will be used as a field separator.
     * Note: The {@link DbUtil.executeQueryAsCopy} method should do the same thing
     * as this method, but is likely quicker.
     * 
     * @param query SQL query to execute
     * @param targetLocation file where results will be saved
     * @param delimiter field delimiter
     * @return number of rows from query
     * @deprecated
     */
    public static int executeQueryAsSelect(String query, File targetLocation, char delimiter) {
        int recCnt = 0;
        Statement stmt = null;
        ResultSet rset = null;
        PrintWriter pwOut = null;

        try {
            pwOut = new PrintWriter(targetLocation, "UTF-8");

            // Connection must NOT be in "auto-commit" mode
            boolean toggleAC = false;
            if (conn.getAutoCommit()) {
                toggleAC = true;
                conn.setAutoCommit(false);
            }

            // Fetch up to 5000 rows at a time
            stmt = conn.createStatement();
            stmt.setFetchSize(5000);
            rset = stmt.executeQuery(query);
            ResultSetMetaData rsmd = rset.getMetaData();
            pwOut.println(getHeader(rsmd, delimiter));

            // Extract the data types of the various fields
            int[] type = new int[rsmd.getColumnCount()];
            for (int i=0;  i<rsmd.getColumnCount();  i++) {
                type[i] = rsmd.getColumnType(i+1);
            }

            StringBuilder buff = null;
            while (rset.next()) {
                recCnt++;
                buff = new StringBuilder(512);
                for (int i=0;  i<type.length;  i++) {
                    if (i > 0) {
                        buff.append(delimiter);
                    }

                    switch(type[i]) {
                    case Types.BIT:
                    case Types.BOOLEAN:
                        boolean bval = rset.getBoolean(i+1);
                        if (! rset.wasNull()) {
                            buff.append(bval);
                        }
                        break;

                    case Types.TINYINT:
                    case Types.SMALLINT:
                    case Types.INTEGER:
                        int ival = rset.getInt(i+1);
                        if (! rset.wasNull()) {
                            buff.append(ival);
                        }
                        break;

                    case Types.NUMERIC:
                    case Types.BIGINT:
                    case Types.DECIMAL:
                        long lval = rset.getLong(i+1);
                        if (! rset.wasNull()) {
                            buff.append(lval);
                        }
                        break;

                    case Types.FLOAT:
                    case Types.DOUBLE:
                        double dval = rset.getDouble(i+1);
                        if (! rset.wasNull()) {
                            buff.append(dval);
                        }
                        break;

                    case Types.CHAR:
                    case Types.VARCHAR:
                        String sval = rset.getString(i+1);
                        if (sval != null) {
                            buff.append(sval);
                        }
                        break;

                    case Types.DATE:
                        Date dtval = rset.getDate(i+1);
                        if (dtval != null) {
                            buff.append(dtval);
                        }
                        break;

                    case Types.TIME:
                        Time tval = rset.getTime(i+1);
                        if (tval != null) {
                            buff.append(tval);
                        }
                        break;

                    case Types.TIMESTAMP:
                        Timestamp tsval = rset.getTimestamp(i+1);
                        if (tsval != null) {
                            buff.append(tsval);
                        }
                        break;

                    default:
                        logger.warn("Unknown data type: " + type[i]);
                    }
                }
                pwOut.println(buff.toString());
            }

            // Reset connection "auto-commit" mode if necessary
            if (toggleAC) {
                conn.setAutoCommit(true);
            }
        } catch (SQLException ex) {
            logger.warn("Unable to execute query '" + query + "' -- " + ex.getMessage(), ex);
        } catch (FileNotFoundException ex) {
            logger.warn("Unable to open file '" + targetLocation + "' -- " + ex.getMessage(), ex);
        } catch (UnsupportedEncodingException ex) {
            logger.warn("Unable to open file '" + targetLocation + "' -- " + ex.getMessage(), ex);
        } finally {
            if (stmt  != null) try { stmt.close(); } catch(Exception ex) { }
            if (rset  != null) try { rset.close(); } catch(Exception ex) { }
            if (pwOut != null) try { pwOut.close(); } catch(Exception ex) { }
        }

        return recCnt;
    }

    /**
     * Return the detailed header for a given query
     * @param query SQL query
     * @param delimiter field delimiter
     * @return header line
     */
    private static String getHeader(String query, char delimiter) {
        PreparedStatement stmt = null;

        try {
            // Prepare the statement and get the column header information
            stmt = conn.prepareStatement(query);
            ResultSetMetaData rsmd = stmt.getMetaData();
            return getHeader(rsmd, delimiter);
        } catch (SQLException ex) {
            logger.warn("Unable to execute query '" + query + "' -- " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (stmt  != null) try { stmt.close(); } catch(Exception ex) { }
        }

        return "";
    }

    /**
     * Return a header from a query -- technically the 'ResultSetMetaData' associated
     * with a query -- with the column name, type [java.sql.Types], display size, scale
     * precision.
     * 
     * @param rsmd ResultSetMetaData instance
     * @param delimiter field delimiter
     * @return header record
     * @throws SQLException 
     */
    private static String getHeader(ResultSetMetaData rsmd, char delimiter) throws SQLException {
        StringBuilder buff = new StringBuilder(1024);

        for (int i=1;  i<=rsmd.getColumnCount();  i++) {
            if (i > 1) buff.append(delimiter);
            buff.append(rsmd.getColumnName(i));
            buff.append(",T:").append(rsmd.getColumnType(i));
            buff.append(",D:").append(rsmd.getColumnDisplaySize(i));
            buff.append(",S:").append(rsmd.getScale(i));
            buff.append(",P:").append(rsmd.getPrecision(i));
        }

        return buff.toString();
    }
}
