/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.util.*;

/**
 * @author wjohnson000
 *
 */
public class SplitVsIndex {

    static List<String> stuff = new ArrayList<>();

    public static void main(String...args) {
        for (int i=0;  i<400_000;  i++) {
            stuff.add("some.long.package.name" + i);
        }
        for (int i=0;  i<400_000;  i++) {
            stuff.add("short.package" + i);
        }
        for (int i=0;  i<400_000;  i++) {
            stuff.add("class" + i);
        }

        doSplit();
        doIndex();
        doSplit();
        doIndex();
        doSplit();
        doIndex();
        doSplit();
        doIndex();
        doSplit();
        doIndex();
    }

    static void doSplit() {
        long time0 = System.nanoTime();
        for (String stuf : stuff) {
            String[] packages = stuf.split("\\.");
            String clazz = packages[packages.length-1];
            if (clazz.isEmpty()) {
                System.out.println("CC1: " + clazz);
            }
        }
        long time1 = System.nanoTime();
        System.out.println("SPLIT: " + (time1 - time0) / 1_000_000.0);
    }

    static void doIndex() {
        long time0 = System.nanoTime();
        for (String stuf : stuff) {
            int ndx = stuf.indexOf('.');
            String clazz = (ndx < 0) ? stuf : stuf.substring(ndx+1);
            if (clazz.isEmpty()) {
                System.out.println("CC2: " + clazz);
            }
        }
        long time1 = System.nanoTime();
        System.out.println("INDEX: " + (time1 - time0) / 1_000_000.0);
    }
}
