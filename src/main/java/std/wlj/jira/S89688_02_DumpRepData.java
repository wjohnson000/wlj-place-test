package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import std.wlj.util.DbConnectionManager;

public class S89688_02_DumpRepData {

    static final String fileBase         = "C:/temp/delete-by-type";
    static final String fileNameVerbose  = "s89688-rep-data-all.txt";
    static final String fileNameParChild = "s89688-rep-data-parent.txt";

    static int[] placeTypeInt = {
          1,    2,    3,    5,    6,   10,   11,   12,   13,   14,   15,   17,   18,
         19,   21,   22,   24,   32,   37,   39,   40,   42,   44,   46,   47,   48,
         50,   51,   53,   54,   55,   59,   63,   64,   68,   69,   70,   74,   75,
         76,   79,   83,   84,   85,   86,   87,   88,   89,   90,   92,   97,  102,
        103,  104,  107,  108,  109,  110,  111,  112,  114,  115,  116,  118,  120,
        121,  124,  125,  126,  128,  131,  133,  135,
    };

    static Set<Integer> placeTypeSet = Arrays.stream(placeTypeInt)
        .mapToObj(pt -> Integer.valueOf(pt))
        .collect(Collectors.toSet());

    public static void main(String... args) {
        try(Connection conn = DbConnectionManager.getConnectionAws()) {
            Files.write(Paths.get(fileBase, fileNameVerbose),  Arrays.asList(""), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.write(Paths.get(fileBase, fileNameParChild), Arrays.asList(""), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
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
            List<String> parChildData = new ArrayList<>();
            try(Statement stmt = conn.createStatement();
                ResultSet rset = stmt.executeQuery(
                        "SELECT * " +
                        "  FROM place_rep AS rep " +
                        " WHERE rep.rep_id BETWEEN " + repId + " AND " + (repId + 999_999) +
                        "   AND rep.tran_id = (SELECT MAX(tran_id) FROM place_rep AS repx WHERE rep.rep_id = repx.rep_id) " +
                        " ORDER BY rep.rep_id ")) {
                while (rset.next()) {
                    again = true;

                    parChildData.add(rset.getString("rep_id") + "|" + rset.getString("parent_id") + "|" + rset.getString("owner_id") + "|" + rset.getString("delete_id"));

                    int placeTypeId = rset.getInt("place_type_id");
                    int deleteId    = rset.getInt("delete_id");
                    boolean isPub   = rset.getBoolean("pub_flag");
                    if (placeTypeSet.contains(placeTypeId)  &&  deleteId <= 0  &&  ! isPub) {
                        verboseData.add(rset.getString("rep_id") + "|" + rset.getString("tran_id") + "|" + rset.getString("parent_id") + "|" + rset.getString("owner_id") +
                                "|" + rset.getString("centroid_long") + "|" + rset.getString("centroid_lattd") + "|" + rset.getString("place_type_id") +
                                "|" + rset.getString("parent_from_year") + "|" + rset.getString("parent_to_year") + "|" + rset.getString("delete_id") +
                                "|" + rset.getString("pref_locale") + "|" + rset.getString("pub_flag") + "|" + rset.getString("validated_flag") +
                                "|" + rset.getString("uuid") + "|" + rset.getString("group_id") + "|" + rset.getString("pref_boundary_id"));
                    }
                }
            } catch(SQLException ex) {
                System.out.println("Unable to do get rep-data ... " + ex.getMessage());
            }

            Files.write(Paths.get(fileBase, fileNameVerbose),  verboseData,  StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            Files.write(Paths.get(fileBase, fileNameParChild), parChildData, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
            repId += 1_000_000;
        }
    }
}
