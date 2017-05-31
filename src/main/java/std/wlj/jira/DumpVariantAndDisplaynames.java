package std.wlj.jira;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.*;
import std.wlj.datasource.DbConnectionManager;

public class DumpVariantAndDisplaynames {
    public static void main(String... args) {
        try(Connection conn = DbConnectionManager.getConnectionAws()) {
            Files.write(Paths.get("C:/temp/place-name-all.txt"), Arrays.asList(""), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(Paths.get("C:/temp/display-name-all.txt"), Arrays.asList(""), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

//            dumpPlaceNames(conn);
            dumpDisplayNames(conn);
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
                        "SELECT place_id, name_id, tran_id, locale, text, delete_flag " +
                        "  FROM place_name AS nam " +
                        " WHERE place_id BETWEEN " + placeId + " AND " + (placeId + 499_999) +
                        "   AND tran_id = (SELECT MAX(tran_id) FROM place_name AS namx WHERE nam.name_id = namx.name_id) " +
                        " ORDER BY place_id, name_id, tran_id")) {
                while (rset.next()) {
                    again = true;
                    data.add(rset.getString("place_id") + "|" + rset.getString("locale") + "|" + rset.getString("text") + "|" + rset.getInt("name_id") + "|" + rset.getInt("tran_id") + "|" + rset.getBoolean("delete_flag"));
                }
            } catch(SQLException ex) {
                System.out.println("Unable to do get place-names ... " + ex.getMessage());
            }

            Files.write(Paths.get("C:/temp/place-name-all.txt"), data, StandardOpenOption.APPEND);
            placeId += 500_000;
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
