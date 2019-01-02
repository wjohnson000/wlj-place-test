package std.wlj.xlit;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.familysearch.standards.place.util.PlaceHelper;

public class XlitOutputCompareDetails {

    static final String TAB              = "\t";
    static final String BASE_DIR         = "D:/xlit/km/truth";
    static final String KM_PHONETIC_FILE = "km-phonetic.txt";
    static final String XLIT_FILE        = "xlit-output.txt";
    static final String XLIT_NAKED_FILE  = "xlit-output-naked.txt";
    static final String COMPARE_FILE       = "xlit-output-compare.txt";
    static final String COMPARE_DETAILS_FILE = "xlit-output-compare-details.txt";

    static       int count = 0;
    static       int matchCount01 = 0;
    static       int matchCount02 = 0;
    static final Map<String,String> km2Phonetic = new HashMap<>();

    public static void main(String... args) throws Exception {
        loadKmPhonetics();
        createCompare();
        createCompareDetails();
        saveKmPhonetics();
    }

    static void createCompare() throws Exception {
        List<String> nameAndOrig = Files.readAllLines(Paths.get(BASE_DIR, XLIT_FILE), StandardCharsets.UTF_8);
        List<String[]> nameToOrig = nameAndOrig.stream()
            .map(line -> PlaceHelper.split(line, '\t'))
            .filter(row -> row.length == 3)
            .collect(Collectors.toList());

        List<String> nameAndNew  = Files.readAllLines(Paths.get(BASE_DIR, XLIT_NAKED_FILE), StandardCharsets.UTF_8);
        Map<String,String> nameToNew = nameAndNew.stream()
            .map(line -> PlaceHelper.split(line, '\t'))
            .filter(row -> row.length == 2)
            .collect(Collectors.toMap(row -> row[0], row -> row[1]));

        List<String> results = nameToOrig.stream()
            .map(row -> row[0] + "\t" + row[1] + "\t" + nameToNew.getOrDefault(row[0], "UNKNOWN"))
            .collect(Collectors.toList());

        Files.write(Paths.get(BASE_DIR, COMPARE_FILE), results, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static void createCompareDetails() throws Exception {
        List<String> data = Files.readAllLines(Paths.get(BASE_DIR, COMPARE_FILE), StandardCharsets.UTF_8);
        List<String> output = data.stream()
            .map(line -> getDetails(line))
            .collect(Collectors.toList());
        Files.write(Paths.get(BASE_DIR, COMPARE_DETAILS_FILE), output, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("\nCount01: " + matchCount01);
        System.out.println("Count02: " + matchCount02);
    }

    static String getDetails(String line) {
        if (++count % 25 == 0) System.out.println();
        System.out.print(".");

        String[] chunks = PlaceHelper.split(line, '\t');
        if (chunks.length < 3) {
            return line;
        }

        String enName      = chunks[0];
        String kmDesired   = chunks[1];
        String kmGenerated = chunks[2];
        List<String> choices = getChoices(kmGenerated);
        String bestMatch = getBestMatch(kmDesired, choices).replaceAll(" ", "");

        String kmPhonetic01 = getKmPhonetic(kmDesired);
        String kmPhoneXxx01 = kmPhonetic01.replaceAll(" ", "").trim().toLowerCase();
        String kmPhonetic02 = getKmPhonetic(bestMatch);
        String kmPhoneXxx02 = kmPhonetic02.replaceAll(" ", "").trim().toLowerCase();

        if (kmDesired.equals(bestMatch)) matchCount01++;
        if (kmPhoneXxx01.equals(kmPhoneXxx02)) matchCount02++;

        StringBuilder buff = new StringBuilder();
        buff.append(enName);
        buff.append(TAB).append(kmDesired.equals(bestMatch) ? "TRUE" : "");
        buff.append(TAB).append(kmPhoneXxx01.equals(kmPhoneXxx02) ? "TRUE" : "");
        buff.append(TAB).append(kmDesired).append(TAB).append(kmPhonetic01).append(TAB).append(getUChars(kmDesired));
        buff.append(TAB).append(kmGenerated).append(TAB).append(getEnCharsInKm(kmGenerated)).append(TAB).append(choices.size());
        buff.append(TAB).append(bestMatch).append(TAB).append(kmPhonetic02).append(TAB).append(getUChars(bestMatch));

        return buff.toString();
    }

    static String getUChars(String text) {
        if (text == null) return "Uknown";
        return text.chars()
            .mapToObj(ch -> Integer.toHexString(ch).toUpperCase())
            .map(hex -> "U+" + StringUtils.leftPad(hex, 4, "0"))
            .collect(Collectors.joining(" "));
    }

    static void loadKmPhonetics() throws IOException {
        List<String> kmPhone = Files.readAllLines(Paths.get(BASE_DIR, KM_PHONETIC_FILE), StandardCharsets.UTF_8);
        kmPhone.stream()
            .map(km -> PlaceHelper.split(km, '\t'))
            .filter(kmp -> kmp.length > 1)
            .forEach(kmp -> km2Phonetic.put(kmp[0], kmp[1]));
        System.out.println("Phonetic count: " + km2Phonetic.size());
    }

    static void saveKmPhonetics() throws IOException {
        List<String> kmPhoneOutput = km2Phonetic.entrySet().stream()
            .map(entry -> entry.getKey() + "\t" + entry.getValue())
            .collect(Collectors.toList());
        Files.write(Paths.get(BASE_DIR, KM_PHONETIC_FILE), kmPhoneOutput, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
    }

    static String getKmPhonetic(String kmText) {
        String xlit = km2Phonetic.get(kmText);
        if (xlit == null) {
            String[] googleKM01 = GoogleTranslateUtil.kmToEn(kmText);
            xlit = googleKM01[1];
            km2Phonetic.put(kmText, xlit);
        }
        return xlit;
    }

    static String getEnCharsInKm(String text) {
        return text.chars()
                .filter(ch -> ch < 256)
                .mapToObj(ch -> "" + (char)ch)
                .collect(Collectors.joining(" "));
    }

    static List<String> getChoices(String text) {
        String[] chunks = PlaceHelper.split(text.replace('}', ' '), '{');
        if (chunks.length == 1) {
            return Arrays.asList(text);
        } else {
            return getChoices(Arrays.asList(chunks));
        }
    }

    static List<String> getChoices(List<String> chunks) {
        List<String> results = new ArrayList<>();

        if (chunks.isEmpty()) {
            results.add("");
        } else {
            List<String> tChunks = new ArrayList<>(chunks);
            String chunk = tChunks.remove(0);
            String[] choices = PlaceHelper.split(chunk, ',');
            for (String choice : choices) {
                for (String tail : getChoices(tChunks)) {
                    StringBuilder buff = new StringBuilder();
                    buff.append(choice.trim());
                    buff.append(tail.trim());
                    results.add(buff.toString());
                }
            }
        }

        return results;
    }

    static String getBestMatch(String text, List<String> choices) {
        String bestMatch = choices.stream()
            .filter(choice -> choice.equals(text))
            .findFirst()
            .orElse(null);

        if (choices.isEmpty()) {
            bestMatch = "";
        } else if (choices.size() == 1) {
            return choices.get(0);
        } else if (bestMatch == null) {
            int charMatchCnt = 0;
            for (String choice : choices) {
                int cnt = 0;
                char[] charsT = text.toCharArray();
                char[] charsC = choice.toCharArray();
                for (int tt=0;  tt<charsT.length;  tt++) {
                    for (int cc=0;  cc<charsC.length;  cc++) {
                        if (charsT[tt] == charsC[cc]) {
                            cnt++;
                            charsT[tt] = 0;
                            break;
                        }
                    }
                }
                if (cnt > charMatchCnt) {
                    charMatchCnt = cnt;
                    bestMatch = choice;
                }
            }
        }

        return (bestMatch == null) ? choices.get(0) : bestMatch;
    }
}
