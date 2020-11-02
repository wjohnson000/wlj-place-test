/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.familysearch.homelands.admin.parser.helper.ExcelUtility;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * Compare the "Praenomin" name data from the original spreadsheet (roc-names-from-missionaries.xlxs) and the
 * updated list (Names from Praenomin reviewed.xlsx).
 * @author wjohnson000
 *
 */
public class PraenominComparison {

    static final String BASE_DIR = "C:/D-drive/homelands/names/final";
    static final String FILE_OLD = "roc-names-from-missionaries.xlsx";
    static final String FILE_NEW = "Names from Praenomin reviewed.xlsx";

    public static void main(String...args) throws Exception {
        byte[] rawDataOld = Files.readAllBytes(Paths.get(BASE_DIR, FILE_OLD));
        Map<Integer, List<List<String>>> nameDataOld = ExcelUtility.loadExcelData(rawDataOld, ".xlsx");
        List<NameDef> modelsOld = modelsFromRows(nameDataOld.get(0));
        Map<String, NameDef> modelMapOld = modelsOld.stream()
                                                    .collect(Collectors.toMap(kk -> kk.text, vv -> vv, (v1, v2) -> v1));
        Map<String, String> varsOld = new TreeMap<>();
        modelsOld.stream()
                 .forEach(mm -> mm.variants.forEach(vv -> varsOld.put(vv.text, mm.text)));

        byte[] rawDataNew = Files.readAllBytes(Paths.get(BASE_DIR, FILE_NEW));
        Map<Integer, List<List<String>>> nameDataNew = ExcelUtility.loadExcelData(rawDataNew, ".xlsx");
        List<NameDef> modelsNew = modelsFromRows(nameDataNew.get(0));
        Map<String, NameDef> modelMapNew = modelsNew.stream()
                                                    .collect(Collectors.toMap(kk -> kk.text, vv -> vv, (v1, v2) -> v1));
        Map<String, String> varsNew = new TreeMap<>();
        modelsNew.stream()
                 .forEach(mm -> mm.variants.forEach(vv -> varsNew.put(vv.text, mm.text)));

        Set<String> names = new TreeSet<>();
        names.addAll(modelMapOld.keySet());
        names.addAll(modelMapNew.keySet());
        for (String name : names) {
            NameDef nameOld = modelMapOld.get(name);
            NameDef nameNew = modelMapNew.get(name);
            if (nameOld == null) {
                if (varsOld.containsKey(nameNew.text)) {
                    System.out.println(nameNew.text + "|" + formatVariants(nameNew) + "|||Promoted from Variant of " + varsOld.get(nameNew.normalText));
                } else {
                    System.out.println(nameNew.text + "|" + formatVariants(nameNew) + "|||ADDED");
                }
            } else if (nameNew == null) {
                if (varsNew.containsKey(nameOld.text)) {
                    System.out.println("||" + nameOld.text + "|" + formatVariants(nameOld) + "|Variant of " + varsNew.get(nameOld.text) + "|" + nameOld.definition);
                } else {
                    System.out.println("||" + nameOld.text + "|" + formatVariants(nameOld) + "|DELETED");
                }
            } else {
//                System.out.println(nameNew.text + "|" + formatVariants(nameNew) + "|" + nameOld.text + "|" + formatVariants(nameOld));
            }
        }
    }

    static String formatVariants(NameDef nameDef) {
        return nameDef.variants.stream()
                      .map(vv -> vv.text)
                      .collect(Collectors.joining(", "));
    }

    static Map<String, NameDef> parseNameDefs(List<List<String>> nameData) {
        Map<String, NameDef> nameDefs = new TreeMap<>();

        for (List<String> nData : nameData) {
            NameDef nameDef = new NameDef();
            nameDef.text = nData.get(2);
            nameDefs.put(nameDef.text, nameDef);
        }

        return nameDefs;
    }

    static List<NameDef> modelsFromRows(List<List<String>> rows) {
        return rows.stream()
                   .skip(1)
                   .map(row -> modelFromRow(row))
                   .filter(model -> model != null)
                   .collect(Collectors.toList());
    }

    static boolean dataIsValid(List<String> row) {
        boolean isOK = true;

        if (row.size() < 6) {
            isOK = false;
        } else {
            String[] names = row.get(2).split(",");
            if (names[0].trim().length() < 2) {
                isOK = false;
            }
            else if (row.get(5).trim().length() < 5) {
                isOK = false;
            }
        }

        return isOK;
    }

    /**
     * Create a single {@link NameDef} from a row of data.  It must have at least six values, and both the name and
     * description fields must be non-blank.
     * 
     * @param row column data from a single row of the spreadsheet
     * @return newly-created NameDef
     */
    static NameDef modelFromRow(List<String> row) {
        if (! dataIsValid(row)) {
            return null;
        }

        NameDef model = new NameDef();

        String[] names = row.get(2).split(",");
        model.text = names[0].trim();
        model.normalText = PlaceHelper.normalize(model.text).toLowerCase();
        model.type = getType(row.get(3));

        if (row.size() == 6) {
            model.language = "en";
        } else {
            model.language = getLanguage(row.get(6));
        }

        String nameLang = row.get(4);
        if (nameLang.trim().isEmpty()) {
            model.definition = row.get(5);
        } else {
            model.definition = nameLang + ": " + row.get(5);
        }

        if (model.definition.toLowerCase().contains("female")) {
            model.isFemale = true;
        } else if (model.definition.toLowerCase().contains("male")) {
            model.isMale = true;
        }

        Arrays.stream(names)
              .skip(1)
              .map(nn -> nn.trim())
              .forEach(name -> {
                  NameDef variant = new NameDef();
                  variant.text = name;
                  variant.normalText = PlaceHelper.normalize(name).toLowerCase();
                  variant.type = "VARIANT";
                  model.variants.add(variant);
              });

        return model;
    }

    /**
     * Determine the name type, must be one of "FIRST", "LAST" or "BOTH"
     * 
     * @param type value from the "Is it a surname, given name or both" column
     * @return appropriate type, defaulting to "BOTH" if not determinable
     */
     static String getType(String type) {
         String nameType = "BOTH";

         String typeVal = type.toLowerCase();
         if (typeVal.contains("first") || typeVal.contains("given")) {
             nameType = "FIRST";
         } else if (typeVal.contains("last") || typeVal.contains("surname")) {
             nameType = "LAST";
         } else if (typeVal.contains("both")) {
             nameType = "BOTH";
         }

         return nameType;
     }

     /**
      * Determine the language of the data, defaulting to "en" (English) if not set
      * 
      * @param lang language, or null
      * @return
      */
    static String getLanguage(String lang) {
        return (lang == null || lang.trim().isEmpty()) ? "en" : lang.toLowerCase();
    }
}
