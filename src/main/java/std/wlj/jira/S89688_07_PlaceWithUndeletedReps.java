package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.familysearch.standards.place.util.PlaceHelper;

public class S89688_07_PlaceWithUndeletedReps {

    static final String fileBase    = "C:/temp/delete-by-type";
    static final String repFileName = "s89688-rep-%04d.sql";
    static final String plcFileName = "s89688-place-%04d.sql";

    public static void main(String... args) {
        Map<Integer, Set<Integer>> placeToRep = new TreeMap<>();
        populatePlaceToRep(placeToRep);
        removeDeletedPlaces(placeToRep);
        placeToRep.entrySet().forEach(System.out::println);
    }

    static void populatePlaceToRep(Map<Integer, Set<Integer>> placeToRep) {
        int fileCount = 1;
        while (true) {
            try {
                String fileName = String.format(repFileName, fileCount++);
                System.out.println("Processing file: " + fileName);
                System.out.println("   P2R.size: " + placeToRep.size());
                List<String> allLines = Files.readAllLines(Paths.get(fileBase, fileName), StandardCharsets.UTF_8);
                populatePlaceToRep(placeToRep, allLines);
            } catch (IOException e) {
                break;
            }
        }
    }

    static void populatePlaceToRep(Map<Integer, Set<Integer>> placeToRep, List<String> allLines) {
        for (int row=8;  row<allLines.size();  row++) {
            String line = allLines.get(row);
            int ndx = line.indexOf("VALUES");
            if (ndx > 0) {
                line = line.substring(ndx+8);
                String[] chunks = PlaceHelper.split(line, ',');

                if (chunks.length > 9) {
                    int repId = Integer.parseInt(chunks[0].trim());
                    int ownerId = Integer.parseInt(chunks[3].trim());
                    Set<Integer> reps = placeToRep.get(ownerId);
                    if (reps == null) {
                        reps = new HashSet<>();
                        placeToRep.put(ownerId, reps);
                    }
                    reps.add(repId);
                }
            }
        }
    }

    private static void removeDeletedPlaces(Map<Integer, Set<Integer>> placeToRep) {
        int fileCount = 1;
        while (true) {
            try {
                String fileName = String.format(plcFileName, fileCount++);
                System.out.println("Processing file: " + fileName);
                System.out.println("   P2R.size: " + placeToRep.size());
                List<String> allLines = Files.readAllLines(Paths.get(fileBase, fileName), StandardCharsets.UTF_8);
                removeDeletedPlaces(placeToRep, allLines);
            } catch (IOException e) {
                break;
            }
        }
    }

    static void removeDeletedPlaces(Map<Integer, Set<Integer>> placeToRep, List<String> allLines) {
        for (int row=1;  row<allLines.size();  row++) {
            String line = allLines.get(row);
            int ndx = line.indexOf("VALUES");
            if (ndx > 0) {
                line = line.substring(ndx+8);
                String[] chunks = PlaceHelper.split(line, ',');

                if (chunks.length > 4) {
                    int placeId = Integer.parseInt(chunks[0].trim());
                    placeToRep.remove(placeId);
                }
            }
        }
    }
}
