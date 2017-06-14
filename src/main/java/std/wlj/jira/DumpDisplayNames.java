package std.wlj.jira;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.*;
import std.wlj.datasource.DbConnectionManager;

public class DumpDisplayNames {
    public static void main(String... args) {
        try(Connection conn = DbConnectionManager.getConnectionAws()) {
            Files.write(Paths.get("C:/temp/display-name-all.txt"), Arrays.asList(""), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
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
                ResultSet rset = stmt.executeQuery(
                        "SELECT rep_id, locale, text, tran_id, delete_flag " +
                        "  FROM rep_display_name AS dsp " +
                        " WHERE rep_id BETWEEN " + repId + " AND " + (repId + 999_999) +
                        "   AND tran_id = (SELECT MAX(tran_id) FROM rep_display_name AS dspx WHERE dsp.rep_id = dspx.rep_id AND dsp.locale = dspx.locale) " +
                        " ORDER BY rep_id, tran_id ")) {
                while (rset.next()) {
                    again = true;
                    data.add(rset.getString("rep_id") + "|" + rset.getString("locale") + "|" + rset.getString("text") + "|" + rset.getInt("tran_id") + "|" + rset.getBoolean("delete_flag"));
                }
            } catch(SQLException ex) {
                System.out.println("Unable to do get place-names ... " + ex.getMessage());
            }

            Files.write(Paths.get("C:/temp/display-name-all.txt"), data, StandardOpenOption.APPEND);
            repId += 1_000_000;
        }
    }
}
