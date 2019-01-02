package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.familysearch.standards.place.util.PlaceHelper;

import std.wlj.datasource.DbConnectionManager;

public class S89688_03_DeleteRepStats {

    static final String fileBase = "C:/temp/delete-by-type";
    static final String fileName  = "s89688-rep-data-all.txt";

    public static void main(String...args) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(fileBase, fileName), StandardCharsets.UTF_8);
        System.out.println("Reps: " + allLines.size());

        Map<Integer, String> typeDetails = getTypeData();
        Map<Integer, Integer> countByType = new TreeMap<>();
        for (String line : allLines) {
            String[] data = PlaceHelper.split(line, '|');
            if (data.length > 12) {
                int type = Integer.parseInt(data[6]);
                Integer count = countByType.get(type);
                count = (count == null) ? 0 : count + 1;
                countByType.put(type, count);
            }
        }

        countByType.entrySet().forEach(ee -> System.out.println(ee.getKey() + "|" + typeDetails.get(ee.getKey()) + "|" + ee.getValue()));
        System.exit(0);
    }

    static Map<Integer, String> getTypeData() {
        String query =
            "SELECT type.type_id, type.code, term.text " + 
            "  FROM type AS type " + 
            "  JOIN type_term as term on term.type_id = type.type_id " + 
            " WHERE type.type_cat = 'PLACE' " + 
            "   AND term.locale = 'en'";

        try(Connection conn = DbConnectionManager.getConnectionAws();
            Statement  stmt = conn.createStatement();
            ResultSet  rset = stmt.executeQuery(query)) {
            Map<Integer, String> results = new TreeMap<>();
            while (rset.next()) {
                int    typeId = rset.getInt("type_id");
                String code   = rset.getString("code");
                String term   = rset.getString("text");
                results.put(typeId, code + "." + term);
            }
            return results;
        } catch(SQLException ex) {
            System.out.println("Unable to do something ... " + ex.getMessage());
            return new TreeMap<>();
        }
    }
}
