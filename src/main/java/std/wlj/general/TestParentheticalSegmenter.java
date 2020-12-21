/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;


import java.util.Arrays;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.core.parse.ParseContext;
import org.familysearch.standards.core.parse.Token;
import org.familysearch.standards.core.parse.segmenters.ParentheticalSegmenter;
import org.familysearch.standards.date.parser.GenDateParseConfig;
import org.familysearch.standards.place.search.parser.PlaceNameToken;
/**
 * @author wjohnson000
 *
 */
public class TestParentheticalSegmenter {

    public static void main(String...args) throws Exception {
        Token token = new PlaceNameToken("ab (cde) ef" , StdLocale.ENGLISH);
        ParseContext pc = new ParseContext(new GenDateParseConfig(), null);
        ParentheticalSegmenter ps = new ParentheticalSegmenter();
        List<Token> newTokens = ps.segment(Arrays.asList(token), pc);
        newTokens.forEach(System.out::println);
    }
}
