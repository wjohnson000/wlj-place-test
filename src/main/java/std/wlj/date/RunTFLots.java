/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.date.model.GenDateInterpResult;

/**
 * @author wjohnson000
 *
 */
public class RunTFLots {

    static final String QQ   = "\"\"";
    static final String path = "C:/temp/date-tf-lots.csv";

    public static void main(String...args) throws IOException {
        List<String> results = Files.readAllLines(Paths.get(path), Charset.forName("UTF-8"));

        long time0 = System.nanoTime();
        results.forEach(line -> {
            String text = getValue(line, "text");
            List<GenDateInterpResult> dates = interpDate(text);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println("  " + text);
            System.out.println("  " + (dates.isEmpty() ? "" : dates.get(0).getDate().toGEDCOMX()));
        });
        long time1 = System.nanoTime();
        System.out.println("COUNT=" + results.size() + ";   TIME=" + (time1 - time0) / 1_000_000.0);
    }

    static String getValue(String line, String key) {
        int ndx01 = line.indexOf(" " + key);
        int ndx02 = line.indexOf(QQ, ndx01);
        int ndx03 = line.indexOf(QQ, ndx02+2);
        if (ndx01 > 0  &&  ndx02 > ndx01  &&  ndx03 > ndx02) {
            return line.substring(ndx02, ndx03).replace('"', ' ').trim();
        } else {
            return "";
        }
    }

    static List<GenDateInterpResult> interpDate(String text) {
        try {
            return DateUtil.interpDate(text, StdLocale.UNDETERMINED).getDates();
        } catch (GenDateException e) {
            return Collections.emptyList();
        }
    }
}
