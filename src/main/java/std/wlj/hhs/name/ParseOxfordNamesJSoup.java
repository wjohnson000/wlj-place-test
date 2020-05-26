/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Look at the name files from "Oxford", list names, variants, etc ...
 * 
 * @author wjohnson000
 *
 */
public class ParseOxfordNamesJSoup {

    private static final String MALE_CHAR = "♂";
    private static final String FEMALE_CHAR = "♀";

    private static String BASE_DIR = "C:/D-drive/homelands/names";
    private static String FIRST_FILE = "first_acref_9780198610601.xml";
    private static String LAST_FILE  = "last_acref_9780195081374.xml";

    private static Map<String, NameDef> nameById = new HashMap<>();
    private static Map<String, List<NameDef>> nameByName = new HashMap<>();

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
                if (nameById.containsKey(nameDef.id)) {
                    System.out.println("Duplicate [id].key: " + nameDef.id);
                } else {
                    nameById.put(nameDef.id, nameDef);
                }

                List<NameDef> tNames = nameByName.computeIfAbsent(nameDef.text, kk -> new ArrayList<>());
                tNames.add(nameDef);
            }
        }

        // Tie variants to their "master" name, and save the "Master" ones
        List<NameDef> masterNames = new ArrayList<>();
        List<NameDef> badMasterNames = new ArrayList<>();

        // Sort-Sort and then Dump-dump
        masterNames = nameById.values().stream()
                              .filter(nd -> nd.refId != null  &&  nd.text != null)
                              .collect(Collectors.toList());
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
//        System.out.println("Max=" + maxVar + " for " + maxNDef.text + " [" + maxNDef.id + "]");
        
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
        nameDef.language = "en";
        nameDef.refId = getAttrValue(row, "xref", "ref");
        String primaryDefinition = cleanup(getTagValue(row, "div1"));
        String secondaryDefinition = cleanup(getTagValueMulti(row, "note"));
        nameDef.definition = primaryDefinition + (secondaryDefinition == null ? "" : (" " + secondaryDefinition));
        if (primaryDefinition != null) {
            if (primaryDefinition.toLowerCase().contains(" pet ")  ||  primaryDefinition.toLowerCase().contains(">pet ")) {
                nameDef.type = "PET";
            } else if (primaryDefinition.toLowerCase().contains(" short ")  ||  primaryDefinition.toLowerCase().contains(">short ")) {
                nameDef.type = "SHORT";
            } else {
                nameDef.type = "REGULAR";
            }
        }
        nameDef.isMale = row.indexOf(MALE_CHAR) > 0;
        nameDef.isFemale = row.indexOf(FEMALE_CHAR) > 0;
        nameDef.variants = getVariants(row);
if ("acref-9780198610601-e-1576".equals(nameDef.id)) {
    System.out.println("NN.JAMES.NN: " + secondaryDefinition);
}
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

    static String getTagValueMulti(String row, String tag) {
        if (row == null) {
            return null;
        }

        int ndx0 = row.indexOf("<" + tag + ">");
        if (ndx0 < 0) {
            ndx0 = row.indexOf("<" + tag + " ");
        }
        int ndx1 = row.indexOf(">", ndx0 + 1);

        if (ndx0 >= 0  &&  ndx1 > 0) {
            int ndx2 = row.lastIndexOf("</" + tag);
            if (ndx2 < 0) {
                ndx2 = row.lastIndexOf("</");
            }
            return row.substring(ndx1 + 1, ndx2);
        } else {
            return null;
        }
    }

    static List<NameDef> getVariants(String row) {
        List<NameDef> variants = new ArrayList<>();
        String note = getTagValue(row, "note");
        if (note != null) {
            String tNote = note;
            List<String> scList = new ArrayList<>();
            while (! tNote.isEmpty()) {
                int ndx0 = tNote.indexOf("<sc>");
                int ndx1 = tNote.indexOf("<sc>", ndx0+1);
                if (ndx1 == -1) {
                    scList.add(tNote);
                    tNote = "";
                } else {
                    scList.add(tNote.substring(0, ndx1));
                    tNote = tNote.substring(ndx1);
                }
            }
            System.out.println("NN: " + note);
            scList.forEach(sc -> System.out.println("    " + sc));
        }
        return variants;
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
