package std.wlj.kml.newberry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Read the results from the previous step (bndy-05-match.txt) and, based on which versions
 * of a particular boundary to load, extend the "to-date" to meet up with the next version
 * to be loaded.  This ensures a contiguous set of boundary date ranges for a given rep.
 * 
 * @author wjohnson000
 *
 */
public class Analyze06_AlterTimeSpan {

    static final String pathToIn  = "D:/postgis/newberry/bndy-05-match.txt";
    static final String pathToOut = "D:/postgis/newberry/bndy-06-match.txt";

    static class BoundaryData06 {
        List<String> allData;
        boolean loadMe;
        String  pmName;
        String  fromDate;
        String  toDate;
        String  newToDate;

        String getPmName() { return pmName; }
    };

    static Map<String, List<BoundaryData06>> boundaryMap = new HashMap<>();

    public static void main(String... args) throws IOException {
        loadMapFile();
        calcNewDates();
    }

    static void loadMapFile() throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(pathToIn), StandardCharsets.UTF_8);
        boundaryMap = allLines.stream()
                .map(line -> extractData(line))
                .filter(bData -> bData.pmName != null)
                .collect(Collectors.groupingBy(BoundaryData06::getPmName, LinkedHashMap::new, Collectors.toList()));
        System.out.println("Count: " + boundaryMap.size());
    }

    static void calcNewDates() throws IOException {
        List<BoundaryData06> bdyToLoad = new ArrayList<>();
        boundaryMap.values().stream().forEach(list -> bdyToLoad.addAll(updateDates(list)));

        // Insert the new "to-date" into the "allData" array.  We can't do a direct "add(...)"
        // because the list is immutable, so we'll break it into "before" and "after" chunks.
        bdyToLoad.forEach(bData -> {
            List<String> allerData = new ArrayList<>();
            allerData.addAll(bData.allData.subList(0, 7));
            allerData.add(bData.newToDate);
            allerData.addAll(bData.allData.subList(7, bData.allData.size()));
            bData.allData = allerData;
        });

        List<String> newBdyData = bdyToLoad.stream()
            .map(bData -> bData.allData.stream().collect(Collectors.joining("|")))
            .collect(Collectors.toList());

        newBdyData.add(0, "");
        Files.write(Paths.get(pathToOut), newBdyData, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static BoundaryData06 extractData(String line) {
        BoundaryData06 bData = new BoundaryData06();
        String[] fields = line.split("\\|");

        if (fields.length > 15) {
            bData.allData  = Arrays.asList(fields);
            bData.loadMe   = "true".equals(fields[0]);
            bData.pmName   = fields[3] + "." + fields[4];
            bData.fromDate = fields[5];
            bData.toDate   = fields[6];
        }

        return bData;
    }
    
    static List<BoundaryData06> updateDates(List<BoundaryData06> list) {
        List<BoundaryData06> newList = new ArrayList<>();

        BoundaryData06 prev = null;
        for (BoundaryData06 bData : list) {
            if (bData.loadMe) {
                newList.add(bData);
                bData.newToDate = bData.toDate;
                prev = bData;
            } else if (prev != null) {
                prev.newToDate = bData.toDate;
            }
        }

        if (prev != null) {
            prev.newToDate = "";
        }

        return newList;
    }
}
