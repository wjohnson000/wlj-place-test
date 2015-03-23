package std.wlj.db2solr;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.*;


/**
 * Generate three files required to look for chains that have cycles in them, particularly
 * when taking the delete-id into account ...
 * 
 * @author wjohnson000
 *
 */
public class ZzzFindCycles01 {
    private static final String DB_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL    = "jdbc:postgresql://std-ws-place-rds-dev.cu21thytneyp.us-east-1.rds.amazonaws.com:1836/sams_place";
    private static final String DB_USER   = "fs_schema_owner";
    private static final String DB_PASSWD = "fzBJBhzRiqqaTPsRVzTzEjzhvnuFLoFvduFVSoGWtnOugJukTbaxiNEVlScwHfj";
//    private static final String DB_URL    = "jdbc:postgresql://localhost:5432/wlj";
//    private static final String DB_USER   = "wlj";
//    private static final String DB_PASSWD = "wlj";


    public static void main(String... args) throws Exception {
        try (Connection conn = makeConn()) {
            saveChildParMap(conn);
            saveDelRepIdMap(conn);
            saveLatestChildParMap(conn);
        } catch(SQLException ex) {
            System.out.println("EX: " + ex.getMessage());
        }
    }

    private static Connection makeConn() throws Exception {
        Class.forName(DB_DRIVER);
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
    }

    private static void saveChildParMap(Connection conn) throws Exception {
        // Save all child-parent associations where the child is also a parent 
        Map<Integer,Integer> childParMap = new TreeMap<>();
        String query =
            "SELECT rep_id, parent_id " +
            "  FROM place_rep " +
            " WHERE rep_id IN " +
            "       (SELECT DISTINCT parent_id FROM place_rep)";

        try(Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery(query)) {
            while (rset.next()) {
                int repId = rset.getInt("rep_id");
                int parId = rset.getInt("parent_id");
                childParMap.put(repId, parId);
            }
        }

        System.out.println("Child-Parent size: " + childParMap.size());

        // Save the data for future processing
        List<String> data = new ArrayList<>();
        for (Map.Entry<Integer,Integer> entry : childParMap.entrySet()) {
            data.add(entry.getKey() + "|" + entry.getValue());
        }
        Files.write(Paths.get("C:/temp/zzz-rep-chain.txt"), data, Charset.forName("UTF-8"));
    }

    private static void saveDelRepIdMap(Connection conn) throws Exception {
        // Save all rep-id --> delete-id associations
        Map<Integer,Integer> delRepIdMap = new TreeMap<>();
        String query =
            "SELECT rep_id, delete_id " +
            "  FROM place_rep " +
            " WHERE delete_id > 0";

        try(Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery(query)) {
            while (rset.next()) {
                int repId = rset.getInt("rep_id");
                int delId = rset.getInt("delete_id");
                delRepIdMap.put(repId, delId);
            }
        }

        System.out.println("Rep-Delete size: " + delRepIdMap.size());

        // Save the data for future processing
        List<String> data = new ArrayList<>();
        for (Map.Entry<Integer,Integer> entry : delRepIdMap.entrySet()) {
            data.add(entry.getKey() + "|" + entry.getValue());
        }
        Files.write(Paths.get("C:/temp/zzz-delete-id.txt"), data, Charset.forName("UTF-8"));
    }

    private static void saveLatestChildParMap(Connection conn) throws Exception {
        // Save all child-parent associations, but only the latest revision#
        int prevRepId = 0;
        List<String> data = new ArrayList<>();
        Files.write(Paths.get("C:/temp/zzz-child-par.txt"), data, Charset.forName("UTF-8"));

        String query =
            "SELECT rep_id, parent_id " +
            "  FROM place_rep " +
            " ORDER BY rep_id ASC, tran_id DESC";

        try(Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery(query)) {
            while (rset.next()) {
                int repId = rset.getInt("rep_id");
                int parId = rset.getInt("parent_id");
                if (repId != prevRepId) {
                    data.add(repId + "|" + parId);
                }
                prevRepId = repId;
                if (data.size() == 500000) {
                    System.out.println("Writing more lines ... ");
                    Files.write(Paths.get("C:/temp/zzz-child-par.txt"), data, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
                    data.clear();
                }
            }
        }

        Files.write(Paths.get("C:/temp/zzz-child-par.txt"), data, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
    }
}
