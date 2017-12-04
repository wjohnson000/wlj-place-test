package std.wlj.jira;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.familysearch.standards.place.util.PlaceHelper;

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
public class Epic15320_01_GenDetails {

    static final String baseDir         = "C:/temp";
    static final String repFile         = "db-place-rep-all.txt";
    static final String unpublishedFile = "e15320-unpublished.txt";
    static final String noLatLongFile   = "e15320-no-lat-long.txt";

    public static void main(String... args) throws IOException {
        List<String> unpubData     = new ArrayList<>(100_000);
        List<String> noLatLongData = new ArrayList<>(100_000);

        Map<String, String> placeTypes = loadPlaceTypes();
        Set<String>         parentReps = loadParentReps();

        System.out.println();
        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("Lines read: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 11) {
                    String repId    = chunks[0];
                    String longtd   = chunks[4];
                    String lattd    = chunks[5];
                    String typeId   = chunks[6];
                    String deleteId = chunks[9];
                    String pubFlag  = chunks[11];

                    String typeData = placeTypes.getOrDefault(typeId, typeId);
                    boolean hasKids = parentReps.contains(repId);

                    if (deleteId == null  ||  deleteId.trim().isEmpty()  ||  deleteId.trim().equals("0")  ||  deleteId.trim().equals("null")) {
                        if (pubFlag == null  ||  pubFlag.trim().isEmpty()  ||  pubFlag.trim().toLowerCase().startsWith("f")) {
                            unpubData.add(repData + "|" + typeData + "|" + hasKids);
                        } else if (lattd == null  ||  lattd.trim().isEmpty()  ||  lattd.trim().equals("null")) {
                            noLatLongData.add(repData + "|" + typeData + "|" + hasKids);
                        } else if (longtd == null  ||  longtd.trim().isEmpty()  ||  longtd.trim().equals("null")) {
                            noLatLongData.add(repData + "|" + typeData + "|" + hasKids);
                        }
                    }
                }
            }
        }

        System.out.println();
        System.out.println("Un-pub size: " + unpubData.size());
        System.out.println("No-l/l size: " + noLatLongData.size());

        Files.write(Paths.get(baseDir, unpublishedFile), unpubData, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(Paths.get(baseDir, noLatLongFile), noLatLongData, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
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
    
    static Set<String> loadParentReps() throws FileNotFoundException, IOException {
        Set<String> parentReps = new HashSet<>();

        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("Lines read.init: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 11) {
                    parentReps.add(chunks[2]);
                }
            }
        }

        return parentReps;
    }
}
