/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.familysearch.standards.core.lang.dict.Dictionary;
import org.familysearch.standards.core.lang.dict.DictionaryFactory;
import org.familysearch.standards.core.lang.dict.Word;
import org.familysearch.standards.date.common.MonthDictionary;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class AssistedNonDateWords {

    private static String[] DICT_NAMES = {
        "frc.xml",
        "imperial_ja.xml",
        "imperial_ko.xml",
        "imperial_zh.xml",
        "misc-dict.xml",
        "modifier-dict.xml",
        "month-dict.xml",
        "numbers.xml",
    };

    public static void main(String... args) throws Exception {
        Set<String> words = new TreeSet<>();
        Dictionary masterDictionary = getMasterDictionary();

        List<String> notFound = new ArrayList<>();

        List<String> textes = Files.readAllLines(Paths.get("C:/temp/date-assisted.txt"), StandardCharsets.UTF_8);
        for (String text : textes) {
            String[] chunks = PlaceHelper.split(text, '|');
            String dateStr = chunks[0].chars()
                .filter(ch -> (ch >= 'A' && ch <= 'Z')  ||  (ch >= 'a' && ch <= 'z')  ||  (ch > 128)  ||  (ch == ' '))
                .mapToObj(ch -> "" + (char)ch)
                .collect(Collectors.joining(""));

            chunks = PlaceHelper.split(dateStr, ' ');
            Arrays.stream(chunks).forEach(word -> words.add(word));

        }

        System.out.println();
        for (String word : words) {
            List<Word> matches = masterDictionary.findWords(word);
            if (matches.isEmpty()) {
                matches = masterDictionary.findWords(word.toLowerCase());
            }

            if (matches.isEmpty()) {
                notFound.add(word);
            }
        }

        Files.write(Paths.get("C:/temp/words-found-no.txt"), notFound, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static Dictionary getMasterDictionary() {
        Dictionary combinedDictionary = DictionaryFactory.createEmptyDictionary();

        for (String dictName : DICT_NAMES) {
            System.out.println("Processing dictionary: " + dictName);
            Dictionary tempDict = DictionaryFactory.createDictionaryFromXML(MonthDictionary.class.getResource(dictName));
            combinedDictionary.mergeDictionary(tempDict);
        }

        return combinedDictionary;
    }
}
