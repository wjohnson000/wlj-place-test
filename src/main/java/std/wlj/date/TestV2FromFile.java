/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.model.DateResult;
import org.familysearch.standards.date.model.GenDateInterpResult;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class TestV2FromFile {
    public static void main(String... args) throws Exception {
        List<String> textes = textesFromFile("C:/temp/date-interp.txt");

        for (String text : textes) {
            System.out.println("\n" + text);
            try {
                String[] chunks = PlaceHelper.split(text, '|');
                DateResult dateResult = DateUtil.interpDate(chunks[0], StdLocale.ENGLISH, null, null, null);
                for (GenDateInterpResult date : dateResult.getDates()) {
                    System.out.println("  gx02: " + chunks[0] + "|" + date.getDate().toGEDCOMX());
                }
            } catch (Exception e) {
                System.out.println("  V2.ext: " + e.getMessage());
            }
        }
    }

    static List<String> textesFromFile(String filename) throws IOException {
        return Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
    }
}