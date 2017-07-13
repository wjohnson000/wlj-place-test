package std.wlj.general;

import java.io.IOException;

public class TestIsCharacterTemp {
    public static void main(String...args) throws IOException {
        int ichar0 = 0x0980;
        int icharX = 0x09FF;
        for (int ichar=ichar0;  ichar<=icharX;  ichar++) {
            String format = "U+000%s|%d|%s|%s|%s|%s";
            if (ichar < '\u0100') {
                format = "U+00%s|%d|%s|%s|%s|%s";
            } else if (ichar < '\u1000') {
                format = "U+0%s|%d|%s|%s|%s|%s";
            } else {
                format = "U+0%s|%d|%s|%s|%s|%s";
            }

            char[] chars = Character.toChars(ichar);
            String whatever = String.format(format, Integer.toHexString(ichar), ichar, String.valueOf(chars), Character.getName(ichar), String.valueOf(Character.isLetter(ichar)), String.valueOf(Character.isAlphabetic(ichar)));
            System.out.println(whatever);
        }
    }
}
