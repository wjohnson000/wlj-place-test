/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.core.parse.ParseContext;
import org.familysearch.standards.core.parse.ParseException;
import org.familysearch.standards.core.parse.Token;
import org.familysearch.standards.core.parse.segmenters.RegexSegmenter;
import org.familysearch.standards.date.parser.GenDateParseConfig;
import org.familysearch.standards.date.parser.GenDateToken;
import org.familysearch.standards.date.parser.GenDateToken.Type;

/**
 * @author wjohnson000
 *
 */
public class TestRegexHandler {

    public static final String APOSTROPHE_YR_RANGE   = "([0-9]{4})[\\']*[sS]";
    public static final String APOSTROPHE_YR_RANGE_X = "([0-9]{4})[\\']*[sS][^\\\\p{IsAlphabetic}]*";
    public static final String APOSTROPHE_YR_RANGE_Y = "([0-9]{4})[\\']*[sS](?:[^\\\\p{IsAlphabetic}]|$|\\s)";

    public static final String[] testes = {
        "1990",
        "1990s",
        "1990'S",
        "1990Sept",
        "1859sep5",
        "1859sep5",
        "1859s era",
        "1859s, 1990's, 1900",
    };

    public static void main(String...args) throws ParseException {
        ParseContext context = new ParseContext(new GenDateParseConfig(), null);

        RegexSegmenter rex01 = new RegexSegmenter(APOSTROPHE_YR_RANGE, null, Type.YEAR_RANGE, true, false, false);
        RegexSegmenter rex02 = new RegexSegmenter(APOSTROPHE_YR_RANGE_X, null, Type.YEAR_RANGE, true, false, false);
        RegexSegmenter rex03 = new RegexSegmenter(APOSTROPHE_YR_RANGE_Y, null, Type.YEAR_RANGE, true, false, false);

        for (String test : testes) {
            GenDateToken token = new GenDateToken(test, StdLocale.ENGLISH);
            System.out.println(test);
            List<Token> tok01 = rex01.segment(Arrays.asList(token), context);
            List<Token> tok02 = rex02.segment(Arrays.asList(token), context);
            List<Token> tok03 = rex03.segment(Arrays.asList(token), context);

            tok01.forEach(tok -> System.out.println("  01: " + tok));
            tok02.forEach(tok -> System.out.println("  02: " + tok));
            tok03.forEach(tok -> System.out.println("  03: " + tok));
            System.out.println();
//            System.out.println("   " + (Pattern.compile(APOSTROPHE_YR_RANGE)).matcher(test).matches());
//            System.out.println("   " + (Pattern.compile(APOSTROPHE_YR_RANGE_X)).matcher(test).matches());
//            System.out.println("   " + (Pattern.compile(APOSTROPHE_YR_RANGE_Y)).matcher(test).matches());
//            System.out.println();
        }
    }
}
