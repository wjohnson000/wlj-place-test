/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiConsumer;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * Find a list of all "Zombie" place-reps.  These are reps which are alive as far as the database is
 * concerned (i.e., not deleted), but have either no display names or no variant names.  Thus they
 * are NOT brought into SOLR.
 * <p/>
 * NOTE: these were cleaned up in November, 2018, hence the result should always be an empty list.
 * 
 * @author wjohnson000
 *
 */
public class ZombiePlaceRepsByPlaceName {

    private static final String UTF_8_ENCODING = "UTF-8";

    private static final String baseDir  = "C:/temp/db-dump";
    private static final String plcFile  = "place-all.txt";
    private static final String repFile  = "place-rep-all.txt";
    private static final String vNamFile = "variant-name-all.txt";
    private static final String dNamFile = "display-name-all.txt";
    private static final String sqlFileName = "del-rep-%04d.sql";

    static Set<String>               allPlaces = new HashSet<>();
    static Map<String, String>       repDetails = new TreeMap<>();
    static Map<String, String>       repToPlace = new HashMap<>();
    static Map<String, List<String>> displayNames = new HashMap<>();
    static Map<String, List<String>> variantNames = new HashMap<>();

    static Map<String, String> placeTypes = DumpTypes.loadPlaceTypes();
    static Map<String, String> nameTypes  = DumpTypes.loadNameTypes();

    static List<String> sqlStuff = new ArrayList<>();
    static String INSERT_SQL =
        "  INSERT INTO place_rep(rep_id, tran_id, parent_id, owner_id, centroid_long, centroid_lattd," +
                               " place_type_id, parent_from_year, parent_to_year, delete_id, " +
                               " pref_locale, pub_flag, validated_flag, uuid, group_id, pref_boundary_id)" +
        " VALUES(%s, tranx_id, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s);";

    static final String[] beginSQL = {
        "DO $$",
        "DECLARE",
        "  tranx_id INTEGER := NEXTVAL('transaction_tran_id_seq');",
        "BEGIN",
        "  INSERT INTO transaction(tran_id, create_ts, create_id) VALUES(tranx_id, now(), 'system-silly-reps');",
        ""
    };

    static final String[] endSQL = {
        "END $$;"    
    };

    public static void main(String... args) throws Exception {
        readAllPlaces();
        removePlacesWithVariantName();
        if (allPlaces.isEmpty()) {
            System.out.println("\n\nNothing to process ... everything is OK !!");
        } else {
            readRepDetails();
            readDisplayNames();
            readPlaceNames();
            dumpAllStuff();
            generateSQL();
        }
    }

    static void readAllPlaces() throws IOException {
        processFile(plcFile, "PLC.read", 5, (line, chunks) -> {
            String placeId  = chunks[0];
            String deleteId = chunks[4];
            if (deleteId.isEmpty()) {
                allPlaces.add(placeId);
            }
        });
    }

    static void removePlacesWithVariantName() throws IOException {
        processFile(vNamFile, "PLC.NAME.read", 7, (line, chunks) -> {
            String placeId = chunks[0];
            String delFlag = chunks[7];
            if (! delFlag.toLowerCase().startsWith("t")) {
                allPlaces.remove(placeId);
            }
        });
    }

    static void readRepDetails() throws IOException {
        processFile(repFile, "REP.read", 9, (line, chunks) -> {
            String repId    = chunks[0];
            String placeId  = chunks[3];
            if (allPlaces.contains(placeId)) {
                repDetails.put(repId, line);
                repToPlace.put(repId, placeId);
            }
        });
    }

    static void readDisplayNames() throws IOException {
        processFile(dNamFile, "REP.NAME.read", 5, (line, chunks) -> {
            String repId = chunks[0];
            if (repToPlace.containsKey(repId)) {
                List<String> names = displayNames.get(repId);
                if (names == null) {
                    names = new ArrayList<>();
                    displayNames.put(repId, names);
                }
                names.add(line);
            }
        });
    }

    static void readPlaceNames() throws IOException {
        processFile(vNamFile, "PLC.NAME.read", 7, (line, chunks) -> {
            String placeId = chunks[0];
            if (allPlaces.contains(placeId)) {
                List<String> names = variantNames.get(placeId);
                if (names == null) {
                    names = new ArrayList<>();
                    variantNames.put(placeId, names);
                }
                names.add(line);
            }
        });
    }

    static void processFile(String filename, String message, int minLen, BiConsumer<String, String[]> consumer) throws IOException {
        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, filename));
                Scanner scan = new Scanner(fis, UTF_8_ENCODING)) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println(message + ": " + lineCnt);

                String   rowData   = scan.nextLine();
                String[] rowFields = PlaceHelper.split(rowData, '|');
                if (rowFields.length >= minLen) {
                    consumer.accept(rowData, rowFields);
                }
            }
        }
    }

    static void dumpAllStuff() {
        for (Map.Entry<String, String> entry : repDetails.entrySet()) {
            System.out.println();
            System.out.println(formatRepData(entry.getValue()));

            List<String> dispNames = displayNames.get(entry.getKey());
            if (dispNames != null) {
                dispNames.forEach(dispName -> System.out.println(formatDisplayName(dispName)));
            }

            List<String> varNames = variantNames.get(repToPlace.get(entry.getKey()));
            if (varNames != null) {
                varNames.forEach(varName -> System.out.println(formatVariantName(varName)));
            }
        }
    }

    static void generateSQL() throws IOException {
        Arrays.stream(beginSQL).forEach(sql -> sqlStuff.add(sql));

        int fileCount = 1;

        for (String repData : repDetails.values()) {
            String[] chunks = PlaceHelper.split(repData, '|');
            String   deleteId = chunks[9];
            if (chunks.length > 14  &&  deleteId.isEmpty()) {
                sqlStuff.add(String.format(INSERT_SQL,
                    numericData(chunks[0]),
                    numericData(chunks[2]),
                    numericData(chunks[3]),
                    numericData(chunks[4]),
                    numericData(chunks[5]),
                    numericData(chunks[6]),
                    numericData(chunks[7]),
                    numericData(chunks[8]),
                    numericData(chunks[2]),  // Use the parent as the "delete_id" value
                    characterData(chunks[10]),
                    booleanData(chunks[11]),
                    booleanData(chunks[12]),
                    characterData(chunks[13]),
                    numericData(chunks[14]),
                    numericData(chunks[15])));

                if (sqlStuff.size() == 50_000) {
                    Arrays.stream(endSQL).forEach(sql -> sqlStuff.add(sql));
                    generateSqlFile(fileCount, sqlStuff);

                    fileCount++;
                    sqlStuff.clear();
                    Arrays.stream(beginSQL).forEach(sql -> sqlStuff.add(sql));
                }
            }
        }

        Arrays.stream(endSQL).forEach(sql -> sqlStuff.add(sql));
        generateSqlFile(fileCount, sqlStuff);
    }

    static String formatRepData(String line) {
        String[] chunks = PlaceHelper.split(line, '|');

        if (chunks.length > 12) {
            StringBuilder buff = new StringBuilder();
            buff.append("rep=").append(chunks[0]);
            buff.append("|parent=").append(chunks[2]);
            buff.append("|owner=").append(chunks[3]);
            buff.append("|type=").append(chunks[6]);
            buff.append("|type=").append(placeTypes.get(chunks[6]));
            buff.append("|pub=").append(chunks[11]);
            buff.append("|val=").append(chunks[12]);
            buff.append("|deleteid=").append(chunks[9]);
            return buff.toString();
        } else {
            return "";
        }
    }

    static String formatDisplayName(String line) {
        String[] chunks = PlaceHelper.split(line, '|');

        if (chunks.length > 5) {
            StringBuilder buff = new StringBuilder();
            buff.append("|disp");
            buff.append("|").append(chunks[2]);
            buff.append("|").append(chunks[3]);
            return buff.toString();
        } else {
            return "";
        }
    }

    static String formatVariantName(String line) {
        String[] chunks = PlaceHelper.split(line, '|');

        if (chunks.length > 7) {
            StringBuilder buff = new StringBuilder();
            buff.append("|var");
            buff.append("|").append(chunks[2]);
            buff.append("|").append(chunks[3]);
            buff.append("|").append(chunks[5]);
            buff.append("|").append(nameTypes.get(chunks[5]));
            return buff.toString();
        } else {
            return "";
        }
    }

    static void generateSqlFile(int fileCount, List<String> sqlStuff) throws IOException {
        String fileName = String.format(sqlFileName, fileCount);
        System.out.println("Saving file: " + fileName);
        Files.write(Paths.get(baseDir, fileName), sqlStuff, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static String characterData(String datum) {
        if ("null".equals(datum)) {
            return datum;
        } else {
            return "'" + datum.replace("'", "''") + "'";
        }
    }

    static String numericData(String datum) {
        if (datum.isEmpty()) {
            return "null";
        } else {
            return datum;
        }
    }

    static String booleanData(String datum) {
        return (datum.toLowerCase().startsWith("t")) ? "TRUE" : "FALSE";
    }
}
