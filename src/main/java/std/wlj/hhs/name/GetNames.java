/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.familysearch.homelands.importer.names.NameDef;
import org.familysearch.homelands.importer.names.other.ROCNameExtractor;
import org.familysearch.homelands.importer.names.oxford.NameDefParser;
import org.familysearch.homelands.importer.names.oxford.NameDefParserJSoup;
import org.familysearch.homelands.importer.names.oxford.NameExtractor;

/**
 * @author wjohnson000
 *
 */
public class GetNames {


    private static final String FIRST_NAME = "FIRST";
    private static final String LAST_NAME  = "LAST";
    private static final String BOTH_NAME  = "BOTH";

    private static String BASE_DIR   = "C:/D-drive/homelands/names";
    private static String FIRST_FILE = "first_acref_9780198610601.xml";
    private static String LAST_FILE  = "last_acref_9780195081374.xml";
    private static String ROC_FILE   = "Names-and-Definitions-from-missionaries.xlsx";

    public static void main(String... args) throws Exception {
        NameDefParser parser = new NameDefParserJSoup();

        Map<String, List<NameDef>> firstNamesOXF = NameExtractor.namesFromFile(BASE_DIR + "/" + FIRST_FILE, parser);
        Map<String, List<NameDef>> lastNamesOXF  = NameExtractor.namesFromFile(BASE_DIR + "/" + LAST_FILE, parser);

        Map<String, List<NameDef>> firstNamesROC = new TreeMap<>();
        Map<String, List<NameDef>> lastNamesROC = new TreeMap<>();

        Map<Integer, String> sheets = ROCNameExtractor.getSheetNames(BASE_DIR + "/" + ROC_FILE);
        for (Integer sheet : sheets.keySet()) {
            System.out.println("\nProcessing sheet " + sheet + " --> " + sheets.get(sheet));

            Map<String, NameDef> nameDefs = ROCNameExtractor.namesFromFile(BASE_DIR + "/" + ROC_FILE, sheet);
            String type = getPredominantType(nameDefs);
            System.out.println("  TYPE: " + type);
            for (Map.Entry<String, NameDef> entry : nameDefs.entrySet()) {
                if (isFirstName(entry.getValue(), type)) {
                    List<NameDef> nameDefList = firstNamesROC.computeIfAbsent(entry.getKey(), kk -> new ArrayList<>());
                    nameDefList.add(entry.getValue());
                }

                if (isLastName(entry.getValue(), type)) {
                    List<NameDef> nameDefList = lastNamesROC.computeIfAbsent(entry.getKey(), kk -> new ArrayList<>());
                    nameDefList.add(entry.getValue());
                }
            }
        }

        Set<String> names = new TreeSet<>();
        names.addAll(getNames(firstNamesOXF));
        names.addAll(getNames(lastNamesOXF));
        names.addAll(getNames(firstNamesROC));
        names.addAll(getNames(lastNamesROC));

        names.forEach(System.out::println);
        System.exit(0);
    }

    static Set<String> getNames(Map<String, List<NameDef>> nameDefs) {
        Set<String> names = new HashSet<>();
        nameDefs.values().forEach(nds -> {
            nds.stream().forEach(nd -> {
                names.add(nd.getText());
                nd.getVariants().forEach(var -> names.add(var.getText()));
                nd.getReferences().forEach(var -> names.add(var.getText()));
            });
        });
        return names;
    }

    static String getPredominantType(Map<String, NameDef> nameDefs) {
        int firstCount = 0;
        int lastCount  = 0;
        int bothCount  = 0;

        for (NameDef nameDef : nameDefs.values()) {
            if (nameDef.getType().equals(FIRST_NAME)) {
                firstCount++;
            } else if (nameDef.getType().equals(LAST_NAME)) {
                lastCount++;
            } else if (nameDef.getType().equals(BOTH_NAME)) {
                bothCount++;
            }
        }

//        System.out.println("  TOTAL-count: " + nameDefs.size());
//        System.out.println("  FIRST-count: " + firstCount);
//        System.out.println("   LAST-count: " + lastCount);
//        System.out.println("   BOTH-count: " + bothCount);

        return (firstCount > lastCount  ||  (firstCount == 0 && lastCount == 0)) ? FIRST_NAME : LAST_NAME;
    }

    static boolean isFirstName(NameDef nameDef, String defaultType) {
        if (nameDef.getType().equals(FIRST_NAME)) {
            return true;
        } else if (nameDef.getType().equals(LAST_NAME)) {
            return false;
        } else if (nameDef.getType().equals(BOTH_NAME)) {
            return true;
        } else {
            return FIRST_NAME.equals(defaultType);
        }
    }

    static boolean isLastName(NameDef nameDef, String defaultType) {
        if (nameDef.getType().equals(FIRST_NAME)) {
            return false;
        } else if (nameDef.getType().equals(LAST_NAME)) {
            return true;
        } else if (nameDef.getType().equals(BOTH_NAME)) {
            return true;
        } else {
            return LAST_NAME.equals(defaultType);
        }
    }
}
