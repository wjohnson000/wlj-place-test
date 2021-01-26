/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Take results of the following Splunk query:
 *    - index=production host=std-ws-place-prod* "*wildcards=false.zh" earliest=@d-2d
 * 
 * And decode the "text=...&wildcards=..." value into UTF-8 text
 * 
 * @author wjohnson000
 *
 */
public class DecodeCJKNames {

    public static void main(String... args) throws Exception {
        Set<String>  names   = new TreeSet<>();
        List<String> targets = Files.readAllLines(Paths.get("C:/temp/cjk-samples.txt"), StandardCharsets.UTF_8);

        for (String line : targets) {
            int ndx0 = line.indexOf("text=%");
            if (ndx0 > 0) {
                int ndx1 = line.indexOf("&wild", ndx0);
                if (ndx1 > ndx0) {
                    String text = line.substring(ndx0+5, ndx1);
                    names.add(URLDecoder.decode(text, "UTF-8"));
                }
            }
        }

        names.forEach(System.out::println);

        System.exit(0);
    }
}
