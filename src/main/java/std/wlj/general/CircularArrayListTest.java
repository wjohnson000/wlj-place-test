/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.util.Iterator;

/**
 * @author wjohnson000
 *
 */
public class CircularArrayListTest {
    public static void main(String... args) {

        CircularArrayList<String> cList = new CircularArrayList<String>(5);

        iterateMe(cList);
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
    }

    /**
     * @param cList
     */
    static void iterateMe(CircularArrayList<String> cList) {
        System.out.println("\nContents");
        Iterator<String> iter = cList.iterator();
        while(iter.hasNext()) {
            System.out.println("  " + iter.next());
        }
    }
}
