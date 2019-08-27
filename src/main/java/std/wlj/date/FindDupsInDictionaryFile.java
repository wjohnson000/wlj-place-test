/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.date.common.MonthDictionary;

/**
 * @author wjohnson000
 *
 */
public class FindDupsInDictionaryFile {

    private static String[] DICT_NAMES = {
//        "frc.xml",
//        "imperial_ja.xml",
//        "imperial_ko.xml",
//        "imperial_zh.xml",
//        "misc-dict.xml",
//        "modifier-dict.xml",
        "month-dict.xml",
//        "numbers.xml",
    };

    public static void main(String... args) throws Exception {
        
        for (String dictName : DICT_NAMES) {
            List<String> lines = getDictionaryResource(dictName);
            List<String> outLines = new ArrayList<>(lines.size());
            for (String line : lines) {
                if (outLines.contains(line)  &&  line.contains("<word ")) {
                    System.out.println(line);
                } else {
                    outLines.add(line);
                }
            }

            System.out.println("\n\n");
            System.out.println("========================================================================");
            System.out.println(dictName);
            System.out.println();
            outLines.forEach(System.out::println);
            System.out.println("========================================================================");
        }

    }

    static List<String> getDictionaryResource(String fileName) throws Exception {
        return Files.readAllLines(Paths.get(MonthDictionary.class.getResource(fileName).toURI()));
    }
}
