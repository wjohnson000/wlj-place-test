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

import org.familysearch.standards.date.api.DateRequest;
import org.familysearch.standards.date.api.model.DateResult;
import org.familysearch.standards.date.api.model.GenDateInterpResult;
import org.familysearch.standards.date.api.model.Metadata;
import org.familysearch.standards.date.common.DateUtil;

/**
 * @author wjohnson000
 *
 */
public class TestV2InputFormatHint {

    public static void main(String... args) throws Exception {
        List<String> results = new ArrayList<>();

        List<String> textes = textesFromRaw();
        for (String text : textes) {
            DateResult  dates02A = new DateResult();
            DateResult  dates02B = new DateResult();
            DateResult  dates02C = new DateResult();
            DateResult  dates02D = new DateResult();

            System.out.println("\n" + text);

            try {
                dates02A = DateUtil.interpDate(new DateRequest(text, "en", "2000", null, null, null));
                dates02B = DateUtil.interpDate(new DateRequest(text, "en", "2000", null, null, "mdy"));
                dates02C = DateUtil.interpDate(new DateRequest(text, "en", "2000", null, null, "dmy"));
                dates02D = DateUtil.interpDate(new DateRequest(text, "en", "2000", null, null, "ymd"));
            } catch (Exception e) {
                System.out.println("  V2.ext: " + e.getMessage());
            }

            results.add("");
            for (GenDateInterpResult date : dates02A.getDates()) {
                results.add(text + "|---|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(Metadata.ATTR_MATCH_TYPE));
            }
            if (dates02A.getDates().isEmpty()) {
                results.add(text + "|---|<none>|<none>");
            }

            for (GenDateInterpResult date : dates02B.getDates()) {
                results.add(text + "|mdy|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(Metadata.ATTR_MATCH_TYPE));
            }
            if (dates02B.getDates().isEmpty()) {
                results.add(text + "|mdy|<none>|<none>");
            }

            for (GenDateInterpResult date : dates02C.getDates()) {
                results.add(text + "|dmy|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(Metadata.ATTR_MATCH_TYPE));
            }
            if (dates02C.getDates().isEmpty()) {
                results.add(text + "|dmy|<none>|<none>");
            }

            for (GenDateInterpResult date : dates02D.getDates()) {
                results.add(text + "|ymd|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(Metadata.ATTR_MATCH_TYPE));
            }
            if (dates02D.getDates().isEmpty()) {
                results.add(text + "|ymd|<none>|<none>");
            }
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

        textes.add("04 May 00");
        textes.add("5/6/1900");
        textes.add("6/5/1900");
        textes.add("6 5 1900");
        textes.add("6 25 1900");
        textes.add("26 5 1900");
        textes.add("1900/6/5");

        return textes;
    }
}