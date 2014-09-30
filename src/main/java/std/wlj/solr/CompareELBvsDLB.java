package std.wlj.solr;

import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class CompareELBvsDLB {
    public static void main(String... args) throws Exception {
        FileSystem currFS = FileSystems.getDefault();
        Path path01 = currFS.getPath("C:", "temp", "search-results-4gb.txt");
        Path path02 = currFS.getPath("C:", "temp", "search-results-6gb.txt");

        List<String> data01 = Files.readAllLines(path01, Charset.forName("UTF-8"));
        List<String> data02 = Files.readAllLines(path02, Charset.forName("UTF-8"));

        System.out.println("Rows01: " + data01.size());
        System.out.println("Rows02: " + data02.size());

        double[] stats01 = getStats(data01);
        double[] stats02 = getStats(data02);

        System.out.println("4GB statistics:");
        System.out.printf("    total:    %.6f%n", stats01[0]);
        System.out.printf("    server:   %.6f%n", stats01[1]);
        System.out.printf("    overhead: %.6f%n", (stats01[0] - stats01[1]*1.04));
        System.out.printf("    average:  %.6f%n", ((stats01[0] - stats01[1]*1.04) / 300000));

        System.out.println();
        System.out.println("6GB statistics:");
        System.out.printf("    total:    %.6f%n", stats02[0]);
        System.out.printf("    server:   %.6f%n", stats02[1]);
        System.out.printf("    overhead: %.6f%n", (stats02[0] - stats02[1]*1.04));
        System.out.printf("    average:  %.6f%n", ((stats02[0] - stats02[1]) / 300000));

        System.exit(0);
    }

    private static double[] getStats(List<String> data) {
        double[] stats = { 0.0, 0.0 };

        for (String datum : data) {
            String[] tokens = datum.split("\\|");
            if (tokens.length > 2) {
                stats[0] += Double.parseDouble(tokens[1]);
                stats[1] += Double.parseDouble(tokens[2]);
            }
        }

        return stats;
    }
}
