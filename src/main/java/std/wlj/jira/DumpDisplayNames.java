package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.*;
import std.wlj.datasource.DbConnectionManager;

public class DumpDisplayNames {

    static final String fileBase = "C:/temp";
    static final String fileName = "db-display-name-all.txt";

    public static void main(String... args) {
        try(Connection conn = DbConnectionManager.getConnectionAws()) {
            Files.write(Paths.get(fileBase, fileName), Arrays.asList(""), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            dumpDisplayNames(conn);
        } catch(SQLException | IOException ex) {
            System.out.println("Unable to do something ... " + ex.getMessage());
        }
    }

    static void dumpDisplayNames(Connection conn) throws IOException {
        int repId = 1;
        boolean again = true;
        while (again) {
            System.out.println("First rep-id: " + repId);
            again = false;
            List<String> data = new ArrayList<>();
            try(Statement stmt = conn.createStatement();
//                ResultSet rset = stmt.executeQuery(
//                        "SELECT rep_id, locale, text, tran_id, delete_flag " +
//                        "  FROM rep_display_name AS dsp " +
//                        " WHERE rep_id BETWEEN " + repId + " AND " + (repId + 999_999) +
//                        "   AND tran_id = (SELECT MAX(tran_id) FROM rep_display_name AS dspx WHERE dsp.rep_id = dspx.rep_id AND dsp.locale = dspx.locale) " +
//                        " ORDER BY rep_id, tran_id ")) {
                ResultSet rset = stmt.executeQuery(
                        "SELECT rep.rep_id, rep.delete_id, dsp.locale, dsp.text, dsp.tran_id, dsp.delete_flag " +
                        "  FROM place_rep AS rep " +
                        "  JOIN rep_display_name AS dsp ON dsp.rep_id = rep.rep_id " +
                        " WHERE rep.rep_id BETWEEN " + repId + " AND " + (repId + 999_999) +
                        "   AND rep.tran_id = (SELECT MAX(tran_id) FROM place_rep AS repx WHERE rep.rep_id = repx.rep_id) " +
                        "   AND dsp.tran_id = (SELECT MAX(tran_id) FROM rep_display_name AS dspx WHERE dsp.rep_id = dspx.rep_id AND dsp.locale = dspx.locale) " +
                        " ORDER BY rep.rep_id, dsp.locale ")) {
                while (rset.next()) {
                    again = true;
                    data.add(rset.getString("rep_id") + "|" + rset.getString("delete_id") + "|" + rset.getString("locale") + "|" + rset.getString("text") + "|" + rset.getInt("tran_id") + "|" + rset.getBoolean("delete_flag"));
                }
            } catch(SQLException ex) {
                System.out.println("Unable to do get place-names ... " + ex.getMessage());
            }

            Files.write(Paths.get(fileBase, fileName), data, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
            repId += 1_000_000;
        }
    }
}
