package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

public class FindIllegalVariantNames {

    public static void main(String... args) throws IOException {
        List<String> badNames = new ArrayList<>();
        NameValidator validator = new NameValidator(null, new MessageFactory());

        List<String> allLines = Files.readAllLines(Paths.get("D:/important/place-name-all.txt"), StandardCharsets.UTF_8);
        System.out.println("Line-count: " + allLines.size());

        int count = 0;
        Set<Integer> iffyChars = new TreeSet<>();
        for (String line : allLines) {
            if (++count % 100_000 == 0) {
                System.out.println(" ... " + count);
            }
            if (line.contains("true")) {
                continue;
            }

            String[] chunks = line.split("\\|");
            if (chunks.length > 3) {
                String name = chunks[2];
                try {
                    validator.validateVariantName(name);
                    name.codePoints()
                        .filter(cp -> ! isValidCodepointDisplay(validator, cp))
                        .peek(cp -> iffyChars.add(cp))
                        .mapToObj(cp -> Integer.valueOf(cp))
                        .map(cp -> String.valueOf(cp))
                        .collect(Collectors.joining(" "));
                } catch(PlaceDataException ex) {
                    Set<Integer> badCP = name.codePoints()
                            .filter(cp -> ! isValidCodepointVariant(validator, cp))
                            .mapToObj(cp -> Integer.valueOf(cp))
                            .collect(Collectors.toSet());
                        String badUChars = badCP.stream()
                            .map(cp -> getUChars(new String(Character.toChars(cp))))
                            .collect(Collectors.joining(" "));

                    String lineData = line + "|" + badUChars + "|" + getUChars(name);
                    badNames.add(lineData);
                }
            }
        }

        Files.write(Paths.get("C:/temp/place-name-invalid.txt"), badNames, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);

        for (Integer cp : iffyChars) {
            String sCH = new String(Character.toChars(cp));
            System.out.println(getUChars(sCH) + "|" + cp + "|" + sCH + "|" + Character.getName(cp));
        }

        System.exit(0);
    }

    static boolean isValidCodepointVariant(NameValidator validator, int codePoint) {
        try {
            String nameX = 'A' + new String(Character.toChars(codePoint)) + 'Z';
            validator.validateVariantName(nameX);
            return true;
        } catch(Exception exx) {
            return false;
        }
    }

    static boolean isValidCodepointDisplay(NameValidator validator, int codePoint) {
        try {
            String nameX = 'A' + new String(Character.toChars(codePoint)) + 'Z';
            validator.validateDisplayName(nameX);
            return true;
        } catch(Exception exx) {
            return false;
        }
    }

    static String getUChars(String text) {
        return text.chars()
            .mapToObj(ch -> Integer.toHexString(ch).toUpperCase())
            .map(hex -> "U+" + StringUtils.leftPad(hex, 4, "0"))
            .collect(Collectors.joining(" "));
    }

}
