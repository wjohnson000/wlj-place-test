/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.general;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author wjohnson000
 *
 */
public class CircularListTest {
    public static void main(String... args) {
        CircularList<String> cList = new CircularList<String>(5);
        System.out.println(cList.add("one"));
        System.out.println(cList.getHighestIndexSoFar());
        System.out.println("\n" + cList.add("two"));
        System.out.println(cList.getHighestIndexSoFar());
        System.out.println("\n" + cList.add("three"));
        System.out.println(cList.getHighestIndexSoFar());
        System.out.println("\n" + cList.add("four"));
        System.out.println(cList.getHighestIndexSoFar());
        System.out.println("\n" + cList.add("five"));
        System.out.println(cList.getHighestIndexSoFar());
        System.out.println("\n" + cList.add("six"));
        System.out.println(cList.getHighestIndexSoFar());
        System.out.println("\n" + cList.add("seven"));
        System.out.println(cList.getHighestIndexSoFar());
        System.out.println("\n" + cList.add("eight"));
        System.out.println(cList.getHighestIndexSoFar());
        
//        iterateMe(cList);
    }

    /**
     * @param cList
     */
    static void iterateMe(CircularList<String> cList) {
        System.out.println("\nContents");
        Iterator<String> iter = cList.iterator();
        while(iter.hasNext()) {
            System.out.println("  " + iter.next());
        }
    }
}
