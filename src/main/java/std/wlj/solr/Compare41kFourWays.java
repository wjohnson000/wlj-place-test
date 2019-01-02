package std.wlj.solr;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Compare41kFourWays {

    private static String[] fileNames = {
        "results-search-41k-sunjdk-embedded.txt",
        "results-search-41k-sunjdk-http.txt",
        "results-search-41k-openjdk-embedded.txt",
        "results-search-41k-openjdk-http.txt"
    };

    private static String[] prefixes = {
        "sun-embd", "sun-http", "open-embd", "open-http"
    };

    public static void main(String... args) throws Exception {
        FileSystem currFS = FileSystems.getDefault();

        Path path = currFS.getPath("C:", "temp", "results-search-41k.txt");
        List<String> data01 = Files.readAllLines(path, StandardCharsets.UTF_8);
        System.out.println("Rows01: " + data01.size());

        List<Map<String,List<String>>> map4x = new ArrayList<>();
        for (String fileName : fileNames) {
            path = currFS.getPath("C:", "temp", fileName);
            List<String> data02 = Files.readAllLines(path, StandardCharsets.UTF_8);
            System.out.println("Rows02: " + data02.size());

            Map<String,List<String>> mapTemp = new HashMap<>();
            // Put the 4x results into a map, keyed by the search string
            for (String data : data02) {
                String[] tokens = data.split("\\|");
                List<String> lines = mapTemp.get(tokens[0]);
                if (lines == null) {
                    lines = new ArrayList<>();
                    mapTemp.put(tokens[0], lines);
                }
                lines.add(data);
            }
            map4x.add(mapTemp);
        }

        List<String> outData = new ArrayList<>();
        for (int i=0;  i<data01.size();  i++) {
            String line01 = data01.get(i);
            String[] tokens01 = line01.split("\\|");

            int ndx = 0;
            boolean addMainEntry = true;
            for (Map<String,List<String>> mapTemp : map4x) {
                StringBuffer buff = new StringBuffer(512);
                String prefix = prefixes[ndx++];

                List<String> line02s = mapTemp.get(tokens01[0]);
                if (line02s == null) {
                    continue;
                }

                boolean samePR = true;
                for (String line02 : line02s) {
                    String[] tokens02 = line02.split("\\|");
                    if (tokens01.length != tokens02.length) {
                        samePR = false;
                    } else {
                        for (int j=7;  j<tokens01.length;  j++) {
                            if (! tokens01[j].equals(tokens02[j])) {
                                samePR = false;
                            }
                        }
                    }
                }

                if (addMainEntry) {
                    addMainEntry = false;
                    buff.append(tokens01[0]);
                    for (int j=1;  j<=6;  j++) {
                        buff.append("|").append(tokens01[j]);
                    }

                    if (! samePR) {
                        for (int j=7;  j<tokens01.length;  j++) {
                            buff.append("|").append(tokens01[j]);
                        }
                    }
                    outData.add(buff.toString());
                }

                for (String line02 : line02s) {
                    String[] tokens02 = line02.split("\\|");
                    buff = new StringBuffer(512);
                    buff.append(prefix);
                    prefix = "";
                    for (int j=1;  j<=6;  j++) {
                        buff.append("|").append(tokens02[j]);
                    }

                    if (! samePR) {
                        for (int j=7;  j<tokens02.length;  j++) {
                            buff.append("|").append(tokens02[j]);
                        }
                    }
                    outData.add(buff.toString());
                }
            }

            outData.add("");
        }

        Path outPath = currFS.getPath("C:", "temp", "results-search-compare-4x.txt");
        Files.write(outPath, outData, StandardCharsets.UTF_8, StandardOpenOption.CREATE);

        System.exit(0);
    }
}
