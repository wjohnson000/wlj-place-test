/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

/**
 * @author wjohnson000
 *
 */
public class ZzzZzz {

    public static void main(String...args) {
        String what = "六 六";
        char[] chars = what.toCharArray();
        for (int ndx=0;  ndx<chars.length;  ndx++) {
            System.out.println(chars[ndx] + " --> " + (int)chars[ndx] + " --> " + Integer.toHexString((int)chars[ndx]));
        }
    }
}
