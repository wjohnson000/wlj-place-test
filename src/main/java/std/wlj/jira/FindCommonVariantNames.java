package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.familysearch.standards.place.util.PlaceHelper;

public class FindCommonVariantNames {

    public static void main(String... args) throws IOException {
        List<String> allLines = Files.readAllLines(Paths.get("D:/important/place-name-all.txt"), Charset.forName("UTF-8"));
        System.out.println("Line-count: " + allLines.size());

        int count = 0;
        String repId = "";
        Set<String> repNames = new HashSet<>();
        Map<String, Integer> nameCount = new TreeMap<>();

        for (String line : allLines) {
            if (++count % 100_000 == 0) {
                System.out.println(" ... " + count);
            }
            if (line.contains("true")) {
                continue;
            }

            String[] chunks = line.split("\\|");
            if (chunks.length > 3) {
                String id   = chunks[0];
                String name = chunks[2];
                String norm = PlaceHelper.normalize(name).toLowerCase();
                if (! id.equals(repId)) {
                    repNames.clear();
                }

                if (id.equals(repId)  &&  repNames.contains(norm)) {
                    // Do nothing!!
                } else {
                    repNames.add(norm);
                    Integer nCnt = nameCount.getOrDefault(norm, 0);
                    if (count < 4_000_000  ||  nCnt > 10) {
                        nameCount.put(norm, nCnt+1);
                    }
                }
                repId = id;
            }
        }

        nameCount.entrySet().stream()
            .filter(entry -> entry.getValue() > 1000)
            .forEach(System.out::println);

        System.exit(0);
    }
}
