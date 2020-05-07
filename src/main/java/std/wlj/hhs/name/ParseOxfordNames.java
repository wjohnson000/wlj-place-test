/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

/**
 * Look at the name files from "Oxford", list names, variants, etc ...
 * 
 * @author wjohnson000
 *
 */
public class ParseOxfordNames {

    private static final String MALE_CHAR = "♂";
    private static final String FEMALE_CHAR = "♀";

    private static String BASE_DIR = "C:/D-drive/homelands/names";
    private static String FIRST_FILE = "first_acref_9780198610601.xml";
    private static String LAST_FILE  = "last_acref_9780195081374.xml";

    private static Map<String, NameDef> allNames = new HashMap<>();

    private static List<String> results = new ArrayList<>(50_000);
    
    public static void main(String... args) throws Exception {
        process(FIRST_FILE);
//        process(LAST_FILE);
    }

    static void process(String file) throws Exception {
        List<String> rows = Files.readAllLines(Paths.get(BASE_DIR, file), StandardCharsets.UTF_8);

        for (String row : rows) {
            NameDef nameDef = parseRow(row);
            if (nameDef != null) {
                if (allNames.containsKey(nameDef.id)) {
                    System.out.println("Duplicate key: " + nameDef.id);
                } else {
                    allNames.put(nameDef.id, nameDef);
                }
            }
        }

        // Tie variants to their "master" name, and save the "Master" ones
        List<NameDef> masterNames = new ArrayList<>();
        List<NameDef> badMasterNames = new ArrayList<>();

        for (NameDef nameDef : allNames.values()) {
            if (nameDef.refId == null) {
                if (nameDef.text == null) {
                    badMasterNames.add(nameDef);
                } else {
                    masterNames.add(nameDef);
                }
            } else {
                NameDef parent = allNames.get(nameDef.refId);
                if (parent == null) {
                    System.out.println("Missing parent: " + nameDef.text + " --> " + nameDef.refId);
                } else {
                    parent.variants.add(nameDef);
                }
            }
        }

        // Sort-Sort and then Dump-dump
        Collections.sort(masterNames, (nd1, nd2) -> nd1.text.compareToIgnoreCase(nd2.text));
        masterNames.addAll(badMasterNames);

        int     maxVar  = 0;
        NameDef maxNDef = null;
        for (NameDef nameDef : masterNames) {
            results.add("");
            results.add("");
            results.add(format(nameDef));
            nameDef.variants.forEach(nd -> results.add(format(nd)));

            if (nameDef.variants.size() > maxVar) {
                maxVar = nameDef.variants.size();
                maxNDef = nameDef;
            }
        }
        System.out.println("Max=" + maxVar + " for " + maxNDef.text + " [" + maxNDef.id + "]");
        
        Files.write(Paths.get("C:/temp/oxford-fn.csv"), results, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static String format(NameDef nameDef) {
        StringBuilder buff = new StringBuilder(1024);

        buff.append(nameDef.id);
        buff.append("|").append(nameDef.text);
        buff.append("|").append(nameDef.language);
        buff.append("|").append(nameDef.type);
        buff.append("|").append(nameDef.isMale);
        buff.append("|").append(nameDef.isFemale);
        buff.append("|").append(nameDef.definition);

        return buff.toString();
    }

    static NameDef parseRow(String row) {
        NameDef nameDef = new NameDef();

        nameDef.id = getAttrValue(row, "e", "id");
        nameDef.text = getTagValue(row, "headword");
        nameDef.language = getTagValue(row, "span");
        nameDef.refId = getAttrValue(row, "xref", "ref");
        nameDef.type = getAttrValue(row, "xref", "type");
        nameDef.definition = cleanup(getTagValue(row, "div1"));
        nameDef.isMale = row.indexOf(MALE_CHAR) > 0;
        nameDef.isFemale = row.indexOf(FEMALE_CHAR) > 0;

        return (nameDef.id == null) ? null : nameDef;
    }

    static String getAttrValue(String row, String tag, String key) {
        int ndx0 = row.indexOf("<" + tag);
        int ndx1 = row.indexOf(" " + key + "=", ndx0 + 1);

        if (ndx0 >=0  &&  ndx1 > 0) {
            int ndx2 = row.indexOf('"', ndx1 + key.length() + 3);
            return row.substring(ndx1 + key.length() + 3, ndx2);
        } else {
            return null;
        }
    }

    static String getTagValue(String row, String tag) {
        if (row == null) {
            return null;
        }

        int ndx0 = row.indexOf("<" + tag + ">");
        if (ndx0 < 0) {
            ndx0 = row.indexOf("<" + tag + " ");
        }
        int ndx1 = row.indexOf(">", ndx0 + 1);
        if (ndx0 >= 0  &&  ndx1 > 0) {
            int ndx2 = row.indexOf("</" + tag, ndx1 + 1);
            if (ndx2 < 0) {
                ndx2 = row.indexOf("</", ndx1 + 1);
            }
            return row.substring(ndx1 + 1, ndx2);
        } else {
            return null;
        }
    }

    static String cleanup(String text) {
        if (text == null) {
            return null;
        }

        String tText = removeComments(text);
        tText = removeXrefGrp(tText);
        tText = removeNameGrp(tText);
        tText = removeDate(tText);

        return tText;
    }

    static String removeComments(String text) {
        int ndx0 = text.indexOf("<!--");
        int ndx1 = text.indexOf("-->", ndx0);
        if (ndx0 >= 0  &&  ndx1 > ndx0) {
            String chunk0 = (ndx0 == 0) ? "" : text.substring(0, ndx0);
            String chunk1 = (ndx1+3 > text.length()) ? "" : text.substring(ndx1+3);
            return (removeComments(chunk0 + chunk1));
        } else {
            return text;
        }
    }

    static String removeXrefGrp(String text) {
        String xrefGrpContent = getTagValue(text, "xrefGrp");
        String xrefContent = getTagValue(xrefGrpContent, "xref");

        int ndx0 = text.indexOf("<xrefGrp>");
        int ndx1 = text.indexOf("</xrefGrp>", ndx0);
        if (ndx0 >= 0  &&  ndx1 > ndx0) {
            String chunk0 = (ndx0 == 0) ? "" : text.substring(0, ndx0);
            String chunk1 = (ndx1+10 > text.length()) ? "" : text.substring(ndx1+10);
            return (removeXrefGrp(chunk0 + xrefContent + chunk1));
        } else {
            return text;
        }
    }

    static String removeNameGrp(String text) {
        String nameGrpContent = getTagValue(text, "nameGrp");

        int ndx0 = text.indexOf("<nameGrp");
        int ndx1 = text.indexOf("</nameGrp>", ndx0);
        if (ndx0 >= 0  &&  ndx1 > ndx0) {
            String chunk0 = (ndx0 == 0) ? "" : text.substring(0, ndx0);
            String chunk1 = (ndx1+10 > text.length()) ? "" : text.substring(ndx1+10);
            return (removeNameGrp(chunk0 + nameGrpContent + chunk1));
        } else {
            return text;
        }
    }

    static String removeDate(String text) {
        String nameGrpContent = getTagValue(text, "date");

        int ndx0 = text.indexOf("<date");
        int ndx1 = text.indexOf("</date>", ndx0);
        if (ndx0 >= 0  &&  ndx1 > ndx0) {
            String chunk0 = (ndx0 == 0) ? "" : text.substring(0, ndx0);
            String chunk1 = (ndx1+7 > text.length()) ? "" : text.substring(ndx1+7);
            return (removeDate(chunk0 + nameGrpContent + chunk1));
        } else {
            return text;
        }
    }
}
