/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.regex.Pattern;

/**
 * @author wjohnson000
 *
 */
public class SimpleDMYPatterns {

    public static final Pattern DMY_SIMPLE_DATE_PAT = Pattern.compile(
        "(?:([0123]{0,1}[0-9]?))[ \\+]" +
        "(?:(" +
    
            // English month names and abbreviations
            "[Jj]an(uary)?|" +
            "[Ff]eb(ruary)?|" +
            "[Mm]ar(ch)?|" +
            "[Aa]pr(il)?|" +
            "[Mm]ay|" +
            "[Jj]un(e)?|" +
            "[Jj]ul(y)?|" +
            "[Aa]ug(ust)?|" +
            "[Ss]ep(t)?(ember)?|" +
            "[Oo]ct(ober)?|" +
            "[Nn]ov(ember)?|" +
            "[Dd]ec(ember)?|" +

            // Spanish month names
            "[Ee]nero|" +
            "[Ff]ebrero|" +
            "[Mm]arzo|" +
            "[Aa]bril|" +
            "[Mm]ayo|" +
            "[Jj]unio|" +
            "[Jj]ulio|" +
            "[Aa]gosto|" +
            "[Ss]eptiembre|" +
            "[Ss]etiembre|" +
            "[Oo]ctubre|" +
            "[Nn]oviembre| " +
            "[Dd]iciembre|" +

            // Portuguese month names
            "[Jj]aneiro|" +
            "[Ff]evereiro|" +
            "[Mm]arço|" +
            "[Aa]bril|" +
            "[Mm]aio|" +
            "[Jj]unho|" +
            "[Jj]ulho|" +
            "[Aa]gosto|" +
            "[Ss]etembro|" +
            "[Oo]utubro|" +
            "[Nn]ovembro|" +
            "[Dd]ezembro|" +

            // French month names
            "[Jj]anvier|" +
            "[Ff]évrier|" +
            "[Mm]ars|" +
            "[Aa]vril|" +
            "[Mm]ai|" +
            "[Jj]uin|" +      // Can't distinguish between "juin" and "juillet" with three-character months
            "[Jj]uillet|" +
            "[Aa]out|" +
            "[Ss]eptembre|" +
            "[Oo]ctobre|" +
            "[Nn]ovembre|" +
            "[Dd]écembre|" +

            // German month names
            "[Jj]anuar|" +
            "[Ff]ebruar|" +
            "[Mm]ärz|" +
            "[Aa]pril|" +
            "[Mm]ai|" +
            "[Jj]uni|" +
            "[Jj]uli|" +
            "[Aa]ugust|" +
            "[Ss]eptember|" +
            "[Oo]ktober|" +
            "[Nn]ovember|" +
            "[Dd]ezember" +

            "))[ \\+]" +
        "(?:([12][0-9][0-9][0-9]))"
    );
}
