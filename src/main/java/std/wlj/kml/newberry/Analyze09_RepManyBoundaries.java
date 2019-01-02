package std.wlj.kml.newberry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Analyze09_RepManyBoundaries {

    static class BoundaryData09 {
        String  line;
        String  repId;

        String  getKey() { return repId; }
    };

    static final String basePath = "D:/postgis/newberry/";
    static final String pathToIn = basePath + "bndy-06-match-co.txt";

    static  Map<String, List<BoundaryData09>> repToBoundary = new HashMap<>();

    public static void main(String...args) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get(pathToIn), StandardCharsets.UTF_8);
        repToBoundary = allLines.stream()
            .map(line -> extractData(line))
            .collect(Collectors.groupingBy(BoundaryData09::getKey, Collectors.toList()));

        repToBoundary.entrySet()
            .stream()
            .filter(entry -> entry.getValue().size() >= 10)
            .forEach(entry -> entry.getValue().forEach(bData -> System.out.println(bData.line)));
    }

    static BoundaryData09 extractData(String line) {
        BoundaryData09 bData = new BoundaryData09();
        bData.repId = "0";

        String[] fields = line.split("\\|");
        if (fields.length > 15) {
            bData.line = line;
            bData.repId = fields[11];
        }

        return bData;
    }
}
