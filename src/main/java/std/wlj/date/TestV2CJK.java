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

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.model.DateResult;
import org.familysearch.standards.date.model.GenDateInterpResult;
import org.familysearch.standards.date.shared.SharedUtil;

/**
 * @author wjohnson000
 *
 */
public class TestV2CJK {

    public static void main(String... args) throws Exception {
        List<String> results = new ArrayList<>();

        List<String> textes = textesFromRaw();
        for (String text : textes) {
            DateResult  datesEn = new DateResult();
            DateResult  datesZh = new DateResult();
            DateResult  datesJa = new DateResult();
            DateResult  datesKo = new DateResult();

            System.out.println("\n" + text);

            try {
                datesEn = DateUtil.interpDate(text, StdLocale.ENGLISH, null, null, null);
            } catch (Exception e) {
                System.out.println("  V1.ext: " + e.getMessage());
            }

            try {
                datesZh = DateUtil.interpDate(text, StdLocale.CHINESE, null, null, null);
            } catch (Exception e) {
                System.out.println("  V1.ext: " + e.getMessage());
            }

            try {
                datesJa = DateUtil.interpDate(text, StdLocale.JAPANESE, null, null, null);
            } catch (Exception e) {
                System.out.println("  V1.ext: " + e.getMessage());
            }

            try {
                datesKo = DateUtil.interpDate(text, StdLocale.KOREAN, null, null, null);
            } catch (Exception e) {
                System.out.println("  V2.ext: " + e.getMessage());
            }

            results.add("");
            for (GenDateInterpResult date : datesEn.getDates()) {
                System.out.println("  gxEN: " + text + "|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
                results.add(text + "|en|Date 2.0|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
            }
            if (datesEn.getDates().isEmpty()) {
                System.out.println("  gxEN: " + text + "|<none>|<none>");
                results.add(text + "|en|Date 2.0|<none>|<none>");
            }
            for (GenDateInterpResult date : datesZh.getDates()) {
                System.out.println("  gxZH: " + text + "|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
                results.add(text + "|zh|Date 2.0|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
            }
            if (datesZh.getDates().isEmpty()) {
                System.out.println("  gxZH: " + text + "|<none>|<none>");
                results.add(text + "|zh|Date 2.0|<none>|<none>");
            }
            for (GenDateInterpResult date : datesJa.getDates()) {
                System.out.println("  gxJA: " + text + "|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
                results.add(text + "|ja|Date 2.0|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
            }
            if (datesJa.getDates().isEmpty()) {
                System.out.println("  gxJA: " + text + "|<none>|<none>");
                results.add(text + "|ja|Date 2.0|<none>|<none>");
            }
            for (GenDateInterpResult date : datesKo.getDates()) {
                System.out.println("  gxKO: " + text + "|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
                results.add(text + "|ko|Date 2.0|" + date.getDate().toGEDCOMX() + "|" + date.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
            }
            if (datesKo.getDates().isEmpty()) {
                System.out.println("  gxKO: " + text + "|<none>|<none>");
                results.add(text + "|ko|Date 2.0|<none>|<none>");
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

        textes.add("朝鮮太宗永樂戊戌年");
//        textes.add("朝鮮世宗永樂己亥年");
//        textes.add("朝鮮世宗永樂壬寅年");

        return textes;
    }
}