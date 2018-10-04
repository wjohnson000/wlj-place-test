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

/**
 * @author wjohnson000
 *
 */
public class ParseV1AssistedLog {

    static final String QQ   = "\"\"";
    static final String path = "C:/temp/date-gedcomx.csv";
    static final List<String> details = new ArrayList<>();

    public static void main(String...args) throws IOException {
        List<String> results = Files.readAllLines(Paths.get(path), Charset.forName("UTF-8"));
        results.forEach(ParseV1AssistedLog::getInterestingStuff);

        Files.write(Paths.get("C:/temp/date-gedcomx.txt"), details, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static void getInterestingStuff(String line) {
        String text = getValue(line, "text");
        String hint = getValue(line, "langHint");
        String lang = getValue(line, "accept-language");
        String gedx = getValue(line, "gedcomx");
        String asst = getValue(line, "assisted");
        if (! gedx.isEmpty()) {
            System.out.println(text + "|" + hint + "|" + lang + "|" + gedx + "|" + asst);
            details.add(text + "|" + hint + "|" + lang + "|" + gedx + "|" + asst);
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
