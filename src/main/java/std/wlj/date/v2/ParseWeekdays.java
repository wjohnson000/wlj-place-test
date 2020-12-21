/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.familysearch.standards.core.LocalizedData;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.core.lang.TextUtil;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class ParseWeekdays {

    static String WORD_PATTERN = "    <word lang=\"%s\" meta=\"%d\">%s</word>";
    static List<List<String>> wordXML = new ArrayList<>();

    public static void main(String...args) throws Exception {
        // Create seven empty lists, one per day of the week
        for (int i=0;  i<7;  i++) {
            wordXML.add(new ArrayList<>());
        }

        // OK, let's do this!!
        List<String> lines = Files.readAllLines(Paths.get("C:/temp/weekday-names.txt"), StandardCharsets.UTF_8);
        for (String line : lines) {
            String[] chunks = PlaceHelper.split(line, '\t');
            if (chunks.length == 9  &&  Arrays.stream(chunks).allMatch(cc -> cc.trim().length() > 0)) {
                String locale = chunks[1];
                if (chunks[0].contains("Romanized")) {
                    locale += "-Latn";
                }

                if (! "ISO".equals(locale)) {
                    wordXML.get(0).addAll(processWeekday(locale, 1, chunks[3]));
                    wordXML.get(1).addAll(processWeekday(locale, 2, chunks[4]));
                    wordXML.get(2).addAll(processWeekday(locale, 3, chunks[5]));
                    wordXML.get(3).addAll(processWeekday(locale, 4, chunks[6]));
                    wordXML.get(4).addAll(processWeekday(locale, 5, chunks[7]));
                    wordXML.get(5).addAll(processWeekday(locale, 6, chunks[8]));
                    wordXML.get(6).addAll(processWeekday(locale, 7, chunks[2]));
                }
            }
        }

        wordXML.forEach(xml -> {
            System.out.println();
            Collections.sort(xml);
            xml.forEach(System.out::println);
        });
    }

    static List<String> processWeekday(String locale, int meta, String name) {
        List<String> results = new ArrayList<>();

        String tName = name.toLowerCase();
        results.add(String.format(WORD_PATTERN, locale, meta, tName));

        LocalizedData<String> tNameNoDiacritic = TextUtil.removeDiacritics(new LocalizedData<String>(tName, StdLocale.makeLocale(locale)));
        if (! tName.equals(tNameNoDiacritic.get())) {
            results.add(String.format(WORD_PATTERN, locale, meta, tNameNoDiacritic.get()));
        }

        String tNameNormalized = PlaceHelper.normalize(tName);
        if (! tName.equals(tNameNormalized)  &&  ! tNameNormalized.equals(tNameNoDiacritic.get())) {
            results.add(String.format(WORD_PATTERN, locale, meta, tNameNormalized));
        }

        return results;
    }
}
