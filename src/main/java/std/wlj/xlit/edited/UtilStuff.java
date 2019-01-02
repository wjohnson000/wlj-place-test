package std.wlj.xlit.edited;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

public class UtilStuff {

    static final String  BASE_DIR = "D:/xlit/km/truth-edited";

    static List<String> readFile(String filename) {
        try {
            return Files.readAllLines(Paths.get(BASE_DIR, filename), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("Unable to read file: " + filename + " --> " + e.getMessage());
            return new ArrayList<>();
        }
    }

    static void writeFile(String filename, List<String> contents) {
        try {
            Files.write(Paths.get(BASE_DIR, filename), contents, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("Unable to create file: " + filename + " --> " + e.getMessage());
        }
    }

    static String getUChars(String text) {
        if (text == null) return "Uknown";
        return text.chars()
            .mapToObj(ch -> Integer.toHexString(ch).toUpperCase())
            .map(hex -> "U+" + StringUtils.leftPad(hex, 4, "0"))
            .collect(Collectors.joining(" "));
    }
}
