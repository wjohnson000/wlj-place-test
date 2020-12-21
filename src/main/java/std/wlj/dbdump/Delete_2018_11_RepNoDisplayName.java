/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class Delete_2018_11_RepNoDisplayName {

    static final String baseDir  = "C:/temp/db-dump";
    static final String repFile  = "place-rep-all.txt";
    static final String vNamFile = "variant-name-all.txt";
    static final String dNamFile = "display-name-all.txt";
    static final String sqlFileName = "upd-var-name-%04d.sql";

    static Set<String>  allReps = new HashSet<>();
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
        readAllReps();
        removeRepsWithDisplayName();
        readRepDetails();
        readDisplayNames();
        readPlaceNames();
        dumpAllStuff();
        generateSQL();
    }

    static void readAllReps() throws IOException {
        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("REP.read: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 9) {
                    String repId    = chunks[0];
                    String deleteId = chunks[9];
                    if (deleteId.isEmpty()) {
                        allReps.add(repId);
                    }
                }
            }
        }
    }
    
    static void removeRepsWithDisplayName() throws IOException {
        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, dNamFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("REP.NAME.read: " + lineCnt);

                String nameData = scan.nextLine();
                String[] chunks = PlaceHelper.split(nameData, '|');
                if (chunks.length > 5) {
                    String repId   = chunks[0];
                    String delFlag = chunks[5];
                    if (! delFlag.toLowerCase().startsWith("t")) {
                        allReps.remove(repId);
                    }
                }
            }
        }
    }

    static void readRepDetails() throws IOException {
        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("REP.read: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 9) {
                    String repId   = chunks[0];
                    String placeId = chunks[3];
                    if (allReps.contains(repId)) {
                        repDetails.put(repId, repData);
                        repToPlace.put(repId, placeId);
                    }
                }
            }
        }
    }

    static void readDisplayNames() throws IOException {
        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, dNamFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("REP.NAME.read: " + lineCnt);

                String nameData = scan.nextLine();
                String[] chunks = PlaceHelper.split(nameData, '|');
                if (chunks.length > 5) {
                    String repId = chunks[0];
                    if (allReps.contains(repId)) {
                        List<String> names = displayNames.get(repId);
                        if (names == null) {
                            names = new ArrayList<>();
                            displayNames.put(repId, names);
                        }
                        names.add(nameData);
                    }
                }
            }
        }
    }

    static void readPlaceNames() throws IOException {
        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, vNamFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("PLC.NAME.read: " + lineCnt);

                String nameData = scan.nextLine();
                String[] chunks = PlaceHelper.split(nameData, '|');
                if (chunks.length > 7) {
                    String placeId = chunks[0];
                    if (repToPlace.containsValue(placeId)) {
                        List<String> names = variantNames.get(placeId);
                        if (names == null) {
                            names = new ArrayList<>();
                            variantNames.put(placeId, names);
                        }
                        names.add(nameData);
                    }
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

    static void generateSQL() {
        Arrays.stream(beginSQL).forEach(sql -> sqlStuff.add(sql));

        for (String repData : repDetails.values()) {
            String[] chunks = PlaceHelper.split(repData, '|');
            if (chunks.length > 14) {
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
            }
        }

        Arrays.stream(endSQL).forEach(sql -> sqlStuff.add(sql));
        sqlStuff.forEach(System.out::println);
    }

    static String formatRepData(String line) {
        String[] chunks = PlaceHelper.split(line, '|');

        if (chunks.length > 7) {
            StringBuilder buff = new StringBuilder();
            buff.append("rep=").append(chunks[0]);
            buff.append("|parent=").append(chunks[2]);
            buff.append("|owner=").append(chunks[3]);
            buff.append("|type=").append(chunks[6]);
            buff.append("|type=").append(placeTypes.get(chunks[6]));
            buff.append("|pub=").append(chunks[11]);
            buff.append("|val=").append(chunks[12]);
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
            buff.append("|").append(chunks[5]);
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
            buff.append("|").append(chunks[7]);
            buff.append("|").append(chunks[5]);
            buff.append("|").append(nameTypes.get(chunks[5]));
            return buff.toString();
        } else {
            return "";
        }
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
