/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.util.Iterator;

/**
 * @author wjohnson000
 *
 */
public class CircularListXTest {
    public static void main(String... args) {
        CircularListX<String> cList = new CircularListX<String>(5);
        cList.add("one");
        iterateMe(cList);
        cList.add("two");
        iterateMe(cList);
        cList.add("three");
        iterateMe(cList);
        cList.add("four");
        iterateMe(cList);
        cList.add("five");
        iterateMe(cList);
        cList.add("six");
        iterateMe(cList);
        cList.add("seven");
        iterateMe(cList);
        cList.add("eight");
        iterateMe(cList);
        cList.add("nine");
        iterateMe(cList);
    }

    /**
     * @param cList
     */
    static void iterateMe(CircularListX<String> cList) {
        System.out.println("\nContents");
        Iterator<String> iter = cList.iterator();
        while(iter.hasNext()) {
            System.out.println("  " + iter.next());
        }
    }
}
