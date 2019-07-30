/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * Find the most common DISPLAY names.
 * 
 * @author wjohnson000
 *
 */
public class FindTopLevelNames {

    private static final String dataDir  = "C:/temp/db-dump";
    private static final String repFile  = "place-rep-all.txt";
    private static final String dispFile = "display-name-all.txt";

    public static void main(String...args) throws Exception {
        Map<String, String> placeTypes = DumpTypes.loadPlaceTypes();
        Map<String, String[]> repData  = new LinkedHashMap<>();
        Map<String, String> repNameEN  = new HashMap<>();
        Map<String, String> repNameXX  = new HashMap<>();
        Map<String, String> repNameZZ  = new HashMap<>();

        try(FileInputStream fis = new FileInputStream(new File(dataDir, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                String   rowData   = scan.nextLine();
                String[] rowFields = PlaceHelper.split(rowData, '|');
                if (rowFields.length > 5) {
                    String sRepId = rowFields[0];
                    String sParId = rowFields[2];
                    String sDelId = rowFields[9];

                    if (sDelId.isEmpty()  &&  (sParId.isEmpty()  ||  sParId.equals("0")  ||  sParId.equals("-1"))) {
                        repData.put(sRepId, rowFields);
                    }
                }
            }
        }
        try(FileInputStream fis = new FileInputStream(new File(dataDir, dispFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                String   rowData   = scan.nextLine();
                String[] rowFields = PlaceHelper.split(rowData, '|');
                if (rowFields.length > 5) {
                    String sRepId = rowFields[0];
                    String locale = rowFields[2];
                    String text   = rowFields[3];

                    rowFields = repData.get(sRepId);
                    if (rowFields != null) {
                        if (locale.startsWith("en")) {
                            repNameEN.put(sRepId, text);
                        }
                        if (locale.equalsIgnoreCase(rowFields[10])) {
                            repNameXX.put(sRepId, text);
                        }
                    }
                }
            }
        }

        for (String[] repDatum : repData.values()) {
            String sRepId = repDatum[0];
            String typeId = repDatum[6];
            String isPub  = repDatum[11].toUpperCase();
            String isVal  = repDatum[12].toUpperCase();

            String typeInfo = placeTypes.getOrDefault(typeId, "unknown");
            String name01   = repNameXX.getOrDefault(sRepId, repNameEN.getOrDefault(sRepId, repNameZZ.getOrDefault(sRepId, "Unknown")));
            String name02   = repNameEN.getOrDefault(sRepId, "");
            if (name01.equals(name02)) {
                name02 = "";
            }
            String repInfo  = sRepId + "|" + name01 + "|" + name02 + "|" + typeId + "|" + typeInfo + "|" + isPub + "|" + isVal;
            System.out.println(repInfo);
        }
    }
}
