/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;


import java.util.Arrays;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.core.parse.ParseContext;
import org.familysearch.standards.core.parse.Token;
import org.familysearch.standards.core.parse.segmenters.RegexSegmenter;
import org.familysearch.standards.date.parser.GenDateParseConfig;
import org.familysearch.standards.place.search.parser.PlaceNameToken;
import org.familysearch.standards.place.search.parser.PlaceNameToken.PlaceTokenType;
/**
 * @author wjohnson000
 *
 */
public class TestRegexSegmenter {

    public static void main(String...args) throws Exception {
        Token token = new PlaceNameToken("abc|def;ghi:jkl" , StdLocale.ENGLISH);
        ParseContext pc = new ParseContext(new GenDateParseConfig(), null);
        RegexSegmenter ps = new RegexSegmenter("[\\|\"«»“”‟„;:]", null, PlaceTokenType.IS_HARD_BREAK, true, true, true);
        List<Token> newTokens = ps.segment(Arrays.asList(token), pc);
        newTokens.forEach(System.out::println);
    }
}
