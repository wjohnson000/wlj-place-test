package std.wlj.xlit;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.familysearch.standards.place.util.PlaceHelper;

public class AnalyzeRules {

    static final String INPUT_FILE  = "phone_table.txt";

    public static void main(String...args) throws Exception {
        if (args.length == 0) {
            System.out.println("Must supply the base directory of the 'phone_table.txt' file.");
            System.exit(0);
        }
        String baseDir = args[0];

        Map<String, Integer>     kmIndex       = new HashMap<>();
        Map<String, Set<String>> kmManagesEn   = new TreeMap<>();
        Map<String, Set<String>> enManagedByKm = new TreeMap<>();
        Map<String, Set<String>> enPatternsXX  = new TreeMap<>();

        List<String> lines = Files.readAllLines(Paths.get(baseDir, INPUT_FILE), StandardCharsets.UTF_8);
        List<String[]> rules = collectRules(lines);

        int kmNdx = 1;
        for (String[] rule : rules) {
            String kmThingey  = rule[0];
            String enPatterns = rule[3];
            if (! kmThingey.isEmpty()  &&  kmThingey.charAt(0) > 132) {
                kmIndex.put(kmThingey, kmNdx);
                Set<String> manages = getUniqueChars(enPatterns);

                // Save the KM --> English stuff
                if (kmManagesEn.containsKey(kmThingey)) {
                    System.out.println("Duplicate -- " + kmThingey);
                }
                kmManagesEn.put(kmThingey, manages);

                // Save the English --> KM stuff
                for (String mangedBy : manages) {
                    Set<String> current = enManagedByKm.get(mangedBy);
                    if (current == null) {
                        current = new TreeSet<>();
                        enManagedByKm.put(mangedBy, current);
                    }
                    current.add(kmThingey);
                }

                // Save full patterns
                Set<String> patterns = getPatterns(enPatterns);
                for (String pattern : patterns) {
                    Set<String> current = enPatternsXX.get(pattern);
                    if (current == null) {
                        current = new TreeSet<>();
                        enPatternsXX.put(pattern, current);
                    }
                    current.add(kmThingey);
                }
            }
            kmNdx += 4;
        }

        System.out.println("=========================================================================================");
        System.out.println("Target language character[s] and the possible Source character[s] that can generate them.");
        System.out.println("=========================================================================================");
        kmManagesEn.entrySet().forEach(entry -> System.out.println(kmIndex.get(entry.getKey()) + "\t" + entry.getKey() + "\t" + getUChars(entry.getKey()) + "\t" + entry.getValue()));

        System.out.println("\n\n=========================================================================================");
        System.out.println("Source language character[s] and the possible Target character[s] they can be mapped to.");
        System.out.println("=========================================================================================");
        enManagedByKm.entrySet().forEach(entry -> System.out.println(entry.getKey() + "\t" + entry.getValue().stream().map(km -> km + "(" + kmIndex.get(km) + ")").collect(Collectors.joining(", ", "[", "]"))));

        System.out.println("\n\n=========================================================================================");
        System.out.println("Source langage patterns that appear in more than one rule.");
        System.out.println("=========================================================================================");
        enPatternsXX.entrySet().stream().filter(entry -> entry.getValue().size() > 1).forEach(entry -> System.out.println(entry.getKey() + "\t" + entry.getValue().stream().map(km -> km + "(" + kmIndex.get(km) + ")").collect(Collectors.joining(", ", "[", "]"))));
    }

    static List<String[]> collectRules(List<String> lines) {
        List<String[]> rules = new ArrayList<>();

        int ssize = lines.size();
        for (int ndx=0;  ndx<lines.size();  ndx+=4) {
            String[] rule = { "", "", "", "" };
            if (ndx   < ssize) rule[0] = lines.get(ndx);
            if (ndx+1 < ssize) rule[1] = lines.get(ndx+1);
            if (ndx+2 < ssize) rule[2] = lines.get(ndx+2);
            if (ndx+3 < ssize) rule[3] = lines.get(ndx+3);
            rules.add(rule);
        }

        return rules;
    }

    static String getUChars(String text) {
        if (text == null) return "Uknown";
        return text.chars()
                .mapToObj(ch -> Integer.toHexString(ch).toUpperCase())
                .map(hex -> "U+" + leftPad(hex, 4, '0'))
                .collect(Collectors.joining(" "));
    }

    static Set<String> getUniqueChars(String enPatterns) {
        Set<String> results = new TreeSet<>();

        int ndx0 = enPatterns.indexOf('[');
        int ndx1 = enPatterns.indexOf(']');
        if (ndx0 >= 0  &&  ndx1 > ndx0) {
            String patternStr = enPatterns.substring(ndx0+1, ndx1);
            String[] patterns = PlaceHelper.split(patternStr, ',');
            for (String pattern : patterns) {
                if (pattern.startsWith("{")) {
                    ndx1 = pattern.indexOf('}');
                    pattern = pattern.substring(ndx1+1);
                }
                if (pattern.endsWith("}")) {
                    ndx0 = pattern.indexOf("{");
                    pattern = pattern.substring(0, ndx0);
                }
                results.add(pattern);
            }
        }

        return results;
    }

    static Set<String> getPatterns(String enPatterns) {
        Set<String> results = new TreeSet<>();

        int ndx0 = enPatterns.indexOf('[');
        int ndx1 = enPatterns.indexOf(']');
        if (ndx0 >= 0  &&  ndx1 > ndx0) {
            String patternStr = enPatterns.substring(ndx0+1, ndx1);
            String[] patterns = PlaceHelper.split(patternStr, ',');
            Arrays.stream(patterns).forEach(ptn -> results.add(ptn));
        }

        return results;
    }

    /**
     * Convenience method to left-pad a String with some character.
     * 
     * @param inStr String to left-pad
     * @param size resulting size of output string
     * @param ch padding character
     * @return
     */
    static String leftPad(String inStr, int size, char ch) {
        String result = inStr;
        char padCh = (ch < 32) ? ' ' : ch;

        while (result.length() < size) {
            result = padCh + result;
        }
        return result;
    }

}
