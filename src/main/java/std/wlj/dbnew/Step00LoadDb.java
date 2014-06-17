package std.wlj.dbnew;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import org.familysearch.standards.core.logging.Logger;

import std.wlj.util.FileUtils;


/**
 * Load the PLACE 2.0 hard-coded data, which defines some basic places, reps,
 * names, sources, etc., in addition to the core data [types, groups, ...] 
 * 
 * @author wjohnson000
 */
public class Step00LoadDb {

    private static Logger logger = new Logger(Step00LoadDb.class);

    /**
     * Command-line arguments
     */
    private static String dbURL  = "jdbc:postgresql://localhost:5432/standards";
    private static String dbUser = "postgres";   // "sams_place";
    private static String dbPassword = "admin";  // "sams_place";
    private static String dataFile = "C:/temp/load-place-db/load-base-values.sql";

    /**
     * Create a connection to the database, or NULL if the connection can't be made.
     *
     * @return
     * @throws SQLException
     */
    private static Connection getConnection() {
        try {
            String dbDriver = "org.postgresql.Driver";
            Class.forName(dbDriver);
            return DriverManager.getConnection(dbURL, dbUser, dbPassword);
        } catch (ClassNotFoundException ex) {
            logger.error("Driver class not found: " + ex);
            return null;
        } catch (SQLException ex) {
            logger.error("Unable to make db connection: " + ex);
            return null;
        }
    }

    /**
     * Load the SQL from the given file, pulling everything from between sets of
     * comment lines.
     *
     * @param conn        database connection
     * @param sqlFileName file containing SQL, UTF-8 encoding
     */
    private static void loadSQL(Connection conn) {
        int cnt = 0;
        StringBuilder buff = new StringBuilder();
        BufferedReader reader = null;
        Map<String,Integer> codeToIdMap = new HashMap<>();

        try {
            reader = FileUtils.getReader(dataFile);
            String sql = null;
            while((sql = reader.readLine()) != null) {
                if (sql.startsWith("INSERT INTO type(")) {
                    int ndx0 = sql.indexOf(" VALUES(");
                    int ndx1 = sql.indexOf(',', ndx0+1);
                    int ndx2 = sql.indexOf(',', ndx1+1);
                    int typeNdx = Integer.parseInt(sql.substring(ndx0+8, ndx1));
                    String typeCod = sql.substring(ndx1+3, ndx2-1);
                    codeToIdMap.put(typeCod, typeNdx);
                }
            }
        } catch(Exception ex) {
            logger.error("Unable to do something ..." + ex.getMessage());
        } finally {
            try { reader.close(); } catch(Exception ex) { }
            reader = null;
        }

        try {
            reader = FileUtils.getReader(dataFile);
            String sql = null;
            while((sql = reader.readLine()) != null) {
                if (sql.startsWith("--")) {
                    if (cnt > 0) {
                        logger.error("Executing " + cnt + " statements ...");
                        doInsert(conn, buff.toString());
                    }
                    cnt = 0;
                    buff = new StringBuilder();
                } else if (sql.startsWith("INSERT INTO place_name(")) {
                    for (Map.Entry<String,Integer> entry : codeToIdMap.entrySet()) {
                        sql = sql.replaceAll(", NT:" + entry.getKey(), ", " + entry.getValue());
                    }
                    cnt++;
                    buff.append(sql).append("\n");
                } else if (sql.startsWith("INSERT INTO place_rep(")) {
                    for (Map.Entry<String,Integer> entry : codeToIdMap.entrySet()) {
                        sql = sql.replaceAll(", PT:" + entry.getKey(), ", " + entry.getValue());
                    }
                    cnt++;
                    buff.append(sql).append("\n");
                } else if (sql.startsWith("INSERT INTO rep_attr(")) {
                    for (Map.Entry<String,Integer> entry : codeToIdMap.entrySet()) {
                        sql = sql.replaceAll(", AT:" + entry.getKey(), ", " + entry.getValue());
                    }
                    cnt++;
                    buff.append(sql).append("\n");
                } else if (sql.startsWith("INSERT INTO citation(")) {
                    for (Map.Entry<String,Integer> entry : codeToIdMap.entrySet()) {
                        sql = sql.replaceAll(", CT:" + entry.getKey(), ", " + entry.getValue());
                    }
                    cnt++;
                    buff.append(sql).append("\n");
                } else if (sql.length() > 0) {
                    cnt++;
                    buff.append(sql).append("\n");
                }
            }
        } catch(Exception ex) {
            logger.error("Unable to do something ..." + ex.getMessage());
        } finally {
            logger.error("Executing " + cnt + " statements ...");
            doInsert(conn, buff.toString());
            try { reader.close(); } catch(Exception ex) { }
        }
    }

    /**
     * Execute the given SQL
     * 
     * @param conn DB connection
     * @param sql query statements to execute
     */
    private static void doInsert(Connection conn, String sql) {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch(SQLException ex) {
            logger.error("Unable to do sql-thing: " + ex.getMessage());
        } finally {
            if (stmt != null) try { stmt.close(); } catch(Exception ex) { }
        }
    }

    /**
     * Get this silly thing a-goin'
     *
     * @param args
     */
    public static void main(String... args) throws SQLException {
        Connection conn = getConnection();
        if (conn != null) {
            conn.setAutoCommit(true);
            loadSQL(conn);
            conn.close();
        }

        System.exit(0);
    }
}