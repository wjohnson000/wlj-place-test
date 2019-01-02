/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.model.GenSimpleDate;
import org.familysearch.standards.date.shortcut.MDYGenSimpleDate;

/**
 * @author wjohnson000
 *
 */
public class TestNewMDY {

    public static void main(String...args) throws Exception {
        List<String> dates = Files.readAllLines(Paths.get("C:/temp/date-interp-mdy.txt"), StandardCharsets.UTF_8);
        for (String date : dates) {
            date = date.replace('"', ' ');
            int ndx = date.lastIndexOf(',');
            if (ndx > 0) {
                String text = date.substring(0, ndx).trim();
                String gedcomx = date.substring(ndx+1).trim();
                List<GenSimpleDate> genDates = MDYGenSimpleDate.fromMDY(text, StdLocale.ENGLISH, null, null, null);
                if (genDates == null  ||  genDates.isEmpty()) {
                    System.out.println("NOO: " + text);
                } else if (! genDates.get(0).toGEDCOMX().equals(gedcomx)) {
                    System.out.println("ERR: " + text + " .. " + genDates.get(0).toGEDCOMX() + " .. " + gedcomx);
                }
            }
        }
    }
}
