/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

/**
 * @author wjohnson000
 *
 */
public class GenerateFatNumbers {

    public static void main(String...args) {
        String what = "〇";
        System.out.println((int)what.charAt(0) + " .. " + Integer.toHexString((int)what.charAt(0)));

        int zero = 0x3007;
        for (int ndx=0;  ndx<10;  ndx++) {
            System.out.println(ndx + " --> " + (zero+ndx) + " .. " + Integer.toHexString(zero+ndx) + "  >>" + (char)(zero+ndx) + "<<");
        }

        zero = 0xFF10;
        for (int ndx=0;  ndx<10;  ndx++) {
            System.out.println(ndx + " --> " + (zero+ndx) + " .. " + Integer.toHexString(zero+ndx) + "  >>" + (char)(zero+ndx) + "<<");
        }
    }
}
