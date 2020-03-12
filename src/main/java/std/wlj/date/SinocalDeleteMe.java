/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

/**
 * @author wjohnson000
 *
 */
public class SinocalDeleteMe {

    public static void main(String...args) {
        String base = "元,皇子拖雷";
        for (int yr=1;  yr<12;  yr++) {
            for (int mo=1;  mo<13;  mo++) {
                for (int dy=1;  dy<30;  dy++) {
                    System.out.println(base + "," + yr + "," + mo + "," + dy);
                }
            }
        }
    }
}
