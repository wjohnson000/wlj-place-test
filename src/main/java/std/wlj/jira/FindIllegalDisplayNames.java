package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.familysearch.standards.place.access.validator.MessageFactory;
import org.familysearch.standards.place.access.validator.NameValidator;
import org.familysearch.standards.place.data.PlaceDataException;

public class FindIllegalDisplayNames {

    public static void main(String... args) throws IOException {
        List<String> badNames = new ArrayList<>();
        NameValidator validator = new NameValidator(null, new MessageFactory());

        List<String> allLines = Files.readAllLines(Paths.get("C:/temp/display-name-all.txt"), Charset.forName("UTF-8"));
        System.out.println("Line-count: " + allLines.size());

        int count = 0;
        Set<Integer> badChars = new TreeSet<>();
        for (String line : allLines) {
            if (++count % 100_000 == 0) {
                System.out.println(" ... " + count);
            }
            String[] chunks = line.split("\\|");
            if (chunks.length > 3) {
                String name = chunks[2];
                try {
                    validator.validateDisplayName(name);
                } catch(PlaceDataException ex) {
                    String lineData = line + "|" + getUChars(name);
                    badNames.add(lineData);
                    final int length = name.length();
                    for (int offset=0;  offset<length; ) {
                        int cp = name.codePointAt(offset);
                        String nameX = "A" + new String(Character.toChars(cp)) + "Z";
                        try {
                            validator.validateDisplayName(nameX);
                        } catch (PlaceDataException ex2) {
                            badChars.add(cp);
                        }
                        offset += Character.charCount(cp);
                    }
                }
            }
        }

        Files.write(Paths.get("C:/temp/display-name-invalid.txt"), badNames, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

        for (Integer cp : badChars) {
            String sCH = new String(Character.toChars(cp));
            System.out.println(getUChars(sCH) + "|" + cp + "|" + sCH);
        }

        System.exit(0);
    }

    static String getUChars(String text) {
        return text.chars()
            .mapToObj(ch -> Integer.toHexString(ch).toUpperCase())
            .map(hex -> "U+" + StringUtils.leftPad(hex, 4, "0"))
            .collect(Collectors.joining(" "));
    }

}
