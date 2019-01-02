package std.wlj.jira;

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
import java.util.Set;
import java.util.stream.Collectors;

import org.familysearch.standards.place.util.PlaceHelper;

public class S89688_05_GeneratePlaceSQL {

    static final class RepDataTiny {
        int     repId;
        int     parId;
        int     parPlaceId;
        boolean isDeleted = false;
        boolean deleteMe = false;

        public RepDataTiny(int repId, int parId) {
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
        Map<Integer, List<RepDataTiny>> placeToRep = getPlaceToRep();
        System.out.println("\n>>>> SIZE: " + placeToRep.size());
        placeToRep.entrySet().stream()
            .limit(100)
            .forEach(System.out::println);

        removeDeletedPlaces(placeToRep);
        System.out.println("\n>>>> SIZE: " + placeToRep.size());
        placeToRep.entrySet().stream()
            .limit(100)
            .forEach(System.out::println);

        keepIfRepsDeleted(placeToRep);
        System.out.println("\n>>>> SIZE: " + placeToRep.size());
        placeToRep.entrySet().stream()
            .limit(100)
            .forEach(System.out::println);

        removeDeletedReps(placeToRep);
        System.out.println("\n>>>> SIZE: " + placeToRep.size());
        placeToRep.entrySet().stream()
            .limit(100)
            .forEach(System.out::println);
        
        setParentPlaceId(placeToRep);
        System.out.println("\n>>>> SIZE: " + placeToRep.size());
        placeToRep.entrySet().stream()
            .limit(100)
            .forEach(System.out::println);

        generatePlaceUpdateSQL(placeToRep);

        System.exit(0);
    }

    static Map<Integer, List<RepDataTiny>> getPlaceToRep() throws IOException {
      List<String> allLines = Files.readAllLines(Paths.get(fileBase, inRepParentFileName), StandardCharsets.UTF_8);

      return allLines.stream()
          .map(line -> PlaceHelper.split(line, '|'))
          .filter(data -> data.length > 2)
          .map(data -> new String[] { data[0], ("null".equals(data[1]) ? "0" : data[1]), data[2] })
          .map(data -> new int[] { Integer.parseInt(data[0]), Integer.parseInt(data[1]), Integer.parseInt(data[2]) })
          .collect(Collectors.groupingBy(
              data -> data[2],
              HashMap::new,
              Collectors.mapping(data -> new RepDataTiny(data[0], data[1]), Collectors.toList())));
    }

    static void removeDeletedPlaces(Map<Integer, List<RepDataTiny>> placeToRep) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(fileBase, inPlaceFileName), StandardCharsets.UTF_8);

        allLines.stream()
            .filter(line -> ! line.endsWith("null"))
            .map(line -> PlaceHelper.split(line, '|'))
            .filter(data -> data.length > 4)
            .map(data -> Integer.parseInt(data[4]))
            .forEach(placeId -> placeToRep.remove(placeId));
    }

    static void keepIfRepsDeleted(Map<Integer, List<RepDataTiny>> placeToRep) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(fileBase, inRepDataFileName), StandardCharsets.UTF_8);

        // We are doing multiple operations, so use traditional looping
        Set<Integer> idsToKeep = new HashSet<>();
        for (String line : allLines) {
            String[] data = PlaceHelper.split(line, '|');
            if (data.length > 4) {
                int repId = Integer.parseInt(data[0]);
                int parId = ("null".equals(data[2])) ? 0 : Integer.parseInt(data[2]);
                int plcId = Integer.parseInt(data[3]);

                idsToKeep.add(plcId);

                List<RepDataTiny> repTs = placeToRep.get(plcId);
                if (repTs != null) {
                    boolean found = false;
                    for (RepDataTiny repT : repTs) {
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

        Set<Integer> idsToDelete = new HashSet<>(placeToRep.keySet());
        idsToDelete.removeAll(idsToKeep);
        
        idsToDelete.forEach(key -> placeToRep.remove(key));
    }

    static void removeDeletedReps(Map<Integer, List<RepDataTiny>> placeToRep) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(fileBase, inRepParentFileName), StandardCharsets.UTF_8);

        Set<Integer> deletedReps = allLines.stream()
                .map(line -> PlaceHelper.split(line, '|'))
                .filter(data -> data.length > 3)
                .filter(data -> (! "null".equals(data[3])))
                .map(data -> Integer.parseInt(data[0]))
                .collect(Collectors.toSet());

        for (Map.Entry<Integer, List<RepDataTiny>> entry : placeToRep.entrySet()) {
            List<RepDataTiny> newRepTs = entry.getValue().stream()
                .filter(repT -> ! deletedReps.contains(repT.repId))
                .collect(Collectors.toList());
            placeToRep.put(entry.getKey(), newRepTs);
        }
    }

    static void setParentPlaceId(Map<Integer, List<RepDataTiny>> placeToRep) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(fileBase, inRepParentFileName), StandardCharsets.UTF_8);

        Map<Integer, Integer> repOwner = allLines.stream()
            .map(line -> PlaceHelper.split(line, '|'))
            .filter(data -> data.length > 2)
            .collect(Collectors.toMap(data -> Integer.parseInt(data[0]), data -> Integer.parseInt(data[2])));

        for (List<RepDataTiny> repTs : placeToRep.values()) {
            boolean deleteAll = repTs.stream().allMatch(repT -> repT.deleteMe);
            if (deleteAll) {
                Set<Integer> parPlaceIds = repTs.stream()
                    .map(repT -> repT.parId)
                    .map(parId -> repOwner.get(parId))
                    .filter(parPlaceId -> parPlaceId != null)
                    .collect(Collectors.toSet());
                if (parPlaceIds.size() == 0) {
                    System.out.println("No parents: " + repTs);
                } else if (parPlaceIds.size() == 1) {
                    repTs.get(0).parPlaceId = parPlaceIds.stream().findFirst().orElse(0);
                } else {
                    repTs.get(0).parPlaceId = parPlaceIds.stream().findFirst().orElse(0);
                }
            } else {
                System.out.println("Don't delete this one: " + repTs);
            }
        }
    }

    static void generatePlaceUpdateSQL(Map<Integer, List<RepDataTiny>> placeToRep) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(fileBase, inPlaceFileName), StandardCharsets.UTF_8);
        System.out.println("Places: " + allLines.size());

        int fileCount = 1;
        List<String> sqlStuff = new ArrayList<>(stmtLimit);
        Arrays.stream(beginSQL).forEach(sql -> sqlStuff.add(sql));

        for (String line : allLines) {
            String[] data = PlaceHelper.split(line, '|');
            try {
                int placeId = Integer.parseInt(data[0]);
                List<RepDataTiny> repTs = placeToRep.get(placeId);
                int deleteId = (repTs == null  || repTs.isEmpty()) ? 0 : repTs.get(0).parPlaceId;
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
        Files.write(Paths.get(fileBase, fileName), sqlStuff, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
