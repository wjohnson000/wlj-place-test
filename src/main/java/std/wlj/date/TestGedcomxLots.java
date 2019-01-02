/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.model.GenDate;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class TestGedcomxLots {

    public static void main(String... args) throws Exception {
        runTests();
    }

    static void runTests() throws Exception {
        List<String> textes = Files.readAllLines(Paths.get("C:/temp/date-gedcomx.txt"), StandardCharsets.UTF_8);
        long time0 = System.nanoTime();

        for (String text : textes) {
            String[] chunks = PlaceHelper.split(text, '|');
            if (chunks.length > 3) {
                GenDate date = DateUtil.fromGedcomX(chunks[3]);
                if (date == null  ||  ! date.toGEDCOMX().equals(chunks[3])) {
                    System.out.println("Text:" + chunks[3] + "  --> Date: " + date);
                }
            }
        }

        long time1 = System.nanoTime();
        System.out.println("  " + (time1 - time0) / 1_000_000.0);
    }
}