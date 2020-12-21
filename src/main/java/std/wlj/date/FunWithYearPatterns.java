/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wjohnson000
 *
 */
public class FunWithYearPatterns {

//    private static final Pattern apostrophe   = Pattern.compile("(?:([0-9]{4}))[\\']*[sS]");
//    private static final Pattern twoDigitYr   = Pattern.compile("(?:([0-9]{2}))[\\?\\_\\-\\*]{2}");
//    private static final Pattern threeDigitYr = Pattern.compile("(?:([0-9]{3}))[\\?\\_\\-\\*]");
//    private static final Pattern fourDigitYr = Pattern.compile("(?:([0-9]{4}))");

    private static final Pattern apostrophe    = Pattern.compile("([0-9]{4})[\\']*[sS]");
    private static final Pattern twoDigitYr    = Pattern.compile("([0-9]{2})[\\?\\_\\*]{2}");
    private static final Pattern threeDigitYr  = Pattern.compile("([0-9]{3})[\\?\\_\\*]");

    private static final Pattern twoDigitYrD   = Pattern.compile("^([0-9]{2})[\\-]{2}");
    private static final Pattern threeDigitYrD = Pattern.compile("^([0-9]{3})[\\-]");

    private static String[] years = {
        "19??",
        "19--",
        "19__",
        "19**",

        "199?",
        "199-",
        "199_",
        "199*",

        "1900's",
        "1900s",
        "1980's",
        "1980s",
        "1977's",
        "1977s",

        "2000",
        "20##",

        "19??-1938",
        "1833-184?",
        "19?? - 1938",
        "1833 - 184?",
        "19-- to 1938",
        "1833 to 184?",
        "1830's to 184?",
        "1900-190?"
    };

    public static void main(String... args) {
        for (String year : years) {
            System.out.println("STR: " + year);
            Matcher matcher = yearWithWildcard(year);
            if (matcher == null) {
                matcher = yearWithApostrophe(year);
            }
            if (matcher != null) {
                System.out.println("     " + getPartialYear(matcher));
            } else {
                findSubParts(year);
            }
        }
    }

    static Matcher yearWithWildcard(String yyyy) {
        Matcher matcher = twoDigitYr.matcher(yyyy);
        if (! matcher.matches()) {
            matcher = threeDigitYr.matcher(yyyy);
        }
        return (matcher.matches()) ? matcher : null;
    }

    static Matcher yearWithApostrophe(String yyyy) {
        Matcher matcher = apostrophe.matcher(yyyy);
        return (matcher.matches()) ? matcher : null;
    }

    static int getPartialYear(Matcher matcher) {
        try {
            return Integer.parseInt(matcher.group(1));
        } catch(NumberFormatException ex) {
            return -1;
        }
    }

    static void findSubParts(String yyyy) {
        Matcher matcher01 = apostrophe.matcher(yyyy);
        Matcher matcher02 = twoDigitYr.matcher(yyyy);
        Matcher matcher03 = threeDigitYr.matcher(yyyy);
        Matcher matcher04 = threeDigitYrD.matcher(yyyy);
        Matcher matcher05 = twoDigitYrD.matcher(yyyy);
   
        whatever(matcher01);
        whatever(matcher02);
        whatever(matcher03);
        whatever(matcher04);
        whatever(matcher05);
    }

    static void whatever(Matcher matcher) {
        while (matcher.find()) {
            int sss = matcher.start();
            int eee = matcher.end();
            System.out.println("   " + matcher + " --> " + sss + " to " + eee + " --> " + matcher.group());
        }
    }
    
}
