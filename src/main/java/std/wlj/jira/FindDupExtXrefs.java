package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FindDupExtXrefs {

    public static void main(String... args) throws IOException {
        Set<String> foundKeys = new HashSet<>();
        List<String> dupLines = new ArrayList<>();

        List<String> allLines = Files.readAllLines(Paths.get("D:/important/external-xref-all.txt"), StandardCharsets.UTF_8);
        System.out.println("Line-count: " + allLines.size());

        for (String line : allLines) {
            String[] chunks = line.split("\\|");
            if (chunks.length > 3) {
                String key = chunks[1] + "." + chunks[2] + "." + chunks[3];
                if (foundKeys.contains(key)) {
                    dupLines.add(line);
                } else {
                    foundKeys.add(key);
                }
            }
        }

//        dupLines.forEach(System.out::println);
        List<String> sqlCommands = dupLines.stream()
            .map(line -> line.split("\\|"))
            .filter(chunks -> chunks.length > 3)
            .map(chunks -> chunks[0])
            .map(chunk -> "DELETE FROM external_xref WHERE xref_id = " + chunk + ";")
            .collect(Collectors.toList());
        Files.write(Paths.get("C:/temp/delete-duplicate-xref.sql"), sqlCommands, StandardOpenOption.CREATE);
    }
}
