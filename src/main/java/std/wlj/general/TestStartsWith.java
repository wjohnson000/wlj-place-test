/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.familysearch.standards.core.lang.TextComparisonUtil;

public class TestStartsWith {

    static Random random = new Random();

    public static void main(String...args) {
        String testName = "Wayne Johnson";
        for (int i=0;  i<10;  i++) {
            List<String> allWords = makeUpWords();
            runVersion03(testName, allWords);
            runVersion04(testName, allWords);
            runVersion02(testName, allWords);
            runVersion01(testName, allWords);
            System.out.println();
        }
    }

    static List<String> makeUpWords() {
        List<String> words = new ArrayList<>();
        for (int i=0;  i<107_000;  i++) {
            int len = random.nextInt(32) + 1;
            StringBuilder buff = new StringBuilder();
            for (int j=0;  j<len;  j++) {
                char ch = (char)(random.nextInt(130) + 33);
                buff.append(ch);
            }
            words.add(buff.toString());
        }
        words.add("Wa");
        words.add("Way");
        words.add("Wayn");
        return words;
    }

    static void runVersion01(String testName, List<String> allWords) {
        long time0 = System.nanoTime();
        List<String> matches = allWords.stream()
            .filter(word -> ! word.trim().isEmpty())
            .filter(word -> testName.trim().toLowerCase().startsWith(word.trim().toLowerCase()))
            .collect(Collectors.toList());
        long time1 = System.nanoTime();
        System.out.println("Matches-01: " + matches.size() + " --> " + (time1-time0)/1_000_000.0);
    }

    static void runVersion02(String testName, List<String> allWords) {
        String tempName = testName.trim().toLowerCase();
        long time0 = System.nanoTime();
        List<String> matches = allWords.stream()
            .filter(word -> ! word.trim().isEmpty())
            .filter(word -> tempName.startsWith(word.trim().toLowerCase()))
            .collect(Collectors.toList());
        long time1 = System.nanoTime();
        System.out.println("Matches-02: " + matches.size() + " --> " + (time1-time0)/1_000_000.0);
    }

    static void runVersion03(String testName, List<String> allWords) {
        String tempName = testName.trim().toLowerCase();
        long time0 = System.nanoTime();
        List<String> matches = allWords.stream()
            .filter(word -> ! word.trim().isEmpty())
            .filter(word -> startsWithIgnoreCase(tempName, word))
            .collect(Collectors.toList());
        long time1 = System.nanoTime();
        System.out.println("Matches-03: " + matches.size() + " --> " + (time1-time0)/1_000_000.0);
    }

    static void runVersion04(String testName, List<String> allWords) {
        String tempName = testName.trim().toLowerCase();
        long time0 = System.nanoTime();
        List<String> matches = allWords.stream()
            .filter(word -> ! word.trim().isEmpty())
            .filter(word -> TextComparisonUtil.startsWithIgnoreCase(tempName, word))
            .collect(Collectors.toList());
        long time1 = System.nanoTime();
        System.out.println("Matches-04: " + matches.size() + " --> " + (time1-time0)/1_000_000.0);
    }

    static boolean startsWithIgnoreCase(String str, String prefix) {
        String tempStr = str.trim();
        String tempPrefix = prefix.trim();

        if (tempStr.length() < tempPrefix.length()) {
            return false;
        }

        for (int i=0;  i<tempPrefix.length();  i++) {
            char ch1 = tempStr.charAt(i);
            char ch2 = tempPrefix.charAt(i);
            if (ch1 != ch2  &&  Character.toUpperCase(ch1) != Character.toUpperCase(ch2)) {
                return false;
            }
        }

        return true;
    }
}
