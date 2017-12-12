package std.wlj.dbdump;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.*;
//import java.util.stream.Collectors;

import std.wlj.datasource.DbConnectionManager;

public class DumpPlaceReps {

    static final String fileBase = "C:/temp";
    static final String fileName = "db-place-rep-all.txt";

//    static int[] placeTypeInt = {
//          1,    2,    3,    5,    6,   10,   11,   12,   13,   14,   15,   17,   18,
//         19,   21,   22,   24,   32,   37,   39,   40,   42,   44,   46,   47,   48,
//         50,   51,   53,   54,   55,   59,   63,   64,   68,   69,   70,   74,   75,
//         76,   79,   83,   84,   85,   86,   87,   88,   89,   90,   92,   97,  102,
//        103,  104,  107,  108,  109,  110,  111,  112,  114,  115,  116,  118,  120,
//        121,  124,  125,  126,  128,  131,  133,  135,
//    };
//
//    static Set<Integer> placeTypeSet = Arrays.stream(placeTypeInt)
//        .mapToObj(pt -> Integer.valueOf(pt))
//        .collect(Collectors.toSet());

    public static void main(String... args) {
        try(Connection conn = DbConnectionManager.getConnectionAws()) {
            Files.write(Paths.get(fileBase, fileName),  Arrays.asList(""), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
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
            List<String> verboseData  = new ArrayList<>();
            try(Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(
                        "SELECT * " +
                        "  FROM place_rep AS rep " +
                        " WHERE rep.rep_id BETWEEN " + repId + " AND " + (repId + 999_999) +
                        "   AND rep.tran_id = (SELECT MAX(tran_id) FROM place_rep AS repx WHERE rep.rep_id = repx.rep_id) " +
                        " ORDER BY rep.rep_id ")) {
                while (rset.next()) {
                    again = true;
                    verboseData.add(rset.getString("rep_id") + "|" + rset.getString("tran_id") + "|" + rset.getString("parent_id") + "|" + rset.getString("owner_id") +
                            "|" + rset.getString("centroid_long") + "|" + rset.getString("centroid_lattd") + "|" + rset.getString("place_type_id") +
                            "|" + rset.getString("parent_from_year") + "|" + rset.getString("parent_to_year") + "|" + rset.getString("delete_id") +
                            "|" + rset.getString("pref_locale") + "|" + rset.getString("pub_flag") + "|" + rset.getString("validated_flag") +
                            "|" + rset.getString("uuid") + "|" + rset.getString("group_id") + "|" + rset.getString("pref_boundary_id"));
                }
            } catch(SQLException ex) {
                System.out.println("Unable to do get rep-data ... " + ex.getMessage());
            }

            Files.write(Paths.get(fileBase, fileName),  verboseData,  Charset.forName("UTF-8"), StandardOpenOption.APPEND);
            repId += 1_000_000;
        }
    }
}
