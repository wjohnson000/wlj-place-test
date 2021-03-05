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
 * Find Reps that have been deleted since Jan 01, 2021 (tranId = 18,502,209).
 * 
 * @author wjohnson000
 *
 */
public class FindDeletedRepsIn2021 {

    private static final String UTF_8_ENCODING = "UTF-8";

    private static final String baseDir  = "C:/temp/db-dump";
    private static final String repFile  = "place-rep-all.txt";
    private static final String dNamFile = "display-name-all.txt";

    static Set<String> delReps = new HashSet<>();
    static Map<String, String>       repDetails = new TreeMap<>();
    static Map<String, List<String>> displayNames = new HashMap<>();

    static Map<String, String> placeTypes = DumpTypes.loadPlaceTypes();

    public static void main(String... args) throws Exception {
        readAllReps();
        if (delReps.isEmpty()) {
            System.out.println("\n\nNothing to process ... everything is OK !!");
        } else {
            readRepDetails();
            readDisplayNames();
            dumpAllStuff();
        }
    }

    static void readAllReps() throws IOException {
        processFile(repFile, "REP.read", 9, (line, chunks) -> {
            String repId    = chunks[0];
            String tranId   = chunks[1];
            String deleteId = chunks[9];

            int iTranId = Integer.parseInt(tranId);
            if (! deleteId.isEmpty()  &&  iTranId > 18_502_209) {
                delReps.add(repId);
            } else if (! deleteId.isEmpty()  &&  iTranId > 15_891_7000) {
                System.out.println("ID: " + deleteId + " --> " + tranId + " --> " + iTranId);
            }
        });
    }

    static void readRepDetails() throws IOException {
        processFile(repFile, "REP.read", 9, (line, chunks) -> {
            String repId   = chunks[0];
            if (delReps.contains(repId)) {
                repDetails.put(repId, line);
            }
        });
    }

    static void readDisplayNames() throws IOException {
        processFile(dNamFile, "REP.NAME.read", 5, (line, chunks) -> {
            String repId = chunks[0];
            if (delReps.contains(repId)) {
                List<String> names = displayNames.get(repId);
                if (names == null) {
                    names = new ArrayList<>();
                    displayNames.put(repId, names);
                }
                names.add(line);
            }
        });
    }

    static void processFile(String filename, String message, int minLen, BiConsumer<String, String[]> consumer) throws IOException {
        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, filename));
                Scanner scan = new Scanner(fis, UTF_8_ENCODING)) {
            // Ignore the header line
            if (scan.hasNextLine()) {
                scan.nextLine();
            }

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
        }
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
            buff.append("|delId=").append(chunks[9]);
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
}
