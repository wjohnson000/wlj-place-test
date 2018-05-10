/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.familysearch.standards.core.LocalizedData;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.GenDateInterpResult;
import org.familysearch.standards.date.parser.GenDateParser;
import org.familysearch.standards.date.parser.handler.CJKImperialHandler;
import org.familysearch.standards.date.shared.SharedUtil;
import org.familysearch.standards.date.shared.ThreadLocalExperiment;
import org.familysearch.standards.date.v1.DateV1Shim;

/**
 * @author wjohnson000
 *
 */
public class TestDateV2Lots {

    static final Set<String> experiments = new HashSet<>();
    static {
        experiments.add(CJKImperialHandler.EXPERIMENT_ENABLE_V2);
    }

    public static void main(String...arg) throws Exception {
        List<String> datesToParse = Files.readAllLines(Paths.get("C:/temp/zh-dates.txt"), Charset.forName("UTF-8"));
        GenDateParser parser = new GenDateParser();

        GenDateInterpResult resultWithExp = null;
        GenDateInterpResult resultNoneExp = null;
        GenDateInterpResult resultV1Shim  = null;
        List<GenDateInterpResult> parseResult;

        int matchCount = 0;
        String prettyPrint;
        List<String> okResults  = new ArrayList<>(datesToParse.size());
        List<String> badResults = new ArrayList<>(datesToParse.size());

        for (String dateToParse : datesToParse) {
            resultNoneExp = null;
            resultV1Shim = null;

            try {
                ThreadLocalExperiment.set(experiments);
                parseResult = parser.parse(new LocalizedData<>(dateToParse, StdLocale.CHINESE));
                resultWithExp = parseResult.get(0);

                ThreadLocalExperiment.clear();
                parseResult = parser.parse(new LocalizedData<>(dateToParse, StdLocale.CHINESE));
                resultNoneExp = parseResult.get(0);
                
                parseResult = DateV1Shim.interpDate(dateToParse);
                resultV1Shim = parseResult.get(0);

                if (resultWithExp.getAttrAsBoolean(SharedUtil.ATTR_USED_V1)  &&  resultNoneExp.getAttrAsBoolean(SharedUtil.ATTR_USED_V1)) {
                     prettyPrint = format(dateToParse, "", null, null, resultV1Shim);
                } else if (resultWithExp.getAttrAsBoolean(SharedUtil.ATTR_USED_V1)  &&  ! resultNoneExp.getAttrAsBoolean(SharedUtil.ATTR_USED_V1)) {
                    boolean match = resultNoneExp.getDate().toGEDCOMX().equals(resultV1Shim.getDate().toGEDCOMX());
                    if (match) matchCount++;
                    String message = (match) ? ">> MATCH <<" : "";
                    prettyPrint = format(dateToParse, message, null, resultNoneExp, resultV1Shim);
                } else if (! resultWithExp.getAttrAsBoolean(SharedUtil.ATTR_USED_V1)  &&  resultNoneExp.getAttrAsBoolean(SharedUtil.ATTR_USED_V1)) {
                    boolean match = resultWithExp.getDate().toGEDCOMX().equals(resultV1Shim.getDate().toGEDCOMX());
                    if (match) matchCount++;
                    String message = (match) ? ">> MATCH <<" : "";
                    prettyPrint = format(dateToParse, message, resultWithExp, null, resultV1Shim);
                } else {
                    String matchWith = resultWithExp.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE);
                    String matchNone = resultNoneExp.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE);
                    if (matchWith != null  &&  matchWith.equals(matchNone)) {
                        boolean match = resultNoneExp.getDate().toGEDCOMX().equals(resultV1Shim.getDate().toGEDCOMX());
                        if (match) matchCount++;
                        String message = (match) ? ">> MATCH <<" : "";
                        prettyPrint = format(dateToParse, message, null, resultNoneExp, resultV1Shim);
                    } else {
                        prettyPrint = format(dateToParse, ">> UNKNOWN <<", resultWithExp, resultNoneExp, resultV1Shim);
                    }
                }

                okResults.add(prettyPrint);
            } catch(Exception ex) {
                prettyPrint = format(dateToParse, ex.getMessage(), resultWithExp, resultNoneExp, resultV1Shim);
                badResults.add(prettyPrint);
            }
        }

        System.out.println();
        System.out.println();
        okResults.forEach(System.out::println);

        System.out.println();
        System.out.println();
        badResults.forEach(System.out::println);

        System.out.println("\n\nMatch-count=" + matchCount);
    }

    static String format(String text, String message, GenDateInterpResult withExp, GenDateInterpResult noExp, GenDateInterpResult v1Shim) {
        StringBuilder buff = new StringBuilder(128);

        buff.append(text);

        if (withExp == null) {
            buff.append("||");
        } else {
            buff.append("|").append(withExp.getDate().toGEDCOMX());
            buff.append("|").append(withExp.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
        }

        if (noExp == null) {
            buff.append("||");
        } else {
            buff.append("|").append(noExp.getDate().toGEDCOMX());
            buff.append("|").append(noExp.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
        }

        if (v1Shim == null) {
            buff.append("||");
        } else {
            buff.append("|").append(v1Shim.getDate().toGEDCOMX());
            buff.append("|").append(v1Shim.getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
        }

        buff.append("|").append(message);

        return buff.toString();
    }
}
