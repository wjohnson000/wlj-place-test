package std.wlj.general;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexPunctuationOnly {

    static String[] testStr = {
        "--------",
        "..",
        ".,.,.,#$%^",
        ".,.,.,#$%^A",
        "",
        " ",
    };

    public static void main(String... args) {
        Pattern pattern = Pattern.compile("^[\\p{Punct}]{3,}$");
        for (String test : testStr) {
            Matcher matcher = pattern.matcher(test);
            System.out.println("Match: " + test + " --> " + matcher.matches());
        }
    }
}
