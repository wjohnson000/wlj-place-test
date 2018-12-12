/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import org.familysearch.standards.core.LocalizedData;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.date.model.DateResult;
import org.familysearch.standards.date.parser.GenDateParser;

/**
 * @author wjohnson000
 *
 */
public class TestDateV2 {
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
//        testDate(parser, "民國105年1月15日", StdLocale.CHINESE);
//        testDate(parser, "民國105年1月15日 Sigh", StdLocale.CHINESE);
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
//        testDate(parser, "27 Jun 1913 (aged 69) Kell, Marion County, Illinois, USA", StdLocale.ENGLISH);
        testDate(parser, "Sept 3/14 1752", StdLocale.ENGLISH);
        testDate(parser, "Sept 3/14, 1752", StdLocale.ENGLISH);
        testDate(parser, "10-20 Mar 2020", StdLocale.ENGLISH);
        testDate(parser, "10-20 Mar, 2020", StdLocale.ENGLISH);
        testDate(parser, "12/25 Mar 2020", StdLocale.ENGLISH);
        testDate(parser, "12/25 Mar, 2020", StdLocale.ENGLISH);
        testDate(parser, "12-25 Mar 2020", StdLocale.ENGLISH);
        testDate(parser, "12-25 Mar, 2020", StdLocale.ENGLISH);
        testDate(parser, "8/28 Mar 2020", StdLocale.ENGLISH);
        testDate(parser, "8/28 Mar, 2020", StdLocale.ENGLISH);
    }

    static void testDate(GenDateParser parser, String text, StdLocale locale) {
        System.out.println("\n==========================================================================================");
        System.out.println("Input: " + text);

        try {
            DateResult dateResult = parser.parse(new LocalizedData<>(text, locale), null, null, null);
            for (int len=0;  len<dateResult.getDates().size();  len++) {
                StringBuilder buff = new StringBuilder(64);
                buff.append(text);
                buff.append("|").append(dateResult.getDates().get(len).getDate().toGEDCOMX());
                System.out.println(buff.toString());
            }
        } catch (GenDateException ex) {
            System.out.println("   Ex: " + ex.getMessage());
        }
    }
}
