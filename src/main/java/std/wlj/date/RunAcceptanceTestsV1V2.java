/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.date.model.DateResult;

/**
 * @author wjohnson000
 *
 */
public class RunAcceptanceTestsV1V2 {

     public static void main(String... args) throws Exception {
        runTests();
    }

    static void runTests() throws Exception {
        StringBuilder buff = new StringBuilder(100_000);
        DateResult dateResult;

        List<String> textes = loadTestDates("Interp_Entries");
//        List<String> textes = loadTestDates("CJK_Interp_Entries");
        for (String text : textes) {
            String[] chunks = text.split("<>");
            if (chunks.length > 5) {
                String interp = chunks[0];
                if (interp.startsWith("#")) {
                    interp = interp.substring(1);
                }
                String locale = chunks[4];
                String expected = chunks[5];

                if (! "gedcomx".equals(expected)) {
                    try {
                        dateResult = DateUtil.interpDate(interp, new StdLocale(locale));
                        String date02Res = dateResult.getDates().stream()
                                .map(date -> date.getDate().toGEDCOMX())
                                .collect(Collectors.joining("|", "\n|v2|", ""));
                        
                        buff.append("\n").append("\n");
                        buff.append(interp).append("|").append(expected).append("|").append(date02Res.contains(expected));
                        buff.append(date02Res);
                    } catch (GenDateException e) { }
                }
            }
        }

        System.out.println(buff.toString());
    }

    static List<String> loadTestDates(String fileName) {
        try(InputStream is = RunAcceptanceTestsV1V2.class.getResourceAsStream(fileName);
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr)) {
            return br.lines().collect(Collectors.toList());
        } catch(Exception ex) {
            return Collections.emptyList();
        }
    }
}