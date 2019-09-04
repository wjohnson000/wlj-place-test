/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
//import java.util.stream.Collectors;

import org.familysearch.standards.date.api.model.DateResult;
import org.familysearch.standards.date.api.model.GenDateInterpResult;
import org.familysearch.standards.date.api.model.DateMetadata;
import org.familysearch.standards.date.common.DateUtil;

import std.wlj.date.v1.DateV1Shim;

/**
 * @author wjohnson000
 *
 */
public class TestDualDateStuff {

    public static void main(String... args) throws Exception {
        List<String> results = new ArrayList<>();

        List<String> textes = textesFromRaw();

        long time0 = System.nanoTime();
        for (String text : textes) {
//            String hex = text.chars()
//                    .mapToLong(ch -> (long)ch)
//                    .mapToObj(ll -> Long.toHexString(ll))
//                    .collect(Collectors.joining(" ", "[", "]"));
//            System.out.println(text + " --> " + hex);
            List<GenDateInterpResult> dates01 = new ArrayList<>();
            DateResult                dates02 = new DateResult();

            System.out.println("\n" + text);

            try {
                dates01 = DateV1Shim.interpDate(text);
            } catch (Exception e) {
                System.out.println("  V1.ext: " + e.getMessage());
            }

            try {
                dates02 = DateUtil.interpDate(text, "en", null, null, null);
            } catch (Exception e) {
                System.out.println("  V2.ext: " + e.getMessage());
            }

            results.add("");
            for (GenDateInterpResult date : dates01) {
                System.out.println("  gx01: " + text + "|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(DateMetadata.ATTR_MATCH_TYPE));
                results.add(text + "|Date 1.0|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(DateMetadata.ATTR_MATCH_TYPE));
            }
            for (GenDateInterpResult date : dates02.getDates()) {
                System.out.println("  gx02: " + text + "|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(DateMetadata.ATTR_MATCH_TYPE));
                results.add(text + "|Date 2.0|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(DateMetadata.ATTR_MATCH_TYPE));
            }
            if (dates02.getDates().isEmpty()) {
                System.out.println("  gx02: " + text + "|<none>|<none>");
                results.add(text + "|Date 2.0|<none>|<none>");
            }
        }
        long time1 = System.nanoTime();

        System.out.println();
        System.out.println("========================================================================================================================");
        System.out.println("========================================================================================================================");
        System.out.println();
        results.forEach(System.out::println);
        System.out.println("\n\nTTT: " + (time1 - time0) / 1_000_000.0);
    }

    static List<String> textesFromFile(String filename) throws IOException {
        return Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
    }

    static List<String> textesFromRaw() {
        List<String> textes = new ArrayList<>();

        textes.add("22 Feb/5 Mar 1752/3");
        textes.add("Sept 3/14, 1752");
        textes.add("10/21 Feb 1759/60");
        textes.add("1 Mar 1759/60");
        textes.add("1 Dec 1910 / 12 Jan 1911");
        textes.add("02/185");
        textes.add("14 Oct 1831 (age 71)");
        textes.add("19-25 March 1803");
        textes.add("13 January 1613/1614");
        textes.add("13 January 1613/14");
        textes.add("1611/2");
        textes.add("1611/12");

        return textes;
    }
}