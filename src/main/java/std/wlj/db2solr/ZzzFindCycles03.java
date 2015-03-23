package std.wlj.db2solr;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.*;


/**
 * Read the file that contains reps that were replaced by their parents, and generate a
 * reasonable spreadsheet from it.
 * 
 * @author wjohnson000
 *
 */
public class ZzzFindCycles03 {

    // Basic information about a place-rep
    public static class RepInfo {
        int repId;
        int parId;
        int delId;
        String prefLocale;
        String type;
        String name;

        @Override public String toString() {
            return repId + " <" + type + ":" + name + ">";
        }
    }

    private static final String DB_DRIVER = "org.postgresql.Driver";
    private static final String DB_URL    = "jdbc:postgresql://std-ws-place-rds-dev.cu21thytneyp.us-east-1.rds.amazonaws.com:1836/sams_place";
    private static final String DB_USER   = "fs_schema_owner";
    private static final String DB_PASSWD = "fzBJBhzRiqqaTPsRVzTzEjzhvnuFLoFvduFVSoGWtnOugJukTbaxiNEVlScwHfj";


    public static void main(String... args) throws Exception {
        try (Connection conn = makeConn()) {
            for (String line : Files.readAllLines(Paths.get("C:/temp/zzz-rep-replaced.txt"), Charset.forName("UTF-8"))) {
                String[] tokens = line.split("\\|");
                if (tokens.length > 2) {
                    int repId = Integer.parseInt(tokens[1]);
                    int parId = Integer.parseInt(tokens[2]);
                    RepInfo rInfo = getRepInfo(repId, conn);
                    while (rInfo != null) {
                        System.out.print(rInfo + "|");
                        rInfo = rInfo.parId <= 0 ? null : getRepInfo(rInfo.parId, conn);
                    }
                    System.out.println();

                    rInfo = getRepInfo(repId, conn);
                    while (rInfo != null) {
                        while (rInfo != null  &&  rInfo.delId > 0) {
                            rInfo = getRepInfo(rInfo.delId, conn);
                        }
                        System.out.print(rInfo + "|");
                        rInfo = (rInfo == null || rInfo.parId <= 0) ? null : getRepInfo(rInfo.parId, conn);
                    }
                    System.out.println();
                    System.out.println();
                }
            }
        } catch(SQLException ex) {
            System.out.println("EX: " + ex.getMessage());
        }
    }

    private static Connection makeConn() throws Exception {
        Class.forName(DB_DRIVER);
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWD);
    }

    private static RepInfo getRepInfo(int repId, Connection conn) throws SQLException {
        RepInfo rInfo = null;

        String query =
            "SELECT pr.rep_id, pr.parent_id, pr.delete_id, ty.code, pr.pref_locale " +
            "  FROM place_rep AS pr " +
            "  LEFT JOIN type AS ty on ty.type_id = pr.place_type_id " +
            " WHERE pr.rep_id = " + repId + " " +
            " ORDER BY pr.tran_id DESC";

        try(Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(query)) {
            if (rset.next()) {
                rInfo = new RepInfo();
                rInfo.repId = repId;
                rInfo.parId = rset.getInt("parent_id");
                rInfo.delId = rset.getInt("delete_id");
                rInfo.type  = rset.getString("code");
                rInfo.prefLocale = rset.getString("pref_locale");
            }
        }

        if (rInfo != null) {
             query = "SELECT * FROM rep_display_name WHERE rep_id = " + repId + " ORDER BY tran_id DESC";
             try(Statement stmt = conn.createStatement();
                     ResultSet rset = stmt.executeQuery(query)) {
                 int level = 0;
                 while (rset.next()) {
                     String loc = rset.getString("locale");
                     String nam = rset.getString("text");
                     if (loc.equalsIgnoreCase(rInfo.prefLocale)  &&  level < 4) {
                         level = 4;
                         rInfo.name = nam;
                     } else if (loc.equals("en")  &&  level < 3) {
                         level = 3;
                         rInfo.name = nam;
                     } else if (loc.startsWith("en")  &&  level < 2) {
                         level = 2;
                         rInfo.name = nam;
                     } else if (loc.contains("Latn")  &&  rInfo.name == null) {
                         level = 1;
                         rInfo.name = nam;
                     }
                 }
             }
        }

        return rInfo;
    }
}
