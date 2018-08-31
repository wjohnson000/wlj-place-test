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
import org.familysearch.standards.date.GenDateInterpResult;
import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.date.shared.SharedUtil;
import org.familysearch.standards.date.v1.DateV1Shim;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class Compare_V1_V2_Assisted {

    private static Map<String, List<GenDateInterpResult>> v1Results = new TreeMap<>();
    private static Map<String, List<GenDateInterpResult>> v2Results = new TreeMap<>();

    private static List<String> matchRes = new ArrayList<>();
    private static List<String> oldV1Res = new ArrayList<>();
    private static List<String> otherRes = new ArrayList<>();
    private static List<String> emptyRes = new ArrayList<>();

    public static void main(String... args) throws Exception {
        List<String> textes = Files.readAllLines(Paths.get("C:/temp/date-assisted.txt"), Charset.forName("UTF-8"));
        for (String text : textes) {
            String[] chunks = PlaceHelper.split(text, '|');
            String tText = chunks[0].trim();
            String locale = chunks[1];
            if (locale.isEmpty()) {
                locale = chunks[2];
            }
            if (locale.isEmpty()) {
                locale = "en";
            }

            if (! tText.isEmpty()) {
                v1Results.put(tText, getV1(tText));
                v2Results.put(tText, getV2(tText, locale));
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

        Files.write(Paths.get("C:/temp/fuzzy-match.txt"), matchRes, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(Paths.get("C:/temp/fuzzy-empty.txt"), emptyRes, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(Paths.get("C:/temp/fuzzy-oldV1.txt"), oldV1Res, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        Files.write(Paths.get("C:/temp/fuzzy-other.txt"), otherRes, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static List<GenDateInterpResult> getV1(String text) {
        try {
            return DateV1Shim.interpDate(text);
        } catch (GenDateException e) {
            return Collections.emptyList();
        }
    }

    static List<GenDateInterpResult> getV2(String text, String locale) {
        
        try {
            return DateUtil.interpDate(text, new StdLocale(locale));
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

        if (gedcomx.startsWith("A")) {
            gedcomx = gedcomx.substring(1);
        }
        if (gedcomx.startsWith("/")) {
            gedcomx = gedcomx.substring(1);
        }
        if (gedcomx.startsWith("A")) {
            gedcomx = gedcomx.substring(1);
        }

        int ndx01 = gedcomx.indexOf('-', 1);
        String year = (ndx01 < 0) ? gedcomx : gedcomx.substring(0, ndx01);

        int ndx02 = year.indexOf("/");
        if (ndx02 > 0) {
            year = year.substring(0, ndx02);
        }

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