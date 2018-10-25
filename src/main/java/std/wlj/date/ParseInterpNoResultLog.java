/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author wjohnson000
 *
 */
public class ParseInterpNoResultLog {

    static final String QQ   = "\"\"";
    static final String path = "C:/temp/date-interp-no-results.csv";
    static final List<String> details = new ArrayList<>();
    static final Map<String, Integer> noResCount = new TreeMap<>();

    public static void main(String...args) throws IOException {
        List<String> results = Files.readAllLines(Paths.get(path), Charset.forName("UTF-8"));
        results.forEach(ParseInterpNoResultLog::getInterestingStuff);

        Files.write(Paths.get("C:/temp/date-interp-no-results.txt"), details, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        noResCount.entrySet().forEach(entry -> System.out.println(entry.getKey() + "|" + entry.getValue()));

        System.out.println("\n\n\n");
        noResCount.entrySet().stream().filter(entry -> entry.getValue() > 25).forEach(entry -> System.out.println(entry.getKey() + "|" + entry.getValue()));
    }

    static void getInterestingStuff(String line) {
        String text = getValue(line, "text");
        String hint = getValue(line, "langHint");
        String lang = getValue(line, "accept-language");
        String time = getValue(line, "time");

        details.add(text + "|" + hint + "|" + lang + "|" + time);

        String  textL = text.toLowerCase();
        Integer count = noResCount.getOrDefault(textL, Integer.valueOf(0));
        noResCount.put(textL, count+1);
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
