/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import org.familysearch.standards.date.parser.GenDateToken;

/**
 * @author wjohnson000
 *
 */
public class TestGenDateToken {

    public static void main(String...args) {
        System.out.println(GenDateToken.Type.find(null));
        System.out.println(GenDateToken.Type.find("abc"));
        System.out.println(GenDateToken.Type.find("PARTICLE"));
        System.out.println(GenDateToken.Type.find("particle"));

        System.out.println(GenDateToken.Type.findByValue(-1));
        System.out.println(GenDateToken.Type.findByValue(1));
        System.out.println(GenDateToken.Type.findByValue(11));
        System.out.println(GenDateToken.Type.findByValue(111));
        System.out.println(GenDateToken.Type.findByValue(1111));
    }
}
