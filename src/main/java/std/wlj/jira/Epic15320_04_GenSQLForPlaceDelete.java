package std.wlj.jira;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.familysearch.standards.place.util.PlaceHelper;

public class Epic15320_04_GenSQLForPlaceDelete {

    static final String baseDir         = "C:/temp";
    static final String repFile         = "db-place-rep-all.txt";
    static final String placeFile       = "db-place-all.txt";
    static final String placeDeleteFile = "e15320-place-delete.sql";

    static final String[] beginSQL = {
        "DO $$",
        "DECLARE",
        "  tranx_id INTEGER := NEXTVAL('transaction_tran_id_seq');",
        "BEGIN",
        "  INSERT INTO transaction(tran_id, create_ts, create_id) VALUES(tranx_id, now(), 'system-delete-place-reps');"
    };

    static final String[] endSQL = {
        "END $$;"    
    };

    static Map<String, String>       repToPlace    = new TreeMap<>();
    static Map<String, String>       repToParent   = new TreeMap<>();
    static Map<String, List<String>> placeToReps   = new TreeMap<>();
    static Map<String, String>       placeToParent = new TreeMap<>();
    static Map<String, String>       placeToDel    = new TreeMap<>();

    public static void main(String...args) throws IOException {
        step01RepToParent();
        System.out.println("\n>>>> SIZE: " + repToParent.size());
        System.out.println("     SIZE: " + placeToReps.size());
        System.out.println("     SIZE: " + placeToParent.size());
        System.out.println("     SIZE: " + placeToDel.size());

        step02PlaceToReps();
        System.out.println("\n>>>> SIZE: " + repToParent.size());
        System.out.println("     SIZE: " + placeToParent.size());
        System.out.println("     SIZE: " + placeToReps.size());
        System.out.println("     SIZE: " + placeToDel.size());

        step03PlaceToRepParentPlace();
        System.out.println("\n>>>> SIZE: " + repToParent.size());
        System.out.println("     SIZE: " + placeToReps.size());
        System.out.println("     SIZE: " + placeToReps.size());
        System.out.println("     SIZE: " + placeToDel.size());

        step04GenerateSQL();

        System.exit(0);
    }

    static void step01RepToParent() throws IOException {
        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("R.Lines.01: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 11) {
                    String repId    = chunks[0];
                    String parentId = chunks[2];
                    String ownerId  = chunks[3];
                    String deleteId = chunks[9];
                    String pubFlag  = chunks[11];

                    if (deleteId == null  ||  deleteId.trim().isEmpty()  ||  deleteId.trim().equals("0")  ||  deleteId.trim().equals("null")) {
                        if (pubFlag == null  ||  pubFlag.trim().isEmpty()  ||  pubFlag.trim().toLowerCase().startsWith("f")) {
                            repToParent.put(repId, parentId);
                            placeToReps.put(ownerId, new ArrayList<>());
                            repToPlace.put(repId, "0");
                            repToPlace.put(parentId, "0");
                        }
                    }
                }
            }
        }
    }

    static void step02PlaceToReps() throws IOException {
        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("R.Lines.02: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 11) {
                    String repId    = chunks[0];
                    String ownerId  = chunks[3];
                    String deleteId = chunks[9];

                    if (repToPlace.containsKey(repId)) {
                        repToPlace.put(repId, ownerId);
                    }

                    if (deleteId == null  ||  deleteId.trim().isEmpty()  ||  deleteId.trim().equals("0")  ||  deleteId.trim().equals("null")) {
                        if (placeToReps.containsKey(ownerId)) {
                            placeToReps.get(ownerId).add(repId);
                            if (repToParent.containsKey(repId)  &&  ! placeToParent.containsKey(ownerId)) {
                                placeToParent.put(ownerId, repToParent.get(repId));
                            }
                        }
                    }
                }
            }
        }
    }

    static void step03PlaceToRepParentPlace() throws FileNotFoundException, IOException {
        placeToParent.entrySet().forEach(entry -> {
            String placeId  = entry.getKey();
            String repParId = entry.getValue();
            String parOwnerId = repToPlace.get(repParId);
            if (parOwnerId != null  &&  ! "0".equals(parOwnerId)) {
                placeToDel.put(placeId, parOwnerId);
            }
        });
    }

    static void step04GenerateSQL() throws IOException {
        List<String> sqlStuff = new ArrayList<>(placeToDel.size());
        Arrays.stream(beginSQL).forEach(sql -> sqlStuff.add(sql));

        int lineCnt = 0;
        int sampleCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, placeFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("P.Lines.03: " + lineCnt);
                
                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 4) {
                    String placeId = chunks[0];
                    String delId   = placeToDel.get(placeId);
                    if (delId != null) {
                        sqlStuff.add(updatePlaceSQL(chunks, delId));
                        if (++sampleCnt < 12) {
                            System.out.println(placeId + "|" + delId);
                        }
                    }
                }
            }
        }
        Arrays.stream(endSQL).forEach(sql -> sqlStuff.add(sql));

        Files.write(Paths.get(baseDir, placeDeleteFile), sqlStuff, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Generate the SQL that will update the place to version it.
     * 
     * @param data Array with all of the existing data fields ... change nothing
     *        but the "delete_id" and the "tran_id"
     * @param deleteId the new deleteId
     */
    static String updatePlaceSQL(String[] data, String deleteId) {
        if (data.length < 5) {
            System.out.println("Too few fields!! " + Arrays.toString(data));
            return "";
        } else if (! "null".equals(data[4])) {
            System.out.println("Already deleted!! " + Arrays.toString(data));
            return "";
        }

        StringBuilder buff = new StringBuilder();

        buff.append("  INSERT INTO place(place_id, tran_id, from_year, to_year, delete_id) ");
        buff.append("VALUES (").append(numericData(data[0]));
        buff.append(", ").append("tranx_id");
        buff.append(", ").append(numericData(data[2]));
        buff.append(", ").append(numericData(data[3]));
        buff.append(", ").append(numericData(deleteId));
        buff.append(");");

        return buff.toString();
    }

    static String characterData(String datum) {
        if ("null".equals(datum)) {
            return datum;
        } else {
            return "'" + datum + "'";
        }
    }

    static String numericData(String datum) {
        return datum;
    }

    static String booleanData(String datum) {
        return (datum.toLowerCase().startsWith("t")) ? "TRUE" : "FALSE";
    }
}
