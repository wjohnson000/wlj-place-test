/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.List;

import org.familysearch.standards.core.LocalizedData;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.core.parse.ParseException;
import org.familysearch.standards.date.GenDateInterpResult;
import org.familysearch.standards.date.parser.GenDateParser;
import org.familysearch.standards.date.shared.SharedUtil;

/**
 * @author wjohnson000
 *
 */
public class TestDateV2 {

    public static void main(String...arg) throws Exception {
        GenDateParser parser = new GenDateParser();

        testDate(parser, "5 November 1955", StdLocale.ENGLISH);
        testDate(parser, "26 Mar.1976", StdLocale.ENGLISH);
        testDate(parser, "30 de janeiro de 1747", StdLocale.ENGLISH);
        testDate(parser, "1352年2月6日", StdLocale.CHINESE);

        testDate(parser,"清遜帝永明王", StdLocale.CHINESE);
        testDate(parser,"清遜帝宣統", StdLocale.CHINESE);
        testDate(parser,"漢成帝建始四年九月", StdLocale.CHINESE);
        testDate(parser,"漢桓帝建和三年秋七月", StdLocale.CHINESE);
        testDate(parser,"漢桓帝建和三年", StdLocale.CHINESE);
        testDate(parser,"太康五年正月", StdLocale.CHINESE);
        testDate(parser,"時嘉慶癸亥三月三十日也", StdLocale.CHINESE);
        testDate(parser,"哀帝建平四年夏", StdLocale.CHINESE);
        testDate(parser,"靈帝熹平三年", StdLocale.CHINESE);
        testDate(parser,"辛丑十一月十九日", StdLocale.CHINESE);
    }

    static void testDate(GenDateParser parser, String text, StdLocale locale) {
        System.out.println("\n\n==========================================================================================");
        System.out.println("Input: " + text);
        try {
            List<GenDateInterpResult> dateResult = parser.parse(new LocalizedData<>(text, locale));
            System.out.println("  Out: " + dateResult.get(0).getDate().toGEDCOMX());
            System.out.println("   V1? " + dateResult.get(0).getAttrAsBoolean(SharedUtil.ATTR_USED_V1));
            System.out.println("   V1? " + dateResult.get(0).getAttrAsBoolean(SharedUtil.ATTR_V1_FULLY_NORMALIZED));
            System.out.println("  Typ: " + dateResult.get(0).getAttrAsString(SharedUtil.ATTR_MATCH_TYPE));
        } catch (ParseException ex) {
            System.out.println("   Ex: " + ex.getMessage());
        }
        System.out.println();
    }
}
