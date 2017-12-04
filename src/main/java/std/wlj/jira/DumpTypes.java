package std.wlj.jira;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import std.wlj.datasource.DbConnectionManager;

/**
 * Generate Place-rep details for:
 * <ul>
 *   <li>Unpublished places</li>
 *   <li>Places w/out latitude or longitude</li>
 * </ul>
 * @author wjohnson000
 *
 */
public class DumpTypes {

    public static void main(String... args) throws IOException {
        Map<String, String> placeTypes = loadPlaceTypes();
        placeTypes.entrySet().forEach(System.out::println);
   }

    static Map<String, String> loadPlaceTypes() {
        Map<String, String> placeTypes = new HashMap<>();

        String query =
            "SELECT ty.type_id, ty.code, tt.text " + 
            "  FROM type AS ty " + 
            "  JOIN type_term AS tt ON tt.type_id = ty.type_id " + 
            " WHERE ty.type_cat = 'PLACE' " + 
            "   AND tt.locale = 'en'";

        try(Connection conn = DbConnectionManager.getConnectionAws();
                Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(query)) {
            while (rset.next()) {
                String id   = rset.getString("type_id");
                String code = rset.getString("code");
                String text = rset.getString("text");
                placeTypes.put(id, code + " (" + text + ")");
            }
        } catch(SQLException ex) {
            System.out.println("Unable to do something ... " + ex.getMessage());
        }

        return placeTypes;
    }
}
