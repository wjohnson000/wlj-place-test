/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.familysearch.standards.core.lang.dict.Dictionary;
import org.familysearch.standards.core.lang.dict.Word;
import org.familysearch.standards.date.shared.ModifierDictionary;

/**
 * @author wjohnson000
 *
 */
public class CheckfForMoreNonDateWords {

    public static void main(String...args) throws Exception {
        Dictionary modDict = ModifierDictionary.getModifierDictionary();
        List<Word> nonDates = modDict.getWordsByType("non-date");

        List<String> possiblesX = Files.readAllLines(Paths.get("C:/temp/non-date-words-more.txt"), StandardCharsets.UTF_8);
        Set<String>  possibles = new TreeSet<>(possiblesX);

        for (Word word : nonDates) {
            Set<String> types = new TreeSet<>(word.getTypes());
            types.remove("non-date");
            String type = types.stream().findFirst().orElse("???");
            System.out.println(word.getText() + "|" + type);
            possibles.remove(word.getText().toLowerCase());
        }

        possibles.forEach(System.out::println);
    }
}
