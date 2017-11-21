package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.*;
import std.wlj.datasource.DbConnectionManager;

public class DumpVariantNames {

    static final String fileBase = "C:/temp";
    static final String fileName = "db-variant-name-all.txt";

    public static void main(String... args) {
        try(Connection conn = DbConnectionManager.getConnectionAws()) {
            Files.write(Paths.get(fileBase, fileName), Arrays.asList(""), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            dumpPlaceNames(conn);
        } catch(SQLException | IOException ex) {
            System.out.println("Unable to do something ... " + ex.getMessage());
        }
    }

    static void dumpPlaceNames(Connection conn) throws IOException {
        int placeId = 1;
        boolean again = true;
        while (again) {
            System.out.println("First place-id: " + placeId);
            again = placeId < 8_500_000;
            List<String> data = new ArrayList<>();
            try(Statement stmt = conn.createStatement();
//                ResultSet rset = stmt.executeQuery(
//                        "SELECT place_id, name_id, tran_id, locale, text, delete_flag " +
//                        "  FROM place_name AS nam " +
//                        " WHERE place_id BETWEEN " + placeId + " AND " + (placeId + 499_999) +
//                        "   AND tran_id = (SELECT MAX(tran_id) FROM place_name AS namx WHERE nam.name_id = namx.name_id) " +
//                        " ORDER BY place_id, name_id, tran_id")) {
                ResultSet rset = stmt.executeQuery(
                        "SELECT plc.place_id, plc.delete_id, var.name_id, var.tran_id, var.locale, var.text, var.delete_flag " +
                        "  FROM place AS plc " +
                        "  JOIN place_name AS var ON var.place_id = plc.place_id " +
                        " WHERE plc.place_id BETWEEN " + placeId + " AND " + (placeId + 499_999) +
                        "   AND plc.tran_id = (SELECT MAX(tran_id) FROM place AS plcx WHERE plc.place_id = plcx.place_id) " +
                        "   AND var.tran_id = (SELECT MAX(tran_id) FROM place_name AS varx WHERE var.name_id = varx.name_id) " +
                        " ORDER BY place_id, name_id, tran_id")) {
                while (rset.next()) {
                    again = true;
                    data.add(rset.getString("place_id") + "|" + rset.getString("delete_id") + "|" + rset.getString("locale") + "|" + rset.getString("text") + "|" + rset.getInt("name_id") + "|" + rset.getInt("tran_id") + "|" + rset.getBoolean("delete_flag"));
                }
            } catch(SQLException ex) {
                System.out.println("Unable to do get place-names ... " + ex.getMessage());
            }

            Files.write(Paths.get(fileBase, fileName), data, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
            placeId += 500_000;
        }
    }
}
