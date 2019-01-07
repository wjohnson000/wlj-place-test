/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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
public class ZombiePlaceReps {

    private static final String UTF_8_ENCODING = "UTF-8";

    private static final String baseDir  = "C:/temp/db-dump";
    private static final String repFile  = "place-rep-all.txt";
    private static final String vNamFile = "variant-name-all.txt";
    private static final String dNamFile = "display-name-all.txt";

    static Set<String>  allReps = new HashSet<>();
    static Map<String, String>       repDetails = new TreeMap<>();
    static Map<String, String>       repToPlace = new HashMap<>();
    static Map<String, List<String>> displayNames = new HashMap<>();
    static Map<String, List<String>> variantNames = new HashMap<>();

    static Map<String, String> placeTypes = DumpTypes.loadPlaceTypes();
    static Map<String, String> nameTypes  = DumpTypes.loadNameTypes();

    public static void main(String... args) throws Exception {
        readAllReps();
        removeRepsWithDisplayName();
        if (allReps.isEmpty()) {
            System.out.println("\n\nNothing to process ... everything is OK !!");
        } else {
            readRepDetails();
            readDisplayNames();
            readPlaceNames();
            dumpAllStuff();
        }
    }

    static void readAllReps() throws IOException {
        processFile(repFile, "REP.read", 9, (line, chunks) -> {
            String repId    = chunks[0];
            String deleteId = chunks[9];
            if (deleteId.isEmpty()) {
                allReps.add(repId);
            }
        });
    }
    
    static void removeRepsWithDisplayName() throws IOException {
        processFile(dNamFile, "REP.NAME.read", 5, (line, chunks) -> {
            String repId   = chunks[0];
            String delFlag = chunks[5];
            if (! delFlag.toLowerCase().startsWith("t")) {
                allReps.remove(repId);
            }
        });
    }

    static void readRepDetails() throws IOException {
        processFile(repFile, "REP.read", 9, (line, chunks) -> {
            String repId   = chunks[0];
            String placeId = chunks[3];
            if (allReps.contains(repId)) {
                repDetails.put(repId, line);
                repToPlace.put(repId, placeId);
            }
        });
    }

    static void readDisplayNames() throws IOException {
        processFile(dNamFile, "REP.NAME.read", 5, (line, chunks) -> {
            String repId = chunks[0];
            if (allReps.contains(repId)) {
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
            if (repToPlace.containsValue(placeId)) {
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
                if (rowFields.length > minLen) {
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
}
