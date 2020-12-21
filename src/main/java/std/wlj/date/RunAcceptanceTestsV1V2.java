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
import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.date.api.model.DateResult;
import org.familysearch.standards.date.common.DateUtil;

/**
 * @author wjohnson000
 *
 */
public class RunAcceptanceTestsV1V2 {

    static final String[] dateFiles = {
        "Interp_Entries",
        "CJK_Interp_Entries",
        "CJK_Interp_Entries"
    };

    public static void main(String... args) throws Exception {
        runTests();
    }

    static void runTests() throws Exception {
        StringBuilder buffMatch    = new StringBuilder(100_000);
        StringBuilder buffNoMatch  = new StringBuilder(100_000);
        StringBuilder buffCJKMatch = new StringBuilder(100_000);

        for (String dateFile : dateFiles) {
            List<String> textes = loadTestDates(dateFile);

            int lineNo = 0;
            for (String text : textes) {
                lineNo++;
                String[] chunks = text.split("<>");
                if (chunks.length > 5) {
                    String interp = chunks[0];
                    if (interp.startsWith("#---> Review")) {
                        interp = interp.substring(12);
                    } else if (interp.startsWith("#--->Review")) {
                        interp = interp.substring(11);
                    } else if (interp.startsWith("#--->")) {
                        interp = interp.substring(5);
                    } else if (interp.startsWith("#")) {
                        interp = interp.substring(1);
                    }
                    interp = interp.trim();

                    String locale = chunks[4];
                    String expected = chunks[5];
                    
                    if (! "gedcomx".equals(expected)) {
                        try {
                            String dateResXx = interpDate(interp, StdLocale.makeLocale(locale)); 
                            
                            boolean match = dateResXx.contains(expected);
                            if (match) {
                                buffMatch.append("\n").append("\n")
                                    .append(dateFile).append(":").append(lineNo)
                                    .append("|").append(interp)
                                    .append("|").append(locale)
                                    .append("|").append(expected);
                                buffMatch.append("\n|||").append(dateResXx);
                            } else {
                                if (isCJK(interp)) {
                                    String dateResZh = interpDate(interp, StdLocale.CHINESE);
                                    String dateResJa = interpDate(interp, StdLocale.JAPANESE);
                                    String dateResKo = interpDate(interp, StdLocale.KOREAN);

                                    if (dateResZh.contains(expected)  ||  dateResJa.contains(expected)  ||  dateResKo.contains(expected)) {
                                        buffCJKMatch.append("\n").append("\n")
                                            .append(dateFile).append(":").append(lineNo)
                                            .append("|").append(interp)
                                            .append("|").append(locale)
                                            .append("|").append(expected);
                                        buffCJKMatch.append("\n|||").append(dateResXx)
                                            .append("|").append(dateResZh)
                                            .append("|").append(dateResJa)
                                            .append("|").append(dateResKo);
                                    } else {
                                        buffNoMatch.append("\n").append("\n")
                                            .append(dateFile).append(":").append(lineNo)
                                            .append("|").append(interp)
                                            .append("|").append(locale)
                                            .append("|").append(expected);
                                        buffNoMatch.append("\n|||").append(dateResXx)
                                            .append("|").append(dateResZh)
                                            .append("|").append(dateResJa)
                                            .append("|").append(dateResKo);
                                    }
                                } else {
                                    buffNoMatch.append("\n").append("\n")
                                        .append(dateFile).append(":").append(lineNo)
                                        .append("|").append(interp)
                                        .append("|").append(locale)
                                        .append("|").append(expected);
                                    buffNoMatch.append("\n|||").append(dateResXx);
                                }

                            }
                        } catch (GenDateException e) { }
                    }
                }
            }
        }

        System.out.println("=================================================================================");
        System.out.println("No Match");
        System.out.println("=================================================================================");
        System.out.println(buffNoMatch.toString());

        System.out.println("=================================================================================");
        System.out.println("CJK Match");
        System.out.println("=================================================================================");
        System.out.println("\n\n");
        System.out.println(buffCJKMatch.toString());

        System.out.println("=================================================================================");
        System.out.println("Expected Match");
        System.out.println("=================================================================================");
        System.out.println("\n\n");
        System.out.println(buffMatch.toString());
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

    static String interpDate(String text, StdLocale locale) throws GenDateException {
        StringBuilder buff = new StringBuilder();

        DateResult dateResult = DateUtil.interpDate(text, String.valueOf(locale), null, null, null);
        if (dateResult.getDates().isEmpty()) {
            buff.append("|");
        } else if (dateResult.getDates().size() == 1) {
            buff.append(dateResult.getDates().get(0).getDate().toGEDCOMX()).append("|");
        } else {
            buff.append(dateResult.getDates().get(0).getDate().toGEDCOMX())
                .append("|").append(dateResult.getDates().get(1).getDate().toGEDCOMX());
        }

        return buff.toString();
    }

    static boolean isCJK(String interp) {
        return interp.charAt(0) > 20_000;
    }
}