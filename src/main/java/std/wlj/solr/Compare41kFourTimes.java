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


public class Compare41kFourTimes {
    public static void main(String... args) throws Exception {
        FileSystem currFS = FileSystems.getDefault();
        Path path01 = currFS.getPath("C:", "temp", "results-search-41k-local.txt");
        Path path02 = currFS.getPath("C:", "temp", "results-search-41k-four-times.txt");

        List<String> data01 = Files.readAllLines(path01, StandardCharsets.UTF_8);
        List<String> data02 = Files.readAllLines(path02, StandardCharsets.UTF_8);
        Map<String,List<String>> map4x = new HashMap<>();

        System.out.println("Rows01: " + data01.size());
        System.out.println("Rows02: " + data02.size());

        // Put the 4x results into a map, keyed by the search string
        for (String data : data02) {
            String[] tokens = data.split("\\|");
            List<String> lines = map4x.get(tokens[0]);
            if (lines == null) {
                lines = new ArrayList<>();
                map4x.put(tokens[0], lines);
            }
            lines.add(data);
        }

        List<String> outData = new ArrayList<>();
        for (int i=0;  i<data01.size();  i++) {
            StringBuffer buff = new StringBuffer(512);
            String line01 = data01.get(i);
            String[] tokens01 = line01.split("\\|");
            List<String> line02s = map4x.get(tokens01[0]);

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

            for (String line02 : line02s) {
                String[] tokens02 = line02.split("\\|");
                buff = new StringBuffer(512);
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

            outData.add("");
        }

        Path outPath = currFS.getPath("C:", "temp", "results-search-compare-4x.txt");
        Files.write(outPath, outData, StandardCharsets.UTF_8, StandardOpenOption.CREATE);

        System.exit(0);
    }
}
