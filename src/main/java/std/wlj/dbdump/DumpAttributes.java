package std.wlj.dbdump;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.*;
import std.wlj.datasource.DbConnectionManager;

public class DumpAttributes {

    static final String fileBase = "C:/temp";
    static final String fileName = "db-attribute-all.txt";

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
        int count = 0;

        boolean again = true;
        while (again) {
            System.out.println("First rep-id: " + repId);
            again = repId < 8_999_999;
            List<String> data = new ArrayList<>();
            try(Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(
                        "SELECT attr_id, tran_id, rep_id, attr_type_id, year, locale, attr_value, delete_flag " +
                        "  FROM rep_attr AS attr " +
                        " WHERE rep_id BETWEEN " + repId + " AND " + (repId + 999_999) +
//                        "   AND tran_id = (SELECT MAX(tran_id) FROM rep_attr AS attrx WHERE attr.rep_id = attrx.rep_id) " +
                        " ORDER BY rep_id, attr_id, tran_id")) {
                while (rset.next()) {
                    count++;
                    again = true;
                    data.add(rset.getInt("rep_id") + "|" + rset.getInt("attr_id") + "|" + rset.getInt("tran_id") + "|" +
                             rset.getInt("attr_type_id") + "|" + rset.getInt("year") + "|" + rset.getString("locale") + "|" +
                             rset.getString("attr_value") + "|" + rset.getBoolean("delete_flag"));
                }
            } catch(SQLException ex) {
                System.out.println("Unable to do get place-names ... " + ex.getMessage());
            }

            Files.write(Paths.get(fileBase, fileName), data, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
            repId += 1_000_000;
        }

        System.out.println("Count: " + count);
    }
}
