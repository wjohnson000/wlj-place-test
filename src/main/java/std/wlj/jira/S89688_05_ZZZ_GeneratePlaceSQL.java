package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.familysearch.standards.place.util.PlaceHelper;

public class S89688_05_ZZZ_GeneratePlaceSQL {

    static final class RepDataTinyZZZ {
        int     repId;
        int     parId;
        int     parPlaceId;
        boolean deleteMe = false;

        public RepDataTinyZZZ(int repId, int parId) {
            this.repId = repId;
            this.parId = parId;
        }

        @Override public String toString() { return repId + " . " + parId + " . " + parPlaceId + " . " + deleteMe; }
    }

    static final int    stmtLimit           = 50_000;
    static final String fileBase            = "C:/temp/delete-by-type";
    static final String inPlaceFileName     = "s89688-place-data.txt";
    static final String inRepParentFileName = "s89688-rep-data-parent.txt";
    static final String inRepDataFileName   = "s89688-rep-data-all.txt";
    static final String sqlFileName         = "s89688-place-%04d.sql";

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

    public static void main(String...args) throws IOException {
        Map<Integer, List<RepDataTinyZZZ>> placeToRep = getPlaceToRep();
        System.out.println("\n>>>> SIZE: " + placeToRep.size());
        placeToRep.entrySet().stream()
            .limit(100)
            .forEach(System.out::println);
        System.out.println(".......... " + placeToRep.get(5315980));

        removeDeletedPlaces(placeToRep);
        System.out.println("\n>>>> SIZE: " + placeToRep.size());
        placeToRep.entrySet().stream()
            .limit(100)
            .forEach(System.out::println);
        System.out.println(".......... " + placeToRep.get(5315980));

        markRepsToDelete(placeToRep);
        System.out.println("\n>>>> SIZE: " + placeToRep.size());
        placeToRep.entrySet().stream()
            .limit(100)
            .forEach(System.out::println);
        System.out.println(".......... " + placeToRep.get(5315980));

        setParentPlaceId(placeToRep);
        System.out.println("\n>>>> SIZE: " + placeToRep.size());
        placeToRep.entrySet().stream()
            .limit(100)
            .forEach(System.out::println);
        System.out.println(".......... " + placeToRep.get(5315980));

        generatePlaceUpdateSQL(placeToRep);

        System.exit(0);
    }

    static Map<Integer, List<RepDataTinyZZZ>> getPlaceToRep() throws IOException {
      List<String> allLines = Files.readAllLines(Paths.get(fileBase, inRepParentFileName), Charset.forName("UTF-8"));
      System.out.println(">> getPlaceToRep <<  Line count: " + allLines.size());

      return allLines.stream()
          .filter(line -> line.endsWith("null"))
          .map(line -> PlaceHelper.split(line, '|'))
          .filter(data -> data.length > 3)
          .map(data -> new String[] { data[0], ("null".equals(data[1]) ? "0" : data[1]), data[2] })
          .map(data -> new int[] { Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]) })
          .collect(Collectors.groupingBy(
              data -> data[2],
              HashMap::new,
              Collectors.mapping(data -> new RepDataTinyZZZ(data[0], data[1]), Collectors.toList())));
    }

    static void removeDeletedPlaces(Map<Integer, List<RepDataTinyZZZ>> placeToRep) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(fileBase, inPlaceFileName), Charset.forName("UTF-8"));
        System.out.println(">> removeDeletedPlaces <<  Line count: " + allLines.size());

        allLines.stream()
            .filter(line -> ! line.endsWith("null"))
            .map(line -> PlaceHelper.split(line, '|'))
            .filter(data -> data.length > 4)
            .map(data -> Integer.parseInt(data[0]))
            .forEach(placeId -> placeToRep.remove(placeId));
    }

    static void markRepsToDelete(Map<Integer, List<RepDataTinyZZZ>> placeToRep) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(fileBase, inRepDataFileName), Charset.forName("UTF-8"));
        System.out.println(">> markRepsToDelete <<  Line count: " + allLines.size());

        // Step '0' -- remove any place that is NOT associated with a rep to be deleted
        System.out.println("   CountB: " + placeToRep.size());
        Set<Integer> idsToKeep = allLines.stream()
            .map(line -> PlaceHelper.split(line, '|'))
            .filter(data -> data.length > 4)
            .map(data -> Integer.parseInt(data[3]))
            .collect(Collectors.toSet());

        Set<Integer> idsToDelete = new HashSet<>(placeToRep.keySet());
        idsToDelete.removeAll(idsToKeep);
        idsToDelete.forEach(key -> placeToRep.remove(key));

        // Step '1' -- flag "to-be-deleted" reps as deleted
        System.out.println("   CountA: " + placeToRep.size());
        for (String line : allLines) {
            String[] data = PlaceHelper.split(line, '|');
            if (data.length > 4) {
                int repId = Integer.parseInt(data[0]);
                int parId = ("null".equals(data[2])) ? 0 : Integer.parseInt(data[2]);
                int plcId = Integer.parseInt(data[3]);

                List<RepDataTinyZZZ> repTs = placeToRep.get(plcId);
                if (repTs != null) {
                    boolean found = false;
                    for (RepDataTinyZZZ repT : repTs) {
                        if (repT.repId == repId) {
                            found = true;
                            repT.deleteMe = true;
                            if (repT.parId != parId) {
                                System.out.println("Oops!! -- mismatched place for: " + repT + " and " + parId);
                            }
                        }
                    }
                    if (! found) {
                        System.out.println("Oops!! -- not found for: " + repId);
                    }
                }
            }
        }
    }

    static void setParentPlaceId(Map<Integer, List<RepDataTinyZZZ>> placeToRep) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(fileBase, inRepParentFileName), Charset.forName("UTF-8"));
        System.out.println(">> setParentPlaceId <<  Line count: " + allLines.size());

        Map<Integer, Integer> repOwner = allLines.stream()
            .map(line -> PlaceHelper.split(line, '|'))
            .filter(data -> data.length > 2)
            .collect(Collectors.toMap(data -> Integer.parseInt(data[0]), data -> Integer.parseInt(data[2])));

        for (List<RepDataTinyZZZ> repTs : placeToRep.values()) {
            boolean deleteAll = repTs.stream().allMatch(repT -> repT.deleteMe);
            if (deleteAll) {
                Set<Integer> parPlaceIds = repTs.stream()
                    .map(repT -> repT.parId)
                    .map(parId -> repOwner.get(parId))
                    .filter(parPlaceId -> parPlaceId != null)
                    .collect(Collectors.toSet());
                if (parPlaceIds.size() == 0) {
                    System.out.println("No owner?: " + repTs);
//                } else if (parPlaceIds.size() == 1) {
//                    repTs.forEach(repT -> repT.parPlaceId = parPlaceIds.stream().findFirst().orElse(0));
                } else {
                    repTs.forEach(repT -> repT.parPlaceId = parPlaceIds.stream().findFirst().orElse(0));
                }
            } else {
                System.out.println("Don't delete this one: " + repTs);
            }
        }
    }

    static void generatePlaceUpdateSQL(Map<Integer, List<RepDataTinyZZZ>> placeToRep) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(fileBase, inPlaceFileName), Charset.forName("UTF-8"));
        System.out.println(">> generatePlaceUpdateSQL <<  Line count: " + allLines.size());

        int fileCount = 1;
        List<String> sqlStuff = new ArrayList<>(stmtLimit);
        Arrays.stream(beginSQL).forEach(sql -> sqlStuff.add(sql));

        for (String line : allLines) {
            String[] data = PlaceHelper.split(line, '|');
            try {
                int placeId = Integer.parseInt(data[0]);
                List<RepDataTinyZZZ> repTs = placeToRep.get(placeId);
                if (repTs != null) {
                    RepDataTinyZZZ repT = repTs.stream().findFirst().orElse(null);
                    int deleteId = (repT == null) ? 0 : repT.parPlaceId;
                    if (deleteId > 0) {
                        sqlStuff.add(updatePlaceSQL(data, deleteId));
                        if (sqlStuff.size() == stmtLimit) {
                            Arrays.stream(endSQL).forEach(sql -> sqlStuff.add(sql));
                            generateSqlFile(fileCount, sqlStuff);
                            fileCount++;
                            sqlStuff.clear();
                            Arrays.stream(beginSQL).forEach(sql -> sqlStuff.add(sql));
                        }
                    }
                }
            } catch(NumberFormatException ex) {
                // Do nothing ...
            }
        }

        Arrays.stream(endSQL).forEach(sql -> sqlStuff.add(sql));
        generateSqlFile(fileCount, sqlStuff);
    }

    /**
     * Generate the SQL that will update the place to version it.
     * 
     * @param data Array with all of the existing data fields ... change nothing
     *        but the "delete_id" and the "tran_id"
     * @param deleteId the new deleteId
     */
    static String updatePlaceSQL(String[] data, int deleteId) {
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
        buff.append(", ").append(numericData(String.valueOf(deleteId)));
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

    static void generateSqlFile(int fileCount, List<String> sqlStuff) throws IOException {
        String fileName = String.format(sqlFileName, fileCount);
        System.out.println("Saving file: " + fileName);
        Files.write(Paths.get(fileBase, fileName), sqlStuff, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
