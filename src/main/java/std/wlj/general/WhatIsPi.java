/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.util.Random;

/**
 * @author wjohnson000
 *
 */
public class WhatIsPi {

    public static void main(String...args) {
        Random ran = new Random();

        int    match = 0;
        int    tries = 500_000_000;

        for (int i=0;  i<tries;  i++) {
            double x = ran.nextDouble();
            double y = ran.nextDouble();
            if (x*x + y*y <= 1.0) {
                match++;
            }
        }

        System.out.println("Tries: " + tries);
        System.out.println("Matches: " + match);
        System.out.println(4.0 * match / tries);

        System.exit(0);
    }
}
