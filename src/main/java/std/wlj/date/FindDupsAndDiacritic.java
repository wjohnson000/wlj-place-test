/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.familysearch.standards.core.LocalizedData;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.core.lang.TextUtil;
import org.familysearch.standards.date.common.MonthDictionary;

/**
 * @author wjohnson000
 *
 */
public class FindDupsAndDiacritic {

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
        findDupEntries();
        findDiacriticOnly();
    }

    static void findDupEntries() throws Exception {
        System.out.println("Duplicate entries ...\n");

        for (String dictName : DICT_NAMES) {
            List<String> lines = getDictionaryResource(dictName);
            Set<String> allEntries = new TreeSet<>();
            Set<String> dispEntries = new TreeSet<>();

            int linenum = 0;
            String wgType = "";
            for (String line : lines) {
                linenum++;
                if (line.trim().startsWith("<word-group ")) {
                    wgType = getAttr(line, "type");
//                    System.out.println(line);
                } else if (line.trim().startsWith("<word ")) {
                    String lang = getAttr(line, "lang");
                    String type = getAttr(line, "type");
                    String meta = getAttr(line, "meta");
                    String valu = getValue(line);

                    if ("display".equalsIgnoreCase(meta)) {
                        String entry = dictName + "|" + wgType + "|" + lang + "|" + type + "|" + meta + "|" + valu;
                        dispEntries.add(entry.toLowerCase());
                    }

                    boolean addIt = true;
                    if ("variant".equalsIgnoreCase(meta)) {
                        String entry = dictName + "|" + wgType + "|" + lang + "|" + type + "|" + "display" + "|" + valu;
                        if (dispEntries.contains(entry.toLowerCase())) {
                            addIt = false;
                            entry = dictName + "|" + wgType + "|" + lang + "|" + type + "|" + meta + "|" + valu;
                            System.out.println(entry + "|" + linenum + "|display");
                        }
                    }

                    String entry = dictName + "|" + wgType + "|" + lang + "|" + type + "|" + meta + "|" + valu;
                    if (allEntries.contains(entry)) {
                        addIt = false;
                        System.out.println(entry + "|" + linenum + "|duplicate");
                    }
                    if (addIt) {
//                        System.out.println(line);
                    }
                    allEntries.add(entry);
                } else {
//                    System.out.println(line);
                }
            }
        }
    }

    static void findDiacriticOnly() throws Exception {
        System.out.println("Diacritic only ...\n");

        for (String dictName : DICT_NAMES) {
            Set<String> entries = new TreeSet<>();
            List<String> lines = getDictionaryResource(dictName);

            // First pass ... save all of the values
            for (String line : lines) {
                if (line.trim().startsWith("<word ")) {
                    String valu = getValue(line);
                    entries.add(valu.toLowerCase());
                }
            }

            // Second pass ... remove diacritics and see if we have the plain word ...
            String wgType = "";
            for (String line : lines) {
                if (line.trim().startsWith("<word-group ")) {
                    wgType = getAttr(line, "type");
                } else if (line.trim().startsWith("<word ")) {
                    String lang = getAttr(line, "lang");
                    String type = getAttr(line, "type");
                    String meta = getAttr(line, "meta");
                    String valu = getValue(line);

                    String tValu = TextUtil.standardizeApostrophes(new LocalizedData<String>(valu, StdLocale.UNDETERMINED)).get();
                    tValu = TextUtil.removeDiacritics(new LocalizedData<String>(tValu, StdLocale.UNDETERMINED)).get();
                    tValu = tValu.toLowerCase();
                    if (! entries.contains(tValu)) {
                        System.out.println(dictName + "|" + wgType + "|" + lang + "|" + type + "|" + meta + "|" + valu + "|" + tValu);
                    }
                }
            }
        }
    }

    static List<String> getDictionaryResource(String fileName) throws Exception {
        return Files.readAllLines(Paths.get(MonthDictionary.class.getResource(fileName).toURI()));
    }

    static String getAttr(String line, String key) {
        int ndx0 = line.indexOf(key + "=\"");
        int ndx1 = line.indexOf('"', ndx0);
        int ndx2 = line.indexOf('"', ndx1+1);

        return (ndx2 == -1) ? "" : line.substring(ndx1+1, ndx2);
    }

    static String getValue(String line) {
        int ndx0 = line.indexOf('>');
        int ndx1 = line.indexOf('<', ndx0+1);

        return (ndx1 == -1) ? "" : line.substring(ndx0+1, ndx1);
    }
}
