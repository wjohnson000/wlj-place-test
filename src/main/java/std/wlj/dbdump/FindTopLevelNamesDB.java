/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import std.wlj.util.DbConnectionManager;

/**
 * Find the most common DISPLAY names.
 * 
 * @author wjohnson000
 *
 */
public class FindTopLevelNamesDB {

    public static void main(String...args) throws Exception {
        try(Connection conn = DbConnectionManager.getConnectionAws()) {
            Map<String, String> types = loadPlaceTypes(conn);
            Map<String, Map<String, String>> reps = loadTopLevelPlaces(conn);
            Map<String, String> names = loadDisplayNames(conn, reps.keySet());

            for (Map<String, String> repDatum : reps.values()) {
                String repId  = repDatum.get("repid");
                String typeId = repDatum.get("typeid");
                String info = repId +
                        "|" + names.getOrDefault(repId, "Unknown") +
                        "|" + typeId +
                        "|" + types.get(typeId) +
                        "|" + repDatum.get("ispub").toUpperCase() +
                        "|" + repDatum.get("isval").toUpperCase();
                System.out.println(info);
            }
        } catch(SQLException ex) {
            
        };
    }

    static Map<String, String> loadPlaceTypes(Connection conn) {
        Map<String, String> placeTypes = new HashMap<>();

        String query =
            "SELECT ty.type_id, ty.code, tt.text " + 
            "  FROM type AS ty " + 
            "  JOIN type_term AS tt ON tt.type_id = ty.type_id " + 
            " WHERE ty.type_cat = 'PLACE' " + 
            "   AND tt.locale = 'en'";

        try(Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(query)) {
            while (rset.next()) {
                String id   = rset.getString("type_id");
                String code = rset.getString("code");
                String text = rset.getString("text");
                placeTypes.put(id, code + " (" + text + ")");
            }
        } catch(SQLException ex) {
            System.out.println("Unable to do something ... 1: " + ex.getMessage());
        }

        return placeTypes;
    }
    
    static Map<String, Map<String, String>> loadTopLevelPlaces(Connection conn) {
        Map<String, Map<String, String>> repData = new LinkedHashMap<>();

        String query =
            "SELECT rep.* " + 
            "  FROM place_rep AS rep " + 
            " WHERE rep.tran_id = (SELECT MAX(tran_id) FROM place_rep AS repx WHERE repx.rep_id = rep.rep_id) " +
            "   AND (rep.parent_id IS NULL  OR  rep.parent_id < 1) " +
            "   AND (rep.delete_id IS NULL  OR  rep.delete_id = 0) " +
            " ORDER BY rep.rep_id";

        try(Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(query)) {
            while (rset.next()) {
                Map<String, String> repDatum = new HashMap<>();
                repDatum.put("repid", rset.getString("rep_id"));
                repDatum.put("typeid", rset.getString("place_type_id"));
                repDatum.put("locale", rset.getString("pref_locale"));
                repDatum.put("ispub", rset.getString("pub_flag"));
                repDatum.put("isval", rset.getString("validated_flag"));
                repData.put(rset.getString("rep_id"), repDatum);
            }
        } catch(SQLException ex) {
            System.out.println("Unable to do something ... 2: " + ex.getMessage());
        }

        return repData;
    }

    static Map<String, String> loadDisplayNames(Connection conn, Set<String> keySet) {
        Map<String, String> names     = new HashMap<>();
        Map<String, String> namesLatn = new HashMap<>();
        Map<String, String> namesBlah = new HashMap<>();

        String repIds = keySet.stream().collect(Collectors.joining(",", "(", ")"));

        String query =
            "SELECT * " +
            "  FROM rep_display_name " +
            " WHERE rep_id IN " + repIds +
            "   AND delete_flag = FALSE";

        try(Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(query)) {
            while (rset.next()) {
                String repId = rset.getString("rep_id");
                String locale = rset.getString("locale");
                String text   = rset.getString("text");
                namesBlah.put(repId, text);
                if (locale.equals("en")) {
                    names.put(repId, text);
                }
                if (locale.contains("Latn")) {
                    namesLatn.put(repId, text);
                }
            }
        } catch(SQLException ex) {
            System.out.println("Unable to do something ... 3: " + ex.getMessage());
        }

        namesLatn.entrySet().stream().forEach(kv -> names.putIfAbsent(kv.getKey(), kv.getValue()));
        namesBlah.entrySet().stream().forEach(kv -> names.putIfAbsent(kv.getKey(), kv.getValue()));

        return names;
    }

}
