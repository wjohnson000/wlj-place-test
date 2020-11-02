package std.wlj.dbnew;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.familysearch.standards.core.logging.Logger;
import org.familysearch.standards.loader.AppConstants;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

/**
 * Utility methods for dealing with the database, and a PostgreSQL database in
 * particular.
 * 
 * @author wjohnson000
 *
 */
public class DbHelperWLJ {

    private static Logger logger = new Logger(DbHelperWLJ.class);

    private static final String SCHEMA_NAME = System.getProperty("PLACE_DB_SCHEMANAME", "");
    private static final long NINETY_MINUTES_AS_MILLIS = 90 * 60 * 1000;
    private static Map<Integer, String> placeRepChainMap = new TreeMap<Integer, String>();

    private long lastChainLoad = 0;
    private DataSource dataSource;

    /**
     * Initialize this instance with a data-source. All subsequent operations
     * will use this data-source.
     * 
     * @param ds SQL data-source
     */
    public DbHelperWLJ(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public boolean isDatabaseAvailable() {
        boolean dbIsOK = false;

        String query = "SELECT COUNT(*) FROM {schema}type";
        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(injectSchema(query))) {
            if (rset.next()) {
                dbIsOK = true;
            }
        } catch (SQLException ex) {
            logger.info(null, AppConstants.MODULE_NAME, "DB availability.exception", "message", ex.getMessage());
        }

        logger.info(null, AppConstants.MODULE_NAME, "DB availablity.result", "db-is-ok", String.valueOf(dbIsOK));
        return dbIsOK;
    }

    /**
     * Seed the place-chain data with all chains involving every place-rep which is a parent
     * to at least one other place-rep.  NOTE: this is an expensive operation, so once run
     * there is no need to run this again for two hours.
     * 
     * @throws Exception
     */
    public synchronized void seedPlaceChain() {
        logger.info(null, AppConstants.MODULE_NAME, "Starting the seed of the place-rep ID chains ...");
        if (placeRepChainMap.size() > 1000 && (System.currentTimeMillis() - lastChainLoad) < NINETY_MINUTES_AS_MILLIS) {
            logger.info(null, AppConstants.MODULE_NAME, "Place-Rep ID chain generation previously finished");
            return;
        }
        lastChainLoad = System.currentTimeMillis();

        // Save all child-parent associations where the child is also a parent
        Map<Integer, Integer> childParentMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> repReplaceMap  = new HashMap<Integer, Integer>();
        String query =
            "SELECT rep_id, parent_id, delete_id " +
            "  FROM {schema}place_rep " +
            " WHERE rep_id IN " +
            "       (SELECT DISTINCT parent_id FROM {schema}place_rep) " +
            "    OR rep_id IN " + 
            "       (SELECT DISTINCT delete_id FROM {schema}place_rep WHERE delete_id IS NOT NULL) " +
            " ORDER BY tran_id ASC";

        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(injectSchema(query))) {
            while (rset.next()) {
                int repId = rset.getInt("rep_id");
                int parId = rset.getInt("parent_id");
                int delId = rset.getInt("delete_id");
                childParentMap.put(repId, parId);
                if (delId > 0) {
                    repReplaceMap.put(repId, delId);
                }
            }
            logger.info(null, AppConstants.MODULE_NAME, "Chain data retrieved ... start generation of chains");

        } catch (SQLException ex) {
            logger.error(ex, AppConstants.MODULE_NAME, "Unable to generate chains");
        }

        buildPlaceRepChains(childParentMap, repReplaceMap);
        logger.info(null, AppConstants.MODULE_NAME, "Place-Rep ID chain generation complete");
    }

    /**
     * Seed the place-chain data with all chains involving every place-rep which is a parent
     * to at least one other place-rep.  NOTE: this is an expensive operation, so once run
     * there is no need to run this again for two hours.
     * 
     * @throws Exception
     */
    public synchronized void seedPlaceChainNew() {
        logger.info(null, AppConstants.MODULE_NAME, "Starting the seed of the place-rep ID chains ...");
        if (placeRepChainMap.size() > 1000 && (System.currentTimeMillis() - lastChainLoad) < NINETY_MINUTES_AS_MILLIS) {
            logger.info(null, AppConstants.MODULE_NAME, "Place-Rep ID chain generation previously finished");
            return;
        }
        lastChainLoad = System.currentTimeMillis();

        // Save all child-parent associations where the child is also a parent
        Map<Integer, Integer> childParentMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> repReplaceMap  = new HashMap<Integer, Integer>();
        String query =
            "SELECT rep_id, parent_id, delete_id " +
            "  FROM {schema}place_rep " +
            " WHERE rep_id IN " +
            "       (SELECT DISTINCT parent_id FROM {schema}place_rep) " +
            "    OR rep_id IN " + 
            "       (SELECT DISTINCT delete_id FROM {schema}place_rep WHERE delete_id IS NOT NULL) " +
            " ORDER BY tran_id ASC";

        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(injectSchema(query))) {
            while (rset.next()) {
                int repId = rset.getInt("rep_id");
                int parId = rset.getInt("parent_id");
                int delId = rset.getInt("delete_id");
                childParentMap.put(repId, parId);
                if (delId > 0) {
                    repReplaceMap.put(repId, delId);
                }
            }
            logger.info(null, AppConstants.MODULE_NAME, "Chain data retrieved ... start generation of chains");

        } catch (SQLException ex) {
            logger.error(ex, AppConstants.MODULE_NAME, "Unable to generate chains");
        }

        buildPlaceRepChains(childParentMap, repReplaceMap);
        logger.info(null, AppConstants.MODULE_NAME, "Place-Rep ID chain generation complete");
    }

    protected void buildPlaceRepChains(Map<Integer, Integer> childParentMap, Map<Integer, Integer> repReplaceMap) {
        for (Integer childId : childParentMap.keySet()) {
            List<Integer> repIdChain = new ArrayList<>();

            Integer repId = childId;
            while (repReplaceMap.containsKey(repId)) {
                repId = repReplaceMap.get(repId);
            }
            repIdChain.add(repId);

            Integer parId = childParentMap.get(repId);
            while (parId != null && parId > 0) {
                while (repReplaceMap.containsKey(parId)) {
                    parId = repReplaceMap.get(parId);
                }
                if (repIdChain.contains(parId)) {
                    logger.error(null, AppConstants.MODULE_NAME, "Circular reference in place-chain",
                                       "ChildId", String.valueOf(childId),
                                       "RepId", String.valueOf(repId),
                                       "ParId", String.valueOf(parId));
                    repIdChain.clear();
                    repIdChain.add(childId);
                    break;
                } else {
                    repIdChain.add(parId);
                    parId = childParentMap.get(parId);
                }
            }

            String chain = repIdChain
                .stream()
                .map(val -> String.valueOf(val))
                .collect(Collectors.joining(","));
            placeRepChainMap.put(childId, chain);
        }
    }

    /**
     * Return the chain for a parent rep-id
     * 
     * @param parentId parent rep-id
     * @return chain
     */
    public String getChain(int parentId) {
        return placeRepChainMap.get(parentId);
    }

    /**
     * Return an iterator for the forward chain
     * 
     * @return entry set iterator
     */
    public synchronized Iterator<Map.Entry<Integer, String>> getChainIterator() {
        return placeRepChainMap.entrySet().iterator();
    }

    /**
     * Find the maximum rep-id value.
     * 
     * @return maximum rep-id value.
     */
    public long getMaxRepId() {
        return getLongValueFromQuery("SELECT MAX(rep_id) FROM place_rep");
    }

    /**
     * Find the maximum revision (transaction-id) value.
     * 
     * @return maximum revision value.
     */
    public long getMaxRevision() {
        return getLongValueFromQuery("SELECT MAX(tran_id) FROM transaction");
    }

    public List<Long> getIds(String query) {
        String tQuery = this.injectSchema(query);
        return getLongValuesFromQuery(tQuery);
    }

    /**
     * Dump the results of a query to the target location. The first record will
     * be an extended header containing column names and definitions. The given
     * delimiter will be used as a field separator.
     * <p/>
     * This uses a PostgreSQL custom class (CopyManager) that will dump out a header 
     * and delimited rows of data.  It can handle results that number in the hundreds
     * of millions of results.  It's MUCH faster than doing a regular query and
     * looping through the result-set.
     * 
     * @param conn  database connection
     * @param query  SQL query to execute
     * @param targetLocation  file where data is to be written
     * @param delimiter  field delimiter character
     * 
     * @return number of rows in output file, or -1 if an error occurred
     */
    public long execQueryAndSave(String query, File targetLocation, char delimiter) {
        long rowCnt = -1;
        String tQuery = injectSchema(query);
        try (Connection conn = this.getConnection();
                PrintWriter pwOut = new PrintWriter(targetLocation, "UTF-8")) {
            BaseConnection connX = getPostgresqlConnection(conn);
            pwOut.println(getHeader(tQuery, delimiter));
            CopyManager mgr = new CopyManager(connX);

            rowCnt = mgr.copyOut("COPY (" + tQuery + ") TO STDOUT WITH csv DELIMITER '|'", pwOut);
        } catch (Exception ex) {
            logger.warn(ex,
                    AppConstants.MODULE_NAME, "Unable to execute query",
                    "sql", tQuery,
                    "location", targetLocation.toString());
        }

        return rowCnt;
    }

    public Map<Integer, Integer> getPlaceAndRevn(long revn) {
        Map<Integer, Integer> results = new HashMap<>();

        String query = "SELECT tran_id, place_id FROM {schema}transaction WHERE place_id > 0 AND tran_id > " + revn;
        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(injectSchema(query))) {
            while (rset.next()) {
                int placeId = rset.getInt("place_id");
                int tranId = rset.getInt("tran_id");
                int currTranId = results.getOrDefault(placeId, 0);
                results.put(placeId, Math.max(currTranId, tranId));
            }
        } catch (SQLException ex) {
            logger.info(ex, AppConstants.MODULE_NAME, "Unable to complere query.");
        }
        
        return results;
    }

    public Map<Integer, Integer> getRepAndRevn(long revn) {
        Map<Integer, Integer> results = new HashMap<>();

        String query = "SELECT tran_id, rep_id FROM {schema}transaction WHERE rep_id > 0 AND tran_id > " + revn;
        try (Connection conn = dataSource.getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(injectSchema(query))) {
            while (rset.next()) {
                int repId = rset.getInt("rep_id");
                int tranId  = rset.getInt("tran_id");
                int currTranId = results.getOrDefault(repId, 0);
                results.put(repId, Math.max(currTranId, tranId));
            }
        } catch (SQLException ex) {
            logger.info(ex, AppConstants.MODULE_NAME, "Unable to complere query.");
        }
        
        return results;
    }

    /**
     * Retrieve a LONG value based on a query -- the first column of the first result
     * 
     * @return long value
     */
    protected long getLongValueFromQuery(String query) {
        List<Long> results = getLongValuesFromQuery(query);
        return (results.isEmpty()) ? 0L : results.get(0).longValue();
    }

    /**
     * Retrieve a list of LONG values based on a query -- use the first column of each result.
     * 
     * @param query SQL query
     * @return list of long values
     */
    protected List<Long> getLongValuesFromQuery(String query) {
        List<Long> results = new ArrayList<>();

        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(query)) {
           while (rset.next()) {
                results.add(rset.getLong(1));
            }
        } catch (Exception ex) {
            logger.error(ex, AppConstants.MODULE_NAME, "Unable to complete query.");
        }

        return results;
    }

    /**
     * Retrieve query data, one row per record entry, given a query field [column] names.
     * The data will be separated by the given delimiter.  NOTE: This should not be used
     * when hundreds-of-thousands or millions of rows can be returned.
     * 
     * @param stmt  prepared-statement, ready to run
     * @param delimiter  field delimiter
     * @param fields  array of columns in the order they're to be pulled
     * @return List of delimited fields
     * @throws SQLException
     */
    public List<String> getGenericRows(String query, String delimiter, String... fields) {
        List<String> results = new ArrayList<String>();

        String queryWithSchema = injectSchema(query);
        try (Connection conn = getConnection();
                Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(queryWithSchema)) {
            while (rset.next()) {
                StringBuilder buff = new StringBuilder(128);
                boolean first = true;
                for (String field : fields) {
                    buff.append(first ? "" : delimiter);
                    String valOrNull = rset.getString(field);
                    buff.append(rset.wasNull() ? "" : valOrNull);
                    first = false;
                }
                results.add(buff.toString());
            }
        } catch (SQLException ex) {
            logger.warn(ex, AppConstants.MODULE_NAME, "Unable to retrieve application data");
        }

        return results;
    }

    /**
     * Inject the schema name into a query if one is required. The Java system
     * property "PLACE_DB_SCHEMANAME" is used to determine this.
     * 
     * @param query  query, with a "{schema}" tag where the schema name is to be inserted.
     * 
     * @return new query, with an optional schema name included.
     */
    public String injectSchema(String query) {
        String schema = SCHEMA_NAME;
        if (schema.length() > 0) {
            schema = schema.trim() + ".";
        }

        return query.replaceAll("\\{schema}", schema);
    }

    /**
     * Inject a WHERE clause restricting the rep-id to a given range.
     * 
     * @param query  query, with a "{schema}" tag where the schema name is to be inserted.
     * @param firstId  the lowest rep-id to be retrieved
     * @param lastId  the highest rep-id to be retrieved
     * 
     * @return new query, with an optional schema name included.
     */
    public String injectRepIdWhere(String query, long firstId, long lastId) {
        String whereSimple = "";
        String whereComplex = "";

        if (firstId > 0 && lastId > 0) {
            whereSimple = " WHERE rep_id BETWEEN " + firstId + " AND " + lastId + " ";
            whereComplex = " WHERE rep.rep_id BETWEEN " + firstId + " AND " + lastId + " ";
        }

        return query.replaceAll("\\{where.rep}", whereSimple).replaceAll("\\{where.rep.rep}", whereComplex);
    }

    /**
     * Inject a WHERE clause restricting the place-id to a given range.
     * 
     * @param query  query, with a "{schema}" tag where the schema name is to be inserted.
     * @param firstId  the lowest place-id to be retrieved
     * @param lastId  the highest place-id to be retrieved
     * 
     * @return new query, with an optional schema name included.
     */
    public String injectPlaceIdAnd(String query, long firstId, long lastId) {
        String whereSimple = "";
        String whereComplex = "";

        if (firstId > 0 && lastId > 0) {
            whereSimple = " AND place_id BETWEEN " + firstId + " AND " + lastId + " ";
            whereComplex = " AND plc.place_id BETWEEN " + firstId + " AND " + lastId + " ";
        }

        return query.replaceAll("\\{and.plc}", whereSimple).replaceAll("\\{and.plc.plc}", whereComplex);
    }

    /**
     * Return the detailed header for a given query
     * 
     * @param conn  database connection
     * @param query  SQL query
     * @param delimiter  field delimiter
     * @return header line
     */
    protected String getHeader(String query, char delimiter) {
        try (Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSetMetaData rsmd = stmt.getMetaData();
            return getHeader(rsmd, delimiter);
        } catch (SQLException ex) {
            logger.warn(ex, AppConstants.MODULE_NAME, "Unable to execute query", "sql", query);
        }

        return "";
    }

    /**
     * Return a header from a query -- technically the 'ResultSetMetaData' associated with a
     * query -- with the column name, type [java.sql.Types], display size, scale precision.
     * 
     * @param rsmd  ResultSetMetaData instance
     * @param delimiter  field delimiter
     * @return header record
     * @throws SQLException
     */
    protected String getHeader(ResultSetMetaData rsmd, char delimiter) throws SQLException {
        StringBuilder buff = new StringBuilder(1024);

        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
            buff.append((i <= 1) ? "" : delimiter);
            buff.append(rsmd.getColumnName(i));
            buff.append(",T:").append(rsmd.getColumnType(i));
            buff.append(",D:").append(rsmd.getColumnDisplaySize(i));
            buff.append(",S:").append(rsmd.getScale(i));
            buff.append(",P:").append(rsmd.getPrecision(i));
        }

        return buff.toString();
    }

    /**
     * Unwrap the connection from the data-source looking for a PostgreSQL
     * connection. The connection opened here will be closed elsewhere.
     * 
     * @return PostgreSQL connection
     */
    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection().getMetaData().getConnection(); // NOSONAR
    }

    /**
     * Unwrap the connection from the data-source looking for a PostgreSQL
     * connection. The connection opened here will be closed elsewhere.
     * 
     * @return PostgreSQL connection
     */
    protected org.postgresql.core.BaseConnection getPostgresqlConnection(Connection dbConn) throws SQLException {
        return dbConn.unwrap(org.postgresql.core.BaseConnection.class);
    }

}