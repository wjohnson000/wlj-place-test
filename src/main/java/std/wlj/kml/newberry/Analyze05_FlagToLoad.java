package std.wlj.kml.newberry;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Take the results of the latest collapsed file (from step 4) and compare it with the file
 * of approved boundaries to load, creating a *FINAL* list of boundaries to load.
 * 
 * The new file, "bndy-xx-loadme.txt" may be created by hand, and needs to have the
 * following format:
 * <ul>
 *   <li>"true", if this boundary is to be loaded</li>
 *   <li>Folder name</li>
 *   <li>Placemark name</li>
 *   <li>timespan from-date</li>
 *   <li>timespan to-date</li>
 *   <li>rep-id</li>
 * </ul>
 * 
 * @author wjohnson000
 *
 */
public class Analyze05_FlagToLoad {

    static final String pathToIn   = "D:/postgis/newberry/bndy-04-match.txt";
    static final String pathToOut  = "D:/postgis/newberry/bndy-05-match.txt";
    static final String pathToLoad = "D:/postgis/newberry/bndy-xx-loadme.txt";

    public static void main(String...args) throws IOException {
        Set<String> boundaryToLoad = getBoundaryToLoad();

        List<String> lines = Files.readAllLines(Paths.get(pathToIn), StandardCharsets.UTF_8);
        List<String> newLines = lines.stream()
            .filter(line -> line.length() > 10)
            .map(line -> {
                String key = getKey(line, 3, 4, 5, 9);
                String loadIt = (boundaryToLoad.contains(key)) ? "true|" : "|";
                return loadIt + line;
            })
            .collect(Collectors.toList());

        newLines.add(0, "");
        Files.write(Paths.get(pathToOut), newLines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static Set<String> getBoundaryToLoad() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(pathToLoad), StandardCharsets.UTF_8);

        return lines.stream()
            .filter(line -> line.startsWith("true"))
            .map(line -> getKey(line, 2, 3, 4, 5))
            .collect(Collectors.toSet());
    }
    
    static String getKey(String line, int... keyFields) {
        String[] fields = line.split("\\|");

        return IntStream.of(keyFields)
            .mapToObj(ndx -> fields[ndx])
            .collect(Collectors.joining("."));
    }
}
