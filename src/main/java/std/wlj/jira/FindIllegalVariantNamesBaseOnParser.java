package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.familysearch.standards.place.util.PlaceHelper;

public class FindIllegalVariantNamesBaseOnParser {

    public static final String REMOVE_PUNCT_PAT_AND_WILDCARDS = "[!@#\\$%\\^\\=`¡¢£¤¥¦§¨©ª¬®¯°±¹²³´ˊµ¶º¼½¾×÷ʹʺˀˁ˄˅ˆˇˈˉ‼⁈⁉‽ʔ¿՞፧⁇﹖؟∗✱٭？＊～\\*\\?]";

    public static void main(String... args) throws IOException {
        List<String> badNames = new ArrayList<>();

        List<String> allLines = Files.readAllLines(Paths.get("D:/important/place-name-all.txt"), StandardCharsets.UTF_8);
        System.out.println("Line-count: " + allLines.size());

        allLines.clear();
        allLines.add("205596|en|Siwa`avaatsi|321401|1|false");
        allLines.add("62374|en|Pleasant Grove Cemetery #1 and #2|16192458|5568065|false");
        allLines.add("62374|en|Pleasant Grove Cemetery 1 and 2|16192458|5568065|false");

        int count = 0;
        Map<Integer, Integer> badPunctCount = new TreeMap<>();
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
                String nameNormal = PlaceHelper.normalize(name);
                String nameNoPunct = nameNormal.replaceAll(REMOVE_PUNCT_PAT_AND_WILDCARDS, "");
                System.out.println("name: " + name);
                System.out.println("    : " + nameNormal);
                System.out.println("    : " + nameNoPunct);
                if (! nameNormal.equalsIgnoreCase(nameNoPunct)) {
                    String badChars02 = getDiff(nameNormal, nameNoPunct);
                    String lineData = line + "|" + nameNormal + "|" + badChars02 + "|" + getUChars(badChars02);
                    badNames.add(lineData);
                    badChars02.codePoints().forEach(cp -> {
                        Integer cnt = badPunctCount.getOrDefault(cp, 0);
                        badPunctCount.put(cp, cnt+1);
                    });
                }
            }
        }

        System.out.println("\n\n\nName count by bad character ...");
        badPunctCount.entrySet().stream()
            .forEach(entry -> {
                String cp = new String(Character.toChars(entry.getKey()));
                System.out.println(cp + "|" + getUChars(cp) + "|" + entry.getValue());
            });

        Files.write(Paths.get("C:/temp/place-name-punctuation-invalid.txt"), badNames, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        System.exit(0);
    }

    static String getDiff(String name, String nameNoP) {
        Set<Integer> codePoints = new TreeSet<>();
        name.codePoints().forEach(cp -> codePoints.add(cp));
        nameNoP.codePoints().forEach(cp -> codePoints.remove(cp));
        int[] cps = codePoints.stream().mapToInt(Integer::intValue).toArray();
        return new String(cps, 0, cps.length);
    }

    static String getUChars(String text) {
        return text.chars()
            .mapToObj(ch -> Integer.toHexString(ch).toUpperCase())
            .map(hex -> "U+" + StringUtils.leftPad(hex, 4, "0"))
            .collect(Collectors.joining(" "));
    }
}
