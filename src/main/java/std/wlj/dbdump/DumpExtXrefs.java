package std.wlj.dbdump;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.*;
import std.wlj.datasource.DbConnectionManager;

public class DumpExtXrefs {

    static final String fileBase = "C:/temp";
    static final String fileName = "db-ext-xref-all.txt";

    public static void main(String... args) {
        try(Connection conn = DbConnectionManager.getConnectionAws()) {
            Files.write(Paths.get(fileBase, fileName), Arrays.asList(""), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            dumpExternalXrefs(conn);
        } catch(SQLException | IOException ex) {
            System.out.println("Unable to do something ... " + ex.getMessage());
        }
    }

    static void dumpExternalXrefs(Connection conn) throws IOException {
        int xrefId = 1;
        boolean again = true;
        while (again) {
            System.out.println("First xref-id: " + xrefId);
            again = false;
            List<String> data = new ArrayList<>();
            try(Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(
                        "SELECT xref_id, rep_id, type_id, external_key, pub_flag " +
                        "  FROM external_xref " +
                        " WHERE xref_id BETWEEN " + xrefId + " AND " + (xrefId + 999_999) +
                        " ORDER BY xref_id ")) {
                while (rset.next()) {
                    again = true;
                    data.add(rset.getString("xref_id") + "|" + rset.getString("rep_id") + "|" + rset.getString("type_id") + "|" + rset.getInt("external_key") + "|" + rset.getBoolean("pub_flag"));
                }
            } catch(SQLException ex) {
                System.out.println("Unable to do get external-xrefs ... " + ex.getMessage());
            }

            Files.write(Paths.get(fileBase, fileName), data, StandardOpenOption.APPEND);
            xrefId += 1_000_000;
        }
    }
}
