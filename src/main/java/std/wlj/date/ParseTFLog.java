/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wjohnson000
 *
 */
public class ParseTFLog {

    static final String QQ   = "\"\"";
    static final String path = "C:/temp/date-tf-lots.csv";
    static final Map<String, Integer> monCount = new HashMap<>();

    public static void main(String...args) throws IOException {
        List<String> results = Files.readAllLines(Paths.get(path), Charset.forName("UTF-8"));
        results.forEach(ParseTFLog::getInterestingStuff);
        monCount.entrySet().forEach(ee -> System.out.println(ee.getKey() + "|" + ee.getValue()));
    }

    static void getInterestingStuff(String line) {
        String text = getValue(line, "text");
        int ndx0 = text.indexOf('+');
        int ndx1 = text.indexOf('+', ndx0+1);
        int ndx2 = text.indexOf('+', ndx1+1);
        if (ndx0 > 0  &&  ndx1 > ndx0  &&  ndx2 == -1) {
            String month = text.substring(ndx0+1, ndx1);
            Integer count = monCount.getOrDefault(month, Integer.valueOf(0));
            monCount.put(month, count+1);
        }
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
}
