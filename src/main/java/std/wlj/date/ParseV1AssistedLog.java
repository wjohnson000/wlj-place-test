/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author wjohnson000
 *
 */
public class ParseV1AssistedLog {

    static final String path = "C:/temp/date-v1-assisted-xx.csv";

    public static void main(String...args) throws IOException {
        List<String> results = Files.readAllLines(Paths.get(path), Charset.forName("UTF-8"));
        System.out.println(results.size());
        results.forEach(ParseV1AssistedLog::getInterestingStuff);
    }

    static  void getInterestingStuff(String line) {
        int ndx01 = line.indexOf('"');
        int ndx02 = line.indexOf('"', ndx01+1);
        int ndx03 = line.indexOf(',', ndx02+1);
        int ndx04 = line.indexOf(',', ndx03+1);
        int ndx05 = line.indexOf(',', ndx04+1);
        int ndx06 = line.indexOf(',', ndx05+1);
        int ndx07 = line.indexOf('"', ndx06+1);
        int ndx08 = line.indexOf('"', ndx07+1);

        if (ndx03 >= 0  &&  ndx04 >= 0  &&  ndx05 >= 0  &  ndx06 >= 0  &&  ndx07 >= 0  &&  ndx08 >= 0) {
            String text = line.substring(0, ndx03).replace('"', ' ').trim();
            String lang = line.substring(ndx03+1, ndx04).replace('"', ' ').trim();
            String stts = line.substring(ndx04+1, ndx05).replace('"', ' ').trim();
            String coun = line.substring(ndx05+1, ndx06).replace('"', ' ').trim();
            String gedx = line.substring(ndx07, ndx08).replace('"', ' ').trim();
            
            System.out.println(text + "|" + lang + "|" + stts + "|" + coun + "|" + gedx);
        }
    }
}
