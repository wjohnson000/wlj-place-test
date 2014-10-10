package std.wlj.solr;

import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class StatsNew {
    private static String[] fileNames = {
        "results-dlbprod-6x25k.txt",
        "results-dlbprod-12x25k.txt",
        "results-dlbprod-50x15k.txt"
    };


    public static void main(String... args) throws Exception {
        FileSystem currFS = FileSystems.getDefault();
        for (String fileName : fileNames) {
            Path path = currFS.getPath("C:", "temp", fileName);

            List<String> data = Files.readAllLines(path, Charset.forName("UTF-8"));
            int count = data.size();

            int badCount = 0;
            double[] stats = { 0.0, 0.0 };
            for (String datum : data) {
                String[] tokens = datum.split("\\|");
                if (tokens.length > 2) {
                    stats[0] += Double.parseDouble(tokens[1]);
                    stats[1] += Double.parseDouble(tokens[2]);
                } else if (datum.contains("thr-")) {
                    badCount++;
                }
            }

            double overhead = stats[0] - stats[1];
            System.out.println("\nThe statistics for: " + fileName);
            System.out.printf("    total:     %.6f;  %.6f%n", stats[0], (stats[0] / count));
            System.out.printf("    server:    %.6f;  %.6f%n", stats[1], (stats[1] / count));
            System.out.printf("    overhead:  %.6f;  %.6f%n", overhead, (overhead / count));
            System.out.printf("    bad calls: %d%n", badCount);
        }

        System.exit(0);
    }
}
