package std.wlj.xlit;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class KmRuleGenerate {

    static final String BASE_DIR      = "D:/xlit/km/truth";
    static final String NEW_RULE_FILE = "phone_table_gen.txt";

    static final String[] RULE_HEADER = {
        "SURFACE_FORM        Version 1.0.0",
        "INFO_ABOUT_FORM",
        "ALTERNATE_FORM",
        "RULESET_PER_LANGUAGE"
    };

    static final String NULL_TARGET =
        "en[—,^,\\~,\\$,-,a{a},{e}y{\\$},e{i},i{i},{i}e{\\$},b{b},{b}h,c{c},d{d},f{f},k{k},l{l},m{m}," +
        "n{n},p{p},s{s},t{t},c{k},{^}a{en},{ne}s{\\$},{ch}s{\\$},{ac}s{\\$},{^}k{n},{^k}h,o{uis}]";

    static Map<Character,List<String>> kmToEnConsonants = new TreeMap<>();
    static {
        kmToEnConsonants.put('\u1780', Arrays.asList("k", "kh", "c[aou]", "qu"));
        kmToEnConsonants.put('\u1781', Arrays.asList("k", "kh", "c[aou]", "qu"));
        kmToEnConsonants.put('\u1782', Arrays.asList("g[aou]"));
        kmToEnConsonants.put('\u1783', Arrays.asList());
        kmToEnConsonants.put('\u1784', Arrays.asList("ng"));
        kmToEnConsonants.put('\u1785', Arrays.asList("ch", "tch", "sch", "tsch"));
        kmToEnConsonants.put('\u1786', Arrays.asList("ch", "tch", "sch", "tsch"));
        kmToEnConsonants.put('\u1787', Arrays.asList("j", "g[eiy]"));
        kmToEnConsonants.put('\u1788', Arrays.asList("j", "g[eiy]"));
        kmToEnConsonants.put('\u1789', Arrays.asList());
        kmToEnConsonants.put('\u178A', Arrays.asList());
        kmToEnConsonants.put('\u178B', Arrays.asList("t", "th"));
        kmToEnConsonants.put('\u178C', Arrays.asList());
        kmToEnConsonants.put('\u178D', Arrays.asList("d"));
        kmToEnConsonants.put('\u178E', Arrays.asList("n", "hn"));
        kmToEnConsonants.put('\u178F', Arrays.asList());
        kmToEnConsonants.put('\u1790', Arrays.asList());
        kmToEnConsonants.put('\u1791', Arrays.asList());
        kmToEnConsonants.put('\u1792', Arrays.asList());
        kmToEnConsonants.put('\u1793', Arrays.asList());
        kmToEnConsonants.put('\u1794', Arrays.asList());
        kmToEnConsonants.put('\u1795', Arrays.asList("p"));
        kmToEnConsonants.put('\u1796', Arrays.asList("b", "bh"));
        kmToEnConsonants.put('\u1797', Arrays.asList());
        kmToEnConsonants.put('\u1798', Arrays.asList("m"));
        kmToEnConsonants.put('\u1799', Arrays.asList("y"));  // TODO use this?
        kmToEnConsonants.put('\u179A', Arrays.asList("r"));
        kmToEnConsonants.put('\u179B', Arrays.asList("l"));
        kmToEnConsonants.put('\u179C', Arrays.asList("v", "f", "ph", "pf"));
        kmToEnConsonants.put('\u179D', Arrays.asList());
        kmToEnConsonants.put('\u179E', Arrays.asList());
        kmToEnConsonants.put('\u179F', Arrays.asList("s", "z", "sz", "c[eiy]"));
        kmToEnConsonants.put('\u17A0', Arrays.asList("h"));
        kmToEnConsonants.put('\u17A1', Arrays.asList());
        kmToEnConsonants.put('\u17A2', Arrays.asList(""));
    }

    static Map<Character, List<String>> kmToEnVowels = new TreeMap<>();
    static {
        kmToEnVowels.put('\u17B6', Arrays.asList("aw"));
        kmToEnVowels.put('\u17B7', Arrays.asList("e", "i", "ie"));
        kmToEnVowels.put('\u17B8', Arrays.asList("ee", "ae", "ey", "ei"));
        kmToEnVowels.put('\u17B9', Arrays.asList(""));
        kmToEnVowels.put('\u17BA', Arrays.asList("u"));
        kmToEnVowels.put('\u17BB', Arrays.asList("oo", "ew"));
        kmToEnVowels.put('\u17BC', Arrays.asList(""));
        kmToEnVowels.put('\u17BD', Arrays.asList(""));
        kmToEnVowels.put('\u17BE', Arrays.asList(""));
        kmToEnVowels.put('\u17BF', Arrays.asList(""));
        kmToEnVowels.put('\u17C0', Arrays.asList(""));
        kmToEnVowels.put('\u17C1', Arrays.asList(""));
        kmToEnVowels.put('\u17C2', Arrays.asList("a", "ai", "ay"));
        kmToEnVowels.put('\u17C3', Arrays.asList(""));
        kmToEnVowels.put('\u17C4', Arrays.asList("o"));
        kmToEnVowels.put('\u17C5', Arrays.asList("au", "ow"));
    }

    static Set<Character> kmInitial = new TreeSet<>();
    static {
        kmInitial.add('\u1781');
        kmInitial.add('\u1786');
        kmInitial.add('\u1788');
    }

    static List<String> rules = new ArrayList<>();


    public static void main(String...args) throws IOException {
        addHeaderAndNull();        
        kmToEnConsonants.entrySet().forEach(entry -> processKM(entry.getKey(), entry.getValue()));
        Files.write(Paths.get(BASE_DIR, NEW_RULE_FILE), rules, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static void addHeaderAndNull() {
        Arrays.stream(RULE_HEADER).forEach(line -> rules.add(line));
        rules.add("");
        rules.add("empty");
        rules.add("NULL");
        rules.add(NULL_TARGET);
    }

    static void processKM(char kmCh, List<String> prefixes) {
        if (prefixes == null  ||  prefixes.isEmpty()) {
            return;
        }

        StringBuilder buff = new StringBuilder();
        buff.append(kmCh);
        String kmRule = buff.toString();

        boolean isInitial = kmInitial.contains(kmCh);
        List<String> pfxOnly = getPrefixesOnly(prefixes);
        addRule(kmRule, isInitial, pfxOnly);

        for (Map.Entry<Character, List<String>> entry : kmToEnVowels.entrySet()) {
            String kmRuleX = kmRule + entry.getKey();
            List<String> andVowels = getPrefixAndVowel(prefixes, entry.getValue());
            addRule(kmRuleX, isInitial, andVowels);
        }
    }

    static void addRule(String kmChunk, boolean isInitialOnly, List<String> entries) {
        if (! entries.isEmpty()) {
            rules.add(kmChunk);
            rules.add("blah");
            rules.add(kmChunk);
            rules.add(
                entries.stream()
                    .map(rule -> (isInitialOnly) ? "{^}" + rule : rule)
                    .collect(Collectors.joining(",", "en[", "]")));
        }
    }

    static List<String> getPrefixesOnly(List<String> prefixes) {
        return prefixes.stream()
            .map(pfx -> {
                int ndx = pfx.indexOf('[');
                return (ndx > 0) ? pfx.substring(0, ndx) : pfx;
            })
            .collect(Collectors.toList());
    }

    static List<String> getPrefixAndVowel(List<String> prefixes, List<String> vowels) {
        List<String> combos = new ArrayList<>();

        if (vowels.size() > 1  ||  ! vowels.get(0).isEmpty()) {
            for (String prefix : prefixes) {
                String okVowels = "aeiouy";
                int ndx = prefix.indexOf('[');
                if (ndx > 0) {
                    okVowels = prefix.substring(ndx+1);
                    prefix = prefix.substring(0, ndx);
                }
                for (String vowel : vowels) {
                    String vowel01 = vowel.substring(0, 1);
                    if (okVowels.contains(vowel01)) {
                        combos.add(prefix + vowel);
                    }
                }
            }
        }

        return combos;
    }
}
