/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

/**
 * @author wjohnson000
 *
 */
public class FullWidthNumbers {

    public static void main(String...args) {
        char wee = '!';
        int  big = 0xFF01;

        for (int ndx=0;  ndx<94;  ndx++, wee++, big++) {
            System.out.println((int)wee + "|" + wee + "|" + big + "|" + (char)big + "|" + Integer.toHexString(big).toUpperCase() + "|" + (big-wee) + "|" + (Integer.toHexString(big-wee)));
        }
    }
}
