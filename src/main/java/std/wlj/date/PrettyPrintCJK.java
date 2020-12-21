/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.*;

import org.familysearch.standards.core.lang.dict.Dictionary;
import org.familysearch.standards.core.lang.dict.DictionaryFactory;
import org.familysearch.standards.core.lang.dict.Word;
import org.familysearch.standards.date.common.ImperialDictionary;

/**
 * @author wjohnson000
 *
 */
public class PrettyPrintCJK {

    static class Thing implements Comparable<Thing> {
        String id;
        int    fromYear;
        int    toYear;
        Map<String, String> textByLang = new HashMap<>();

        @Override
        public int compareTo(Thing that) {
            if (this.fromYear == that.fromYear) {
                return this.toYear - that.toYear;
            } else {
                return this.fromYear - that.fromYear;
            }
        }
    }

    private static Map<String, Thing> empThings = new HashMap<>();
    private static Map<String, Thing> rgnThings = new HashMap<>();
    private static List<String> outputRows = new ArrayList<>();

    public static void main(String... args) {
        Dictionary zhDict = DictionaryFactory.createDictionaryFromXML(ImperialDictionary.class.getResource("imperial_zh.xml"));
        List<Word> tangs = zhDict.getWordsByType("dynasty");

        Word tangWord = tangs.stream()
            .filter(ww -> ww.getText().equals("唐"))  // Tang dynasty
            .findFirst().orElse(null);
        Thing tangDyn = fromWord(tangWord);

        List<Word> empWords = zhDict.getWordsByType(tangDyn.id);
        for (Word empWord : empWords) {
            Thing tempThing = fromWord(empWord);
            Thing empThing = empThings.computeIfAbsent(tempThing.id, kk -> tempThing);
            empThing.textByLang.put(empWord.getLanguage().toString(), empWord.getText());
        }

        List<Thing> tempEmpList = new ArrayList<>(empThings.values());
        Collections.sort(tempEmpList);
        for (Thing empThing : tempEmpList) {
            System.out.println("\nEMP: " + empThing.id);
            System.out.println("yrs: " + empThing.fromYear + "-" + empThing.toYear);
            System.out.println("txt: " + empThing.textByLang);

            rgnThings.clear();
            List<Word> rgnWords = zhDict.getWordsByType(empThing.id);
            for (Word rgnWord : rgnWords) {
                Thing tempThing = fromWord(rgnWord);
                Thing rgnThing = rgnThings.computeIfAbsent(tempThing.id, kk -> tempThing);
                rgnThing.textByLang.put(rgnWord.getLanguage().toString(), rgnWord.getText());
            }

            boolean first = true;
            List<Thing> tempRgnList = new ArrayList<>(rgnThings.values());
            Collections.sort(tempRgnList);
            for (Thing rgnThing : tempRgnList) {
                System.out.println("\n  RGN: " + rgnThing.id);
                System.out.println("  yrs: " + rgnThing.fromYear + "-" + rgnThing.toYear);
                System.out.println("  txt: " + rgnThing.textByLang);

                StringBuilder buff = new StringBuilder();
                if (first) {
                    first = false;
                    outputRows.add("");

                    String[] names = getNames(empThing);
                    buff.append(names[0]);
                    buff.append("|").append(names[1]);
                    buff.append("|").append(names[2]);
                    buff.append("|").append(empThing.fromYear);
                    buff.append("|").append(empThing.toYear);
                } else {
                    buff.append("||||");
                }

                String[] names = getNames(rgnThing);
                buff.append("|").append(names[0]);
                buff.append("|").append(names[1]);
                buff.append("|").append(names[2]);
                buff.append("|").append(rgnThing.fromYear);
                buff.append("|").append(rgnThing.toYear);
                outputRows.add(buff.toString());
            }
        }

        System.out.println("\n\n");
        outputRows.forEach(System.out::println);
    }

    static Thing fromWord(Word word) {
        Thing thing = new Thing();

        thing.id = ImperialDictionary.parseIdFromMeta(word.getMetadata());
        thing.fromYear = ImperialDictionary.parseStartYearFromMeta(word.getMetadata());
        thing.toYear = ImperialDictionary.parseEndYearFromMeta(word.getMetadata());
        thing.textByLang.put("zh", "");
        thing.textByLang.put("zh-Hant", "");
        thing.textByLang.put("zh-Hans", "");
        thing.textByLang.put("zh-Hani", "");

        return thing;
    }

    static String[] getNames(Thing thing) {
        String[] names = { "", "", "" };

        int ndx = 0;
        if (! thing.textByLang.get("zh").isEmpty()) {
            names[ndx++] = thing.textByLang.get("zh");
        }
        if (! thing.textByLang.get("zh-Hant").isEmpty()) {
            names[ndx++] = thing.textByLang.get("zh-Hant");
        }
        if (! thing.textByLang.get("zh-Hans").isEmpty()) {
            names[ndx++] = thing.textByLang.get("zh-Hans");
        }
        if (! thing.textByLang.get("zh-Hani").isEmpty()) {
            names[ndx++] = thing.textByLang.get("zh-Hani");
        }

        return names;
    }
}
