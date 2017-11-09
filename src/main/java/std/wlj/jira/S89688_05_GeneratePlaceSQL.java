package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
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
        boolean deleteMe = false;

        public RepDataTiny(int repId, int parId) {
            this.repId = repId;
            this.parId = parId;
        }

        @Override public String toString() { return repId + " . " + parId + " . " + parPlaceId + " . " + deleteMe; }
    }

    static final int    stmtLimit   = 50_000;
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

        setParentPlaceId(placeToRep);
        System.out.println("\n>>>> SIZE: " + placeToRep.size());
        placeToRep.entrySet().stream()
            .limit(100)
            .forEach(System.out::println);

        System.exit(0);
    }

    static Map<Integer, List<RepDataTiny>> getPlaceToRep() throws IOException {
      List<String> allLines = Files.readAllLines(Paths.get("C:/temp", inRepParentFileName), Charset.forName("UTF-8"));

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
        List<String> allLines = Files.readAllLines(Paths.get("C:/temp", inPlaceFileName), Charset.forName("UTF-8"));

        allLines.stream()
            .filter(line -> ! line.endsWith("null"))
            .map(line -> PlaceHelper.split(line, '|'))
            .filter(data -> data.length > 4)
            .map(data -> Integer.parseInt(data[4]))
            .forEach(placeId -> placeToRep.remove(placeId));
    }

    static void keepIfRepsDeleted(Map<Integer, List<RepDataTiny>> placeToRep) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get("C:/temp", inRepDataFileName), Charset.forName("UTF-8"));

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

    static void setParentPlaceId(Map<Integer, List<RepDataTiny>> placeToRep) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get("C:/temp", inRepParentFileName), Charset.forName("UTF-8"));

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
                    System.out.println("Multiple parents: " + repTs + " --> " + parPlaceIds);
                }
            } else {
                System.out.println("Don't delete this one: " + repTs);
            }
        }
    }

    /**
     * Generate the SQL that will update the place-rep to version it.
     * 
     * @param data Array with all of the existing data fields ... change nothing
     *        but the "delete_id" and the "tran_id"
     */
    static String updateRepSQL(String[] data) {
        if (data.length < 16) {
            System.out.println("Too few fields!! " + Arrays.toString(data));
            return "";
        } else if (! "null".equals(data[9])) {
            System.out.println("Already deleted!! " + Arrays.toString(data));
            return "";
        }

        StringBuilder buff = new StringBuilder();

        buff.append("  INSERT INTO place_rep(rep_id, tran_id, parent_id, owner_id, centroid_long, centroid_lattd, ");
        buff.append("place_type_id, parent_from_year, parent_to_year, delete_id, pref_locale, pub_flag, validated_flag, ");
        buff.append("uuid, group_id, pref_boundary_id) ");
        buff.append("VALUES (").append(numericData(data[0]));
        buff.append(", ").append("tranx_id");
        buff.append(", ").append(numericData(data[2]));
        buff.append(", ").append(numericData(data[3]));
        buff.append(", ").append(numericData(data[4]));
        buff.append(", ").append(numericData(data[5]));
        buff.append(", ").append(numericData(data[6]));
        buff.append(", ").append(numericData(data[7]));
        buff.append(", ").append(numericData(data[8]));
        buff.append(", ").append(numericData(data[2]));   // Use the parent as the new "delete_id"
        buff.append(", ").append(characterData(data[10]));
        buff.append(", ").append(booleanData(data[11]));
        buff.append(", ").append(booleanData(data[12]));
        buff.append(", ").append(characterData(data[13]));
        buff.append(", ").append(numericData(data[14]));
        buff.append(", ").append(numericData(data[15]));
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
        Files.write(Paths.get("C:/temp", fileName), sqlStuff, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
