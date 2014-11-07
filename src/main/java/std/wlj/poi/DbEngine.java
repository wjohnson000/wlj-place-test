package std.wlj.poi;

import java.sql.*;
import java.util.Objects;


/**
 * A class that can create a Truth-Set, with its associated Truths and Truth-Values.
 * It is relative "dumb", and knows only about DB-ish things.
 * 
 * @author wjohnson000
 *
 */
public class DbEngine {

    private static final String DB_DRIVER       = "org.postgresql.Driver";

    private static final String SEQ_TRUTH_SET   = "truth_set_seq";
    private static final String SEQ_TRUTH       = "truth_seq";
    private static final String SEQ_TRUTH_VALUE = "truth_value_seq";

    private static final String SQL_NEXT_SEQ    = "SELECT NEXTVAL(?)";
    private static final String SQL_TRUTH_SET   = "INSERT INTO truth_set(id, type, name, version) VALUES(?,?,?,?)";
    private static final String SQL_TRUTH       = "INSERT INTO truth(id, set_id, val, attributes) VALUES(?,?,?,?)";
    private static final String SQL_TRUTH_VALUE = "INSERT INTO truth_value(id, truth_id, val, attributes, score) VALUES(?,?,?,?,?)";

    private String     dbUrl;
    private String     username;
    private String     password;

    private Connection conn = null;
    private PreparedStatement stmtNextSeq    = null;
    private PreparedStatement stmtTruthSet   = null;
    private PreparedStatement stmtTruth      = null;
    private PreparedStatement stmtTruthValue = null;


    /**
     * Constructor takes JDBC parameters.
     * 
     * @param dbUrl database URL
     * @param username username
     * @param password password
     */
    public DbEngine(String dbUrl, String username, String password) {
        Objects.requireNonNull(dbUrl, "DB-URL can't be null");
        Objects.requireNonNull(username, "Username can't be null");
        Objects.requireNonNull(password, "Password can't be null");

        this.dbUrl    = dbUrl;
        this.username = username;
        this.password = password;
    }

    /**
     * Set up database connection and associated resources
     * 
     * @return TRUE if we have a good connection, FALSE otherwise
     */
    public boolean initialize() {
        connectToDb();
        return (conn != null);
    }

    /**
     * Close the database resources -- this method should be called by the controlling
     * application.
     */
    public void shutdown() {
        try {
            stmtNextSeq.close();
            stmtTruthSet.close();
            stmtTruth.close();
            stmtTruthValue.close();
        } catch(SQLException ex) {
            System.out.println("Error disconnecting from database: " + ex.getMessage());
        }
    }

    /**
     * Reset all sequence values based on the MAX(id) from the associated table
     */
    public void resetSequences() {
        resetSequence("truth_set", SEQ_TRUTH_SET);
        resetSequence("truth", SEQ_TRUTH);
        resetSequence("truth_value", SEQ_TRUTH_VALUE);
    }

    /**
     * Add a new "TRUTH_SET" entry
     * 
     * @param type type, which should be one of "place", "placerep", "name" or
     *        "namegrouping"
     * @param name truth-set name, application-defined
     * @param version truth-set version, application-defined
     * @return unique identifier for the truth-set
     */
    public int addTruthSet(String type, String name, String version) {
        Objects.requireNonNull(conn);

        int nextId = -1;
        try {
            nextId = getNextFromSequence(SEQ_TRUTH_SET);
            stmtTruthSet.clearParameters();
            stmtTruthSet.setInt(1, nextId);
            stmtTruthSet.setString(2, type);
            stmtTruthSet.setString(3, name);
            stmtTruthSet.setString(4, version);
            int cnt = stmtTruthSet.executeUpdate();
            if (cnt == 0) {
                nextId = -1;
                System.out.println("Unable to insert TRUTH_SET for some reason.");
            }
        } catch(SQLException ex) {
            System.out.println("Unable to insert TRUTH_SET: " + ex.getMessage());
        }

        return nextId;
    }

    /**
     * Add a "TRUTH" to an existing "TRUTH_SET"
     * 
     * @param truthSetId truth-set identifier
     * @param value value which will control the search process
     * @param attributes additional attributes which may be used as part of the search
     * @return unique identifier for the truth
     */
    public int addTruth(int truthSetId, String value, String attributes) {
        Objects.requireNonNull(conn);

        int nextId = -1;
        try {
            nextId = getNextFromSequence(SEQ_TRUTH);
            stmtTruth.clearParameters();
            stmtTruth.setInt(1, nextId);
            stmtTruth.setInt(2, truthSetId);
            stmtTruth.setString(3, value);
            stmtTruth.setString(4, attributes);
            int cnt = stmtTruth.executeUpdate();
            if (cnt == 0) {
                nextId = -1;
                System.out.println("Unable to insert TRUTH for some reason.");
            }
        } catch(SQLException ex) {
            System.out.println("Unable to insert TRUTH: " + ex.getMessage());
        }

        return nextId;
    }

    /**
     * Add a "TRUTH_VALUE" to a "TRUTH"
     * 
     * @param truthId truth identifier
     * @param value an expected result from the search process
     * @param attributes additional attribute which should come back from the search
     * @param score score of this truth-value
     * @return unique identifier for the truth-value
     */
    public int addTruthValue(int truthId, String value, String attributes, double score) {
        Objects.requireNonNull(conn);

        int nextId = -1;
        try {
            nextId = getNextFromSequence(SEQ_TRUTH_VALUE);
            stmtTruthValue.clearParameters();
            stmtTruthValue.setInt(1, nextId);
            stmtTruthValue.setInt(2, truthId);
            stmtTruthValue.setString(3, value);
            stmtTruthValue.setString(4, attributes);
            stmtTruthValue.setDouble(5, score);
            int cnt = stmtTruthValue.executeUpdate();
            if (cnt == 0) {
                nextId = -1;
                System.out.println("Unable to insert TRUTH_VALUE for some reason.");
            }
        } catch(SQLException ex) {
            System.out.println("Unable to insert TRUTH_VALUE: " + ex.getMessage());
        }

        return nextId;
    }

    /**
     * Return the next value from the given sequence
     * @param seqName
     * @return
     */
    private int getNextFromSequence(String seqName) throws SQLException {
        Objects.requireNonNull(conn);

        stmtNextSeq.clearParameters();
        stmtNextSeq.setString(1, seqName);
        ResultSet rset = stmtNextSeq.executeQuery();
        if (rset.next()) {
            return rset.getInt(1);
        } else {
            return -1;
        }
    }

    /**
     * Reset a sequence based on the MAX(id) from the given table
     * 
     * @param tableName table name
     * @param seqName sequence name
     */
    private void resetSequence(String tableName, String seqName) {
        int maxId = -1;

        // Retrieve the MAX(id) from the given table
        try (Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery("SELECT MAX(id) FROM " + tableName)) {
            if (rset.next()) {
                maxId = rset.getInt(1);
            }
        } catch(SQLException ex) {
            System.out.println("Unable to get max identifier from table: " + tableName + ": "+ ex.getMessage());
        }

        // Update the sequence base on that id, if needed
        if (maxId > -1) {
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("ALTER SEQUENCE " + seqName + " RESTART WITH " + (maxId+1));
            } catch(SQLException ex) {
                System.out.println("Unable to update sequence: " + seqName + ": "+ ex.getMessage());
            }
        }
    }

    /**
     * Acquire a DB connection
     */
    private void connectToDb() {
        try {
            Class.forName(DB_DRIVER);
            conn = DriverManager.getConnection(dbUrl, username, password);

            stmtNextSeq = conn.prepareStatement(SQL_NEXT_SEQ);
            stmtTruthSet = conn.prepareStatement(SQL_TRUTH_SET);
            stmtTruth = conn.prepareStatement(SQL_TRUTH);
            stmtTruthValue = conn.prepareStatement(SQL_TRUTH_VALUE);
        } catch (ClassNotFoundException | SQLException ex) {
            conn = null;
            System.out.println("Unable to connect to the database ... " + ex.getMessage());
        }
    }
}
