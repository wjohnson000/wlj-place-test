package std.wlj.kml;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class MatchKMLToPlaceRep {
    static class RepData {
        String type;
        String name;

        public RepData(String type, String name) {
            this.type = type;
            this.name = name;
        }
        public String toString() {
            return type + " . " + name;
        }
    }

    public static void main(String... args) throws IOException {
        Map<Integer,List<String>> repToFile = createMatchFileMap();
        repToFile.entrySet().stream()
            .forEach(System.out::println);
    }

    public static Map<Integer,List<String>> createMatchFileMap() throws IOException {
        // Pull rep-id and name from silly file
        List<String> repIdName = Files.readAllLines(Paths.get("D:/postgis/rep-to-id.txt"), Charset.forName("UTF-8"));

        Map<Integer,RepData> repIdMap = repIdName.stream()
            .filter(entry -> entry.trim().length() > 3)
            .map(entry -> entry.split("\t"))
            .collect(Collectors.toMap(
                e -> Integer.parseInt(e[0].trim()),
                e -> new RepData(e[1], e[2]),
                (k1, k2) -> k1,
                TreeMap::new));

        // Read the list of files
        List<String> files = new ArrayList<>();
        Files.newDirectoryStream(Paths.get("D:/postgis/files"))
            .forEach(ff -> files.add(ff.getFileName().toString()));

        Map<Integer,List<String>> repToFile = new TreeMap<>();
        repIdMap.entrySet().stream()
            .forEach((entry -> repToFile.put(entry.getKey(), matchFiles(entry.getValue(), files))));
        return repToFile;
    }

    static List<String> matchFiles(RepData repData, List<String> fileNames) {
        if (repData.type.equalsIgnoreCase("US")) {
            return fileNames.stream()
                    .filter(fn -> fn.toLowerCase().replaceAll(" ", "").startsWith("state-")  ||  fn.toLowerCase().replaceAll(" ", "").startsWith("us"))
                    .filter(fn -> fn.toLowerCase().replaceAll(" ", "").endsWith("-" + repData.name.toLowerCase().replaceAll(" ", "") + ".kml"))
                    .collect(Collectors.toList());
        } else {
            return fileNames.stream()
               .filter(fn -> fn.toLowerCase().replaceAll(" ", "").startsWith("county-" + repData.type.toLowerCase() + "_"))
               .filter(fn -> fn.toLowerCase().replaceAll(" ", "").endsWith(repData.name.toLowerCase().replaceAll(" ", "") + ".kml"))
               .collect(Collectors.toList());
        }
    }
}
