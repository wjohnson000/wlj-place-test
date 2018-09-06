/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.familysearch.standards.core.lang.dict.Dictionary;
import org.familysearch.standards.core.lang.dict.DictionaryFactory;
import org.familysearch.standards.core.lang.dict.Word;
import org.familysearch.standards.date.shared.MonthDictionary;

/**
 * @author wjohnson000
 *
 */
public class CheckNonDateWords {

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
        Dictionary masterDictionary = getMasterDictionary();
        List<String> textes = Files.readAllLines(Paths.get("C:/temp/words-non-date.txt"), Charset.forName("UTF-8"));
        Set<String> candidates = textes.stream().map(wd -> wd.toLowerCase()).collect(Collectors.toSet());
        Set<String> words = new TreeSet<>(candidates);
        

        for (String word : words) {
            List<Word> matches = masterDictionary.findWords(word);
            if (matches.isEmpty()) {
                System.out.println(word);
            } else {
                System.out.println(word + " --> found");
            }
        }
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
