/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class ParseNoResultsForNonDate {

    static final String path = "C:/temp/date-interp-no-results-more.txt";

    public static void main(String... args) throws Exception {
        Set<String> allWords = new TreeSet<>();

        List<String> results = Files.readAllLines(Paths.get(path), Charset.forName("UTF-8"));
        for (String line : results) {
            String[] chunk = PlaceHelper.split(line, '|');
            String[] words = PlaceHelper.split(chunk[0], ' ');
            for (String word : words) {
                String tWord = word.toLowerCase();
                if (isWord(tWord)) {
                    allWords.add(tWord);
                }
            }
        }

        allWords.forEach(System.out::println);
    }

    protected static boolean isWord(String tWord) {
        return tWord.chars().allMatch(ch -> (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z'));
    }
}
