package std.wlj.solr;

import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class StatsNew {
    public static void main(String... args) throws Exception {
        FileSystem currFS = FileSystems.getDefault();
        Path path = currFS.getPath("C:", "temp", "search-results-elbxx.txt");

        List<String> data = Files.readAllLines(path, Charset.forName("UTF-8"));
        int count = data.size();

        double[] stats = { 0.0, 0.0 };
        for (String datum : data) {
            String[] tokens = datum.split("\\|");
            if (tokens.length > 2) {
                stats[0] += Double.parseDouble(tokens[1]);
                stats[1] += Double.parseDouble(tokens[2]);
            }
        }

        double overhead = stats[0] - stats[1];
        System.out.println("The statistics:");
        System.out.printf("    total:     %.6f;  %.6f%n", stats[0], (stats[0] / count));
        System.out.printf("    server:    %.6f;  %.6f%n", stats[1], (stats[1] / count));
        System.out.printf("    overhead:  %.6f;  %.6f%n", overhead, (overhead / count));

        System.exit(0);
    }
}
