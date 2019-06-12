/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

/**
 * @author wjohnson000
 *
 */
public class TestUnicodeRange {

    public static void main(String...args) {
        char ch00 = '\u1000';
        char ch99 = '\u109F';

        for (char ch=ch00;  ch<ch99;  ch++) {
            if (Character.isDefined(ch)  &&  ! Character.isAlphabetic(ch)) {
                System.out.println(ch + "|" + (int)ch + "|" + Integer.toHexString(ch) + "|" + Character.getName(ch));
            }
        }
    }
}
