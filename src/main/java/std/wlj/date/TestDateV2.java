/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.familysearch.standards.core.LocalizedData;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.core.parse.ParseException;
import org.familysearch.standards.date.GenDateInterpResult;
import org.familysearch.standards.date.parser.GenDateParser;
import org.familysearch.standards.date.parser.handler.CJKImperialHandler;
import org.familysearch.standards.date.shared.SharedUtil;
import org.familysearch.standards.date.shared.ThreadLocalExperiment;

/**
 * @author wjohnson000
 *
 */
public class TestDateV2 {

    static final Set<String> experiments = new HashSet<>();
    static {
        experiments.add(CJKImperialHandler.EXPERIMENT_ENABLE_V2);
    }

    public static void main(String...arg) throws Exception {
        GenDateParser parser = new GenDateParser();

//        testDate(parser, "5 November 1955", StdLocale.ENGLISH);        // OK
//        testDate(parser, "10/06/12", StdLocale.ENGLISH);               // OK
//        testDate(parser, "10/06/80", StdLocale.ENGLISH);               // OK
//        testDate(parser, "20/06/80", StdLocale.ENGLISH);               // OK
//        testDate(parser, "10/26/80", StdLocale.ENGLISH);               // OK
//        testDate(parser, "10/26/1980", StdLocale.ENGLISH);             // OK
//        testDate(parser, "26 Mar.1976", StdLocale.ENGLISH);            // OK
//        testDate(parser, "30 de janeiro de 1747", StdLocale.ENGLISH);  // OK
//        testDate(parser, "1352年2月6日", StdLocale.CHINESE);           // OK

//        testDate(parser, "民國", StdLocale.CHINESE);
//        testDate(parser, "民國5", StdLocale.CHINESE);
//        testDate(parser, "民國5年", StdLocale.CHINESE);
//        testDate(parser, "民國5年1", StdLocale.CHINESE);
//        testDate(parser, "民國5年1月", StdLocale.CHINESE);
//        testDate(parser, "民國三年一月", StdLocale.CHINESE);
//        testDate(parser, "民國5年1月15", StdLocale.CHINESE);
//        testDate(parser, "民國5年1月15日", StdLocale.CHINESE);
        testDate(parser, "民國105年1月15日", StdLocale.CHINESE);
        testDate(parser, "民國105年1月15日 Sigh", StdLocale.CHINESE);
//        testDate(parser, "雍正癸", StdLocale.CHINESE);
//        testDate(parser, "清遜帝", StdLocale.CHINESE);                  // YES + YES
//        testDate(parser, "清永明王", StdLocale.CHINESE);                // YES + NO
//        testDate(parser, "永明王", StdLocale.CHINESE);                // YES + NO
//        testDate(parser, "清遜帝永明王", StdLocale.CHINESE);            // YES + NO
//        testDate(parser, "清遜帝宣統", StdLocale.CHINESE);              // YES + YES
//        testDate(parser, "漢成帝建始四年九月", StdLocale.CHINESE);      // YES + YES* 
//        testDate(parser, "漢桓帝建和三年秋七月", StdLocale.CHINESE);    // YES + NO
//        testDate(parser, "漢桓帝建和三年", StdLocale.CHINESE);          // YES + YES
//        testDate(parser, "太康五年正月", StdLocale.CHINESE);            // EXC + NO
//        testDate(parser, "時嘉慶癸亥三月三十日也", StdLocale.CHINESE);   // EXC + NO
//        testDate(parser, "哀帝建平四年夏", StdLocale.CHINESE);          // YES + NO
//        testDate(parser, "靈帝熹平三年", StdLocale.CHINESE);            // YES + YES*
//        testDate(parser, "辛丑十一月十九日", StdLocale.CHINESE);        // YES (lots) + NO
    }

    static void testDate(GenDateParser parser, String text, StdLocale locale) {
        System.out.println("\n==========================================================================================");
        System.out.println("Input: " + text);

        for (int times=1;  times<=2;  times+=1) {
            if (times == 1) {
                ThreadLocalExperiment.clear();
            } else {
                ThreadLocalExperiment.set(experiments);
            }

            try {
                List<GenDateInterpResult> dateResult = parser.parse(new LocalizedData<>(text, locale));
                for (int len=0;  len<dateResult.size();  len++) {
                    StringBuilder buff = new StringBuilder(64);
                    buff.append("v2=").append(times==2);
                    buff.append("|").append(dateResult.get(len).getDate().toGEDCOMX());
                    buff.append("|").append(dateResult.get(len).getAttrAsBoolean(SharedUtil.ATTR_USED_V1));
                    buff.append("|").append(dateResult.get(len).getAttrAsBoolean(SharedUtil.ATTR_V1_FULLY_NORMALIZED));
                    buff.append("|").append(dateResult.get(len).getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
                    System.out.println(buff.toString());
                }
            } catch (ParseException ex) {
                System.out.println("   Ex: " + ex.getMessage());
            }
            System.out.println();
        }
    }
}
