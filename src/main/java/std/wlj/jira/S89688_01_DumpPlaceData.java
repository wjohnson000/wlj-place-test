package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.*;

import std.wlj.datasource.DbConnectionManager;

public class S89688_01_DumpPlaceData {

    static final String fileBase = "C:/temp/delete-by-type";
    static final String fileName = "s89688-place-data.txt";

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
                ResultSet rset = stmt.executeQuery(
                        "SELECT * " +
                        "  FROM place AS plc " +
                        " WHERE place_id BETWEEN " + placeId + " AND " + (placeId + 999_999) +
                        "   AND tran_id = (SELECT MAX(tran_id) FROM place AS plcx WHERE plc.place_id = plcx.place_id) " +
                        " ORDER BY place_id")) {
                while (rset.next()) {
                    again = true;
                    data.add(rset.getString("place_id") + "|" + rset.getString("tran_id") + "|" + rset.getString("from_year") + "|" + rset.getString("to_year") + "|" + rset.getString("delete_id"));
                }
            } catch(SQLException ex) {
                System.out.println("Unable to do get places ... " + ex.getMessage());
            }

            Files.write(Paths.get(fileBase, fileName), data, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            placeId += 1_000_000;
        }
    }
}
