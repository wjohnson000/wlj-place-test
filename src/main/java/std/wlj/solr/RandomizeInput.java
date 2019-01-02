package std.wlj.solr;

import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RandomizeInput {
    public static void main(String... args) throws Exception {
        FileSystem currFS = FileSystems.getDefault();
        Path path01 = currFS.getPath("C:/temp/local-all.txt");

        List<String> data01 = Files.readAllLines(path01, StandardCharsets.UTF_8);

        Collections.sort(data01, new Comparator<String>() {
            @Override
            public int compare(String str1, String str2) {
                return str1.hashCode() - str2.hashCode();
            }
        });

        Path outPath = currFS.getPath("C:/temp/local-all-random.txt");
        Files.write(outPath, data01, StandardCharsets.UTF_8, StandardOpenOption.CREATE);

        System.exit(0);
    }
}
