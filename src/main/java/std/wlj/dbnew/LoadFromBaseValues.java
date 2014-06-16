package std.wlj.dbnew;

import java.io.*;
import java.sql.*;
import java.util.*;

import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.familysearch.standards.core.logging.Logger;


/**
 * Load the PLACE 2.0 base data into a database.
 * 
 * @author wjohnson000
 */
public class LoadFromBaseValues {

    private static Logger logger = new Logger(LoadFromBaseValues.class);

    /**
     * Command-line arguments
     */
    private static String dbURL  = "jdbc:postgresql://localhost:5432/standards";
    private static String dbUser = "postgres";   // "sams_place";
    private static String dbPassword = "admin";  // "sams_place";
    private static String sqlDirectory = "C:/temp/place-2.0/sql-files";
    private static String version = "1.19";

    /**
     * List of files associated with each table option
     */
    private static Map<String,List<String>> tableFileNames = new TreeMap<>();
    private static Map<String,String[]>     tableCopyStmts = new HashMap<>();

    static {
        tableFileNames.put("00-placeType",  Arrays.asList("placeType", "placeTypeTerm"));
        tableFileNames.put("01-attrType",   Arrays.asList("placeAttributeType", "placeAttributeTypeTerm"));
        tableFileNames.put("02-nameType",   Arrays.asList("placeNameType", "placeNameTypeTerm"));
        tableFileNames.put("03-sourceType", Arrays.asList("placeSourceType", "placeSourceTypeTerm"));
        tableFileNames.put("04-group",      Arrays.asList("group", "groupHierarchy", "groupMember", "groupTerm"));
//        tableFileNames.put("05-place_seq",  Arrays.asList("placeSequences"));
    }

    static {
        String[] copyArrayType = {
            "COPY sams_place.TYPE(type_id, code, type_cat, pub_flag) FROM stdin",
            "COPY sams_place.TYPE_TERM(term_id, type_id, locale, text, description) FROM stdin"
        };
        tableCopyStmts.put("00-placeType", copyArrayType);
        tableCopyStmts.put("01-attrType", copyArrayType);
        tableCopyStmts.put("02-nameType", copyArrayType);
        tableCopyStmts.put("03-sourceType", copyArrayType);

        String[] copyArrayGroup = {
            "COPY sams_place.GROUP_DEF(group_id, group_type, pub_flag) FROM stdin",
            "COPY sams_place.GROUP_HIERARCHY(parent_group_id, child_group_id) FROM stdin",
            "COPY sams_place.GROUP_MEMBER(group_id, entity_id, delete_flag) FROM stdin",
            "COPY sams_place.GROUP_TERM(term_id, group_id, locale, name, description) FROM stdin"
        };
        tableCopyStmts.put("04-group", copyArrayGroup);
    }

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
     * Start the load process, by running each required file in turn
     *
     * @param conn database connection
     */
    private static void startLoad(Connection conn) {
        for (Map.Entry<String,List<String>> entry : tableFileNames.entrySet()) {
            String to = entry.getKey();
            List<String> sqlFileNames = entry.getValue();
            String[] copyStmts = tableCopyStmts.get(to);                    
            Integer fileIndex = 0;
            for (String sqlFileName : sqlFileNames) {
                logger.debug("Attempting to open file: " + sqlFileName);
                if (new File(sqlDirectory, fileWithVersion(sqlFileName)).exists()) {
                    logger.debug("Found SQL data file: " + fileWithVersion(sqlFileName));
                    if (null != copyStmts) {
                        logger.info("Reading sql file: " + sqlFileName + ", using copy command: \"" + copyStmts[fileIndex] + ".\"");
                        loadSQL(conn, sqlFileName, copyStmts[fileIndex]);
                    } else {
                        logger.info("Reading sql file: " + sqlFileName + ", using insert statements.");
                        loadSQL(conn, sqlFileName);
                    }
                } else {
                    logger.warn("SQL file not found: " + fileWithVersion(sqlFileName));
                }
                fileIndex++;
            }
        }
    }

    private static void loadSQL(Connection conn, String sqlFileName) {
        String nullStmt = null;
        loadSQL(conn, sqlFileName, nullStmt);
    }

    /**
     * Load the SQL from the given file, committing every 5000 lines
     *
     * @param conn        database connection
     * @param sqlFileName file containing SQL, UTF-8 encoding
     */
    private static void loadSQL(Connection conn, String sqlFileName, String copyStmt) {
        logger.error("Loading File: " + fileWithVersion(sqlFileName));

        long readCount = 0;
        long insCount = 0;
        long nnow = System.currentTimeMillis();
        Statement stmt = null;
        BufferedReader reader = null;
        File sqlFile = new File(sqlDirectory, fileWithVersion(sqlFileName));
        String sql = "";
        try {
            reader = new BufferedReader(new InputStreamReader(new FileInputStream(sqlFile), "UTF-8"), 1024 * 64);
            if (null != copyStmt) {
                CopyManager copyMgr = new CopyManager((BaseConnection) conn);
                insCount = copyMgr.copyIn(copyStmt, reader);
            } else {
                stmt = conn.createStatement();
                while ((sql = reader.readLine()) != null) {
                    if (readCount % 100000 == 0) {
                        logger.debug("  Processed: " + readCount);
                    }

                    stmt.addBatch(sql);
                    if (readCount % 4000 == 0) {
                        int[] cnts = stmt.executeBatch();
                        for (int cnt : cnts) {
                            insCount += cnt;
                        }
                        stmt.clearBatch();
                    }
                }
            }
        } catch (IOException | SQLException ex) {
            logger.error("Unable to process file: " + sqlFile, ex);
            if (ex instanceof SQLException) {
                SQLException sqlex = (SQLException)ex;
                while (sqlex != null) {
                    sqlex = sqlex.getNextException();
                    logger.error("  Next ex: " + sqlex);
                }
            }
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ex) {
                    logger.info("Unable to close file reader: " + ex.getMessage());
                }
            }

            // Attempt to execute the final batch of INSERT statements
            if (stmt != null) {
                logger.info("SQL statement: " + sql);
                try {
                    int[] cnts = stmt.executeBatch();
                    for (int cnt : cnts) {
                        insCount += cnt;
                    }
                } catch (Exception ex) {
                    logger.error("Unable to execute final INSERT statements ..." + ex.getMessage());
                    for (StackTraceElement e : ex.getStackTrace()) {
                        logger.error(e);
                    }
                } finally {
                    try {
                        stmt.close();
                    } catch (Exception ex) {
                        logger.info("Unable to close statement: " + ex.getMessage());
                    }
                }
            }
        }

        logger.error("Done loading ... time: " + (System.currentTimeMillis() - nnow));
        logger.error("  Read: " + readCount);
        logger.error("  Inserted: " + insCount);
    }

    /**
     * Generate a file name based on the stored "version" value.  If null or empty,
     * don't add anything.  Otherwise add the "version" value to the file name.
     *
     * @param baseName base file name
     * @return file name with version
     */
    private static String fileWithVersion(String baseName) {
        if (version == null || version.trim().length() == 0) {
            return baseName + ".sql";
        } else if (version.startsWith("-")) {
            return baseName + version + ".sql";
        } else {
            return baseName + "-" + version + ".sql";
        }
    }

    /**
     * Get this silly thing a-goin'
     *
     * @param args
     */
    public static void main(String... args) throws SQLException {
        long nnow = System.currentTimeMillis();

        Connection conn = getConnection();
        if (conn != null) {
            conn.setAutoCommit(true);
            startLoad(conn);
            conn.close();
        }

        logger.info("==========================================================================");
        logger.info("Process done ... total time (ms): " + (System.currentTimeMillis() - nnow));
        logger.info("==========================================================================");
    }
}