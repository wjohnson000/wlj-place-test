package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.familysearch.standards.place.access.validator.MessageFactory;
import org.familysearch.standards.place.access.validator.NameValidator;
import org.familysearch.standards.place.data.PlaceDataException;

public class FindIllegalVariantNames {

    public static void main(String... args) throws IOException {
        List<String> badNames = new ArrayList<>();
        NameValidator validator = new NameValidator(null, new MessageFactory());

        List<String> allLines = Files.readAllLines(Paths.get("C:/temp/place-name-all.txt"), Charset.forName("UTF-8"));
        System.out.println("Line-count: " + allLines.size());

        for (String line : allLines) {
            String[] chunks = line.split("\\|");
            if (chunks.length > 3) {
                String name = chunks[2];
                try {
                    validator.validateVariantName(name);
                } catch(PlaceDataException ex) {
                    String lineData = line + "|" + getUChars(name);
                    badNames.add(lineData);
                }
            }
        }

        Files.write(Paths.get("C:/temp/place-name-invalid.txt"), badNames, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        System.exit(0);
    }

    static String getUChars(String text) {
        return text.chars()
            .mapToObj(ch -> Integer.toHexString(ch).toUpperCase())
            .map(hex -> "U+" + StringUtils.leftPad(hex, 4, "0"))
            .collect(Collectors.joining(" "));
    }

}
