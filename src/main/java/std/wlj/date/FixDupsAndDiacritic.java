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
public class FixDupsAndDiacritic {

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
        findAndFixEntries();
        
    }

    static void findAndFixEntries() throws Exception {
        System.out.println("Find and fix duplicate entries and diacritics ...\n");

        // First pass ... keep track of unique entries that are to be preserved
        for (String dictName : DICT_NAMES) {
            List<String> lines = getDictionaryResource(dictName);
            Set<String> entries = new TreeSet<>();
            
            String wgType = "";
            for (String line : lines) {
                if (line.trim().startsWith("<word-group ")) {
                    wgType = getAttr(line, "type");
                } else if (line.trim().startsWith("<word ")) {
                    String lang = getAttr(line, "lang");
                    String type = getAttr(line, "type");
                    String meta = getAttr(line, "meta");
                    String valu = getValue(line);

                    boolean addit = true;
                    String entry = dictName + "|" + wgType + "|" + lang + "|" + type + "|" + meta + "|" + valu;
                    if ("variant".equals(meta)) {
                        String entryD = dictName + "|" + wgType + "|" + lang + "|" + type + "|" + "display" + "|" + valu;
                        if (entries.contains(entryD)) {
                            addit = false;
                        }
                    }
                    if (addit) {
                        entries.add(entry);
                    }
                }
            }

            // Second pass ... remove diacritics and see if we have the plain word ...
            wgType = "";
            for (String line : lines) {
                if (line.trim().startsWith("<word-group ")) {
                    System.out.println(line);
                    wgType = getAttr(line, "type");
                } else if (line.trim().startsWith("<word ")) {
                    String lang = getAttr(line, "lang");
                    String type = getAttr(line, "type");
                    String meta = getAttr(line, "meta");
                    String valu = getValue(line);

                    String entry = dictName + "|" + wgType + "|" + lang + "|" + type + "|" + meta + "|" + valu;
                    if (entries.contains(entry)) {
                        System.out.println(line);
                    }
                    
                    String tValu = TextUtil.standardizeApostrophes(new LocalizedData<String>(valu, StdLocale.UNDETERMINED)).get();
                    tValu = TextUtil.removeDiacritics(new LocalizedData<String>(tValu, StdLocale.UNDETERMINED)).get();
                    String entryNoDiac = dictName + "|" + wgType + "|" + lang + "|" + type + "|" + "variant" + "|" + tValu;
                    if (! tValu.equalsIgnoreCase(valu)  &&  ! entries.contains(entryNoDiac)) {
                        StringBuilder buff = new StringBuilder();
                        buff.append("    <word");
                        buff.append(" ").append(formatAttr("lang", lang));
                        buff.append(" ").append(formatAttr("type", type));
                        buff.append(" ").append(formatAttr("meta", "variant"));
                        buff.append(">").append(tValu).append("</word>");
                        System.out.println(buff.toString());
                    }
                } else {
                    System.out.println(line);
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

    static String formatAttr(String key, String value) {
        return key + "=" + '"' + value + '"';
    }
}
