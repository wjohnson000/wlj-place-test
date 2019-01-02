package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.familysearch.standards.place.util.PlaceHelper;

public class S89688_06_SampleDeleteData {

    static final class RepOwnerData {
        int repId;
        int repDeleteId;
        int ownerId;
        int ownerDeleteId;

        @Override public String toString() {
            return repId + "|" + repDeleteId + "|" + ownerId + "|" + ownerDeleteId;
        }
    }

    static final String fileBase    = "C:/temp/delete-by-type";
    static final String repFileName = "s89688-rep-%04d.sql";
    static final String plcFileName = "s89688-place-%04d.sql";

    public static void main(String... args) {
        Map<Integer, RepOwnerData> repDelete = new TreeMap<>();
        sampleRepData(repDelete);
        samplePlaceData(repDelete);
        System.out.println("COUNT: " + repDelete.size());
        repDelete.values().forEach(System.out::println);
    }

    static void sampleRepData(Map<Integer, RepOwnerData> repDelete) {
        int fileCount = 1;
        while (true) {
            try {
                String fileName = String.format(repFileName, fileCount++);
                System.out.println("Processing file: " + fileName);
                List<String> allLines = Files.readAllLines(Paths.get(fileBase, fileName), StandardCharsets.UTF_8);
                sampleRepData(repDelete, allLines);
            } catch (IOException e) {
                break;
            }
        }
    }

    static void sampleRepData(Map<Integer, RepOwnerData> repDelete, List<String> allLines) {
        for (int row=8;  row<allLines.size();  row+=8765) {
            String line = allLines.get(row);
            int ndx = line.indexOf("VALUES");
            if (ndx > 0) {
                line = line.substring(ndx+8);
                String[] chunks = PlaceHelper.split(line, ',');

                if (chunks.length > 9) {
                    RepOwnerData roData = new RepOwnerData();
                    roData.repId = Integer.parseInt(chunks[0].trim());
                    roData.ownerId = Integer.parseInt(chunks[3].trim());
                    roData.repDeleteId = Integer.parseInt(chunks[9].trim());
                    repDelete.put(roData.repId, roData);
                }
            }
        }
    }

    static void samplePlaceData(Map<Integer, RepOwnerData> repDelete) {
        int fileCount = 1;
        while (true) {
            try {
                String fileName = String.format(plcFileName, fileCount++);
                System.out.println("Processing file: " + fileName);
                List<String> allLines = Files.readAllLines(Paths.get(fileBase, fileName), StandardCharsets.UTF_8);
                samplePlaceData(repDelete, allLines);
            } catch (IOException e) {
                break;
            }
        }
    }

    static void samplePlaceData(Map<Integer, RepOwnerData> repDelete, List<String> allLines) {
        for (int row=1;  row<allLines.size();  row++) {
            String line = allLines.get(row);
            int ndx = line.indexOf("VALUES");
            if (ndx > 0) {
                line = line.substring(ndx+8);
                String[] chunks = PlaceHelper.split(line, ',');

                if (chunks.length > 4) {
                    int placeId = Integer.parseInt(chunks[0].trim());
                    RepOwnerData roData = repDelete.values().stream().filter(rod -> rod.ownerId == placeId).findFirst().orElse(null);
                    if (roData != null) {
                        String deleteId = chunks[4].trim();
                        deleteId = deleteId.substring(0, deleteId.length()-2);
                        roData.ownerDeleteId = Integer.parseInt(deleteId);
                    }
                }
            }
        }
    }
}
