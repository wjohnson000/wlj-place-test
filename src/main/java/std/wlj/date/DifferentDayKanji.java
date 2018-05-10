/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

/**
 * @author wjohnson000
 *
 */
public class DifferentDayKanji {

    public static void main(String...args) {
        String one = "0998年11月27日";
        String two = "0083年11月23曰";

        one.chars().forEach(ch -> System.out.println(ch + " --> " + (int)ch + " --> " + Integer.toHexString((int)ch)));

        System.out.println();
        System.out.println();
        two.chars().forEach(ch -> System.out.println(ch + " --> " + (int)ch + " --> " + Integer.toHexString((int)ch)));
    }
}
