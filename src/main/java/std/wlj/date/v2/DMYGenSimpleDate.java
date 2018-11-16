/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.familysearch.standards.date.exception.GenDateException;
import org.familysearch.standards.date.model.GenSimpleDate;

/**
 * @author wjohnson000
 *
 */
public class DMYGenSimpleDate {

    private static final Map<String,Integer> monthNumber = new HashMap<>();
    static {
        monthNumber.put("jan",  1);
        monthNumber.put("feb",  2);
        monthNumber.put("mar",  3);
        monthNumber.put("apr",  4);
        monthNumber.put("may",  5);
        monthNumber.put("jun",  6);
        monthNumber.put("jul",  7);
        monthNumber.put("aug",  8);
        monthNumber.put("sep",  9);
        monthNumber.put("oct", 10);
        monthNumber.put("nov", 11);
        monthNumber.put("dec", 12);

        monthNumber.put("ene",  1);
        monthNumber.put("fev",  2);
        monthNumber.put("fév",  2);
        monthNumber.put("mär",  3);
        monthNumber.put("abr",  4);
        monthNumber.put("avr",  4);
        monthNumber.put("mai",  5);
        monthNumber.put("ago",  8);
        monthNumber.put("aou",  8);
        monthNumber.put("set",  9);
        monthNumber.put("out", 10);
        monthNumber.put("okt", 10);
        monthNumber.put("dic", 12);
        monthNumber.put("dez", 12);
        monthNumber.put("déc", 12);

    }

    public static final Pattern DMY_SIMPLE_DATE_PAT = Pattern.compile(
        "(?:([0-9][0-9]?))[ \\+]" +
        "(?:(" +
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

            "enero|" +
            "febrero|" +
            "marzo|" +
            "abril|" +
            "mayo|" +
            "junio|" +
            "julio|" +
            "agosto|" +
            "septiembre|" +
            "setiembre|" +
            "octubre|" +
            "noviembre| " +
            "diciembre|" +

            "janeiro|" +
            "fevereiro|" +
            "março|" +
            "abril|" +
            "maio|" +
            "junho|" +
            "julho|" +
            "agosto|" +
            "setembro|" +
            "outubro|" +
            "novembro|" +
            "dezembro|" +

            "[Jj]anvier|" +
            "[Ff]évrier|" +
            "[Mm]ars|" +
            "[Aa]vril|" +
            "[Mm]ai|" +
            "[Jj]uin|" +
            "[Jj]uillet|" +
            "[Aa]out|" +
            "[Ss]eptembre|" +
            "[Oo]ctobre|" +
            "[Nn]ovembre|" +
            "[Dd]écembre|" +

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

    public static boolean isValidDMY(String text) {
        return text != null  &&  DMY_SIMPLE_DATE_PAT.matcher(text.trim()).matches();
    }

    public static GenSimpleDate from(String simpleDMY) throws GenDateException {
        Matcher m = DMY_SIMPLE_DATE_PAT.matcher(simpleDMY.trim());

        if (m.matches()) {
            int day   = Integer.parseInt(m.group(1));
            int month = monthNumber.getOrDefault(m.group(2).substring(0, 3).toLowerCase(), 0);
            int year  = Integer.parseInt(m.group(15));
            
            return new GenSimpleDate(false, year, month, day, 0, 0, 0);
        } else {
            return null;
        }
    }
}
