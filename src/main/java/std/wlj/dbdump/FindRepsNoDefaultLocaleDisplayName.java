/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class FindRepsNoDefaultLocaleDisplayName {

    private static final String dataDir  = "C:/temp/db-dump";
    private static final String repFile  = "place-rep-all.txt";
    private static final String nameFile = "display-name-all.txt";
    private static final String outFile  = "rep-disp-name-mismatch.txt";

    private static List<String> results = new ArrayList<>(250_000);
    
    public static void main(String...args) throws Exception {
        Map<String, String> types = DumpTypes.loadAllTypes();

        // Extract default-locale, type for every non-deleted place-rep
        Map<String, String[]> repData = new HashMap<>();

        try(FileInputStream fis = new FileInputStream(new File(dataDir, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                String   rowData   = scan.nextLine();
                String[] rowFields = PlaceHelper.split(rowData, '|');
                if (rowFields.length > 9) {
                    String sRepId = rowFields[0];
                    String typeId = rowFields[6];
                    String sDelId = rowFields[9];
                    String locale = rowFields[10];
                    String localeX = localeShort(locale);

                    if (sDelId.isEmpty()) {
                        repData.put(sRepId, new String[] { localeX, locale, types.getOrDefault(typeId, typeId), "", "" });
                    }
                }
            }
        }
        System.out.println("Rep-Count: " + repData.size());

        try(FileInputStream fis = new FileInputStream(new File(dataDir, nameFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                String   rowData = scan.nextLine();
                String[] rowFields = PlaceHelper.split(rowData, '|');
                if (rowFields.length > 5) {
                    String sRepId = rowFields[0];
                    String locale = rowFields[2];
                    String text   = rowFields[3];
                    String isDel  = rowFields[5];
                    String localeX = localeShort(locale);
                    String[] repDatum = repData.get(sRepId);

                    if ("t".equals(isDel)) {
                        if (repDatum != null  &&  repDatum[0].equals(localeX)) {
                            System.out.println(sRepId + "|" + locale + "|" + text);
                        }
                        continue;
                    }

                    if (repDatum != null) {
                        if (repDatum[0].equals(localeX)) {
                            repData.remove(sRepId);
                        } else if (repDatum[3].isEmpty()) {
                            repDatum[3] = locale;
                            repDatum[4] = text;
                        } else if (localeX.equals("en")) {
                            repDatum[3] = locale;
                            repDatum[4] = text;   
                        }
                    }
                }
            }
        }
        System.out.println("Rep-Count: " + repData.size());

        for (Map.Entry<String, String[]> entry : repData.entrySet()) {
            StringBuilder buff = new StringBuilder();
            buff.append(entry.getKey());
            for (String vvv : entry.getValue()) {
                buff.append("|").append(vvv);
            }
            results.add(buff.toString());
        }
        
        Files.write(Paths.get(dataDir, outFile), results, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.exit(0);
    }

    static String localeShort(String locale) {
        int ndx = (locale == null) ? -1 : locale.indexOf('-');
        return (ndx <= 0) ? locale : locale.substring(0, ndx);
    }
}
