/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;
import java.util.Map.Entry;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * Find the most common DISPLAY names.
 * 
 * @author wjohnson000
 *
 */
public class FindCommonVariantNames {

    private static final String dataDir   = "C:/temp/db-dump";
    private static final String vNamFile  = "variant-name-all.txt";

    public static void main(String...args) throws Exception {
        Map<String, Integer> nameCount = new HashMap<>();

        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(dataDir, vNamFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("VAR-NAME.main: " + lineCnt);

                String   rowData   = scan.nextLine();
                String[] rowFields = PlaceHelper.split(rowData, '|');
                if (rowFields.length > 7) {
                    String delId = rowFields[1];
                    String text  = rowFields[3];
                    String dflg  = rowFields[7];
                    if (delId.isEmpty()  &&  ! "t".equalsIgnoreCase(dflg)) {
                        String normTxt = PlaceHelper.normalize(text).toLowerCase();
                        Integer count = nameCount.getOrDefault(normTxt, Integer.valueOf(0));
                        nameCount.put(normTxt, count+1);
                    }
                }
            }
        }

        nameCount.entrySet().stream()
            .filter(entry -> entry.getValue() >= 200)
            .sorted(new Comparator<Entry<String, Integer>>() {
                @Override public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
                    return e2.getValue() - e1.getValue();
                }
            })
            .forEach(System.out::println);
    }
}
