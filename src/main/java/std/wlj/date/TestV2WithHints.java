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

import org.familysearch.standards.date.api.model.DateResult;
import org.familysearch.standards.date.api.model.GenDateInterpResult;
import org.familysearch.standards.date.api.model.Metadata;
import org.familysearch.standards.date.common.DateUtil;

/**
 * @author wjohnson000
 *
 */
public class TestV2WithHints {

    public static void main(String... args) throws Exception {
        List<String> results = new ArrayList<>();

        List<String> textes = textesFromRaw();
        for (String text : textes) {
            DateResult  dates01 = new DateResult();
            DateResult  dates02 = new DateResult();
            DateResult  dates03 = new DateResult();

            System.out.println("\n" + text);

            try {
                dates01 = DateUtil.interpDate(text,"en", "9999", null, null);
            } catch (Exception e) {
                System.out.println("  V1.ext: " + e.getMessage());
            }
//
//            try {
//                dates02 = DateUtil.interpDate(text, StdLocale.ENGLISH, "Before 09 May 2019", null, null);
//            } catch (Exception e) {
//                System.out.println("  V2.ext: " + e.getMessage());
//            }
//
//            try {
//                dates03 = DateUtil.interpDate(text, StdLocale.ENGLISH, "After 2019-05-07", null, null);
//            } catch (Exception e) {
//                System.out.println("  V2.ext: " + e.getMessage());
//            }

            results.add("");
            for (GenDateInterpResult date : dates01.getDates()) {
                System.out.println("  gx02: " + text + "|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(Metadata.ATTR_MATCH_TYPE));
                results.add(text + "|Date|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(Metadata.ATTR_MATCH_TYPE));
            }
            if (dates01.getDates().isEmpty()) {
                System.out.println("  gx02: " + text + "|<none>|<none>");
                results.add(text + "|Date|<none>|<none>");
            }
//            for (GenDateInterpResult date : dates02.getDates()) {
//                System.out.println("  hint: " + text + "|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
//                results.add(text + "|Before|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
//            }
//            if (dates02.getDates().isEmpty()) {
//                System.out.println("  hint: " + text + "|<none>|<none>");
//                results.add(text + "|Before|<none>|<none>");
//            }
//            for (GenDateInterpResult date : dates03.getDates()) {
//                System.out.println("  hint: " + text + "|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
//                results.add(text + "|After|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
//            }
//            if (dates03.getDates().isEmpty()) {
//                System.out.println("  hint: " + text + "|<none>|<none>");
//                results.add(text + "|After|<none>|<none>");
//            }
        }

        System.out.println();
        System.out.println("========================================================================================================================");
        System.out.println("========================================================================================================================");
        System.out.println();
        results.forEach(System.out::println);
    }

    static List<String> textesFromFile(String filename) throws IOException {
        return Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8);
    }

    static List<String> textesFromRaw() {
        List<String> textes = new ArrayList<>();

//        textes.add("3/11");
//        textes.add("3/11/00");
//        textes.add("3/11/01");
//        textes.add("PLUS");
//        textes.add("3/0/11");
//        textes.add("0/3/11");
//
//        textes.add("04 May 00");
//
//        textes.add("Saturday");
//        textes.add("Before Samedi");
//        textes.add("After Friday");
//        textes.add("About thurs");

        textes.add("March 05");

        return textes;
    }
}