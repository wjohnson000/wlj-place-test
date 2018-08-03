/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.DateUtil;
import org.familysearch.standards.date.GenDateException;
import org.familysearch.standards.date.GenDateInterpResult;
import org.familysearch.standards.date.GenDateParsingException;
import org.familysearch.standards.date.shared.SharedUtil;
import org.familysearch.standards.date.v1.DateV1Shim;

/**
 * @author wjohnson000
 *
 */
public class Compare_V1_V2 {

//    static String[] textes = {
//        "順帝三年七月七日",
//        "順帝丙寅叄年七月七日",
//        "金世宗大定2年5月5日",
//        "安政5年6月8日",
//        "清世祖順治元年1月1日",
//        "清世祖順治1年1月1日",
//        "陳文帝天嘉年1月1日",
//        "吳大帝嘉禾年1月1日",
//        "民國10年10月10日",
//        "安政5年6月8",
//        "西元1921年11月9日",
//        "宣統三年十二月三十日",
//        "宣統三年十二月三十一日",
//        "光緖丁酉年十一月二十九日",
//        "朝鮮太祖洪武壬申年七月十七日", 
//        "乾隆丙午年二月廿三日未時",
//        "大正五年一月六號",
//        "清世祖順治元年1月1日", 
//    };

    private static Map<String, List<GenDateInterpResult>> v1Results = new TreeMap<>();
    private static Map<String, List<GenDateInterpResult>> v2Results = new TreeMap<>();

    private static List<String> matchRes = new ArrayList<>();
    private static List<String> oldV1Res = new ArrayList<>();
    private static List<String> otherRes = new ArrayList<>();
    private static List<String> emptyRes = new ArrayList<>();

    public static void main(String... args) throws Exception {
        List<String> textes = Files.readAllLines(Paths.get("C:/temp/zh-dates.txt"), Charset.forName("UTF-8"));
        for (String text : textes) {
            String tText = text.trim();
            if (! tText.isEmpty()) {
                v1Results.put(tText, getV1(tText));
                v2Results.put(tText, getV2(tText));
            }
        }

        for (String text : v1Results.keySet()) {
            List<String> results = compareResults(text);
            if (results.stream().allMatch(txt -> txt.contains("v1-only"))) {
                oldV1Res.add("");
                oldV1Res.addAll(results);
            } else if (results.stream().allMatch(txt -> txt.contains("match"))) {
                matchRes.add("");
                matchRes.addAll(results);
            } else if (results.stream().allMatch(txt -> txt.contains("no-result"))) {
                emptyRes.add("");
                emptyRes.addAll(results);
            } else {
                otherRes.add("");
                otherRes.addAll(results);
            }
        }

        Files.write(Paths.get("C:/temp/cjk-match.txt"), matchRes, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(Paths.get("C:/temp/cjk-empty.txt"), emptyRes, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(Paths.get("C:/temp/cjk-oldV1.txt"), oldV1Res, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(Paths.get("C:/temp/cjk-other.txt"), otherRes, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static List<GenDateInterpResult> getV1(String text) {
        try {
            return DateV1Shim.interpDate(text);
        } catch (GenDateParsingException e) {
            return Collections.emptyList();
        }
    }

    static List<GenDateInterpResult> getV2(String text) {
        try {
            return DateUtil.interpDate(text, StdLocale.CHINESE);
        } catch (GenDateException e) {
            return Collections.emptyList();
        }
    }
    
    static List<String> compareResults(String text) {
        List<String> results = new ArrayList<>();

        List<GenDateInterpResult> v1Dates = v1Results.get(text);
        List<GenDateInterpResult> v2Dates = v2Results.get(text);

        boolean first = true;
        GenDateInterpResult v1Date = (v1Dates.isEmpty()) ? null : v1Dates.remove(0);
        GenDateInterpResult v2Date = (v2Dates.isEmpty()) ? null : v2Dates.remove(0);
        while (v1Date != null  ||  v2Date != null) {
            if (v1Date == null) {
                results.add(formatResult(text, first, v1Date, v2Date));
                v2Date = (v2Dates.isEmpty()) ? null : v2Dates.remove(0);
            } else if (v2Date == null) {
                results.add(formatResult(text, first, v1Date, v2Date));
                v1Date = (v1Dates.isEmpty()) ? null : v1Dates.remove(0);
            } else {
                int v1Year = getYear(v1Date);
                int v2Year = getYear(v2Date);
                if (v1Year < v2Year) {
                    results.add(formatResult(text, first, v1Date, null));
                    v1Date = (v1Dates.isEmpty()) ? null : v1Dates.remove(0);
                } else if (v2Year < v1Year) {
                    results.add(formatResult(text, first, null, v2Date));
                    v2Date = (v2Dates.isEmpty()) ? null : v2Dates.remove(0);    
                } else {
                    results.add(formatResult(text, first, v1Date, v2Date));
                    v1Date = (v1Dates.isEmpty()) ? null : v1Dates.remove(0);
                    v2Date = (v2Dates.isEmpty()) ? null : v2Dates.remove(0);    
                }
            }
            first = false;
        }

        if (first) {
            results.add(text + "|||||no-result");
        }

        return results;
    }

    static int getYear(GenDateInterpResult dateRes) {
        String gedcomx = dateRes.getDate().toGEDCOMX();
        int ndx01 = gedcomx.indexOf('-');
        if (ndx01 == 0) {
            ndx01 = gedcomx.indexOf('-', 1);
        }
        String year = (ndx01 < 0) ? gedcomx : gedcomx.substring(0, ndx01);

        try {
            return Integer.parseInt(year);
        } catch(NumberFormatException | NullPointerException ex) {
            System.out.println("Unable to parse year from: " + gedcomx + " --> '" + year + "'");
            return 0;
        }
    }

    static String formatResult(String text, boolean first, GenDateInterpResult v1Date, GenDateInterpResult v2Date) {
        StringBuilder buff = new StringBuilder();

        buff.append(first ? text : "");
        buff.append("|").append(v1Date == null ? "" : v1Date.getDate().toGEDCOMX());
        buff.append("|").append(v1Date == null ? "" : v1Date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1));
        buff.append("|").append(v2Date == null ? "" : v2Date.getDate().toGEDCOMX());
        buff.append("|").append(v2Date == null ? "" : v2Date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1));

        String comment = "";
        if (v1Date == null) {
            comment = "new";
        } else if (v2Date == null) {
        } else if (v2Date.getAttrAsBoolean(SharedUtil.ATTR_USED_V1)) {
            comment = "v1-only";
        } else {
            comment = "match";
        }
        buff.append("|").append(comment);

        return buff.toString();
    }
}