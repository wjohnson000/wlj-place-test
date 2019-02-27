/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class ZzzCountFields {

    public static void main(String...args) throws Exception {
//        List<String> attrData = Files.readAllLines(Paths.get("C:/temp/db-dump/attribute-all.txt"), StandardCharsets.UTF_8);
        List<String> attrData = Files.readAllLines(Paths.get("C:/temp/flat-file/load-test/range-1/rep-attr.txt"), StandardCharsets.UTF_8);
        System.out.println("Lines: " + attrData.size());

        for (int ln=0;  ln<attrData.size();  ln++) {
            String attr = attrData.get(ln);
            String[] chunks = PlaceHelper.split(attr, '|');
            if (chunks.length > 0) {
                if (chunks.length < 14) {
                    System.out.println("LN.LT." + (ln+1) + ": " + attr);
                } else if (chunks.length > 14) {
                    System.out.println("LN.GT." + (ln+1) + ": " + attr);
                }
            }
        }
    }
}
