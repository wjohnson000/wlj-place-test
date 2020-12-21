/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.v2;

import org.familysearch.standards.date.common.ImperialDictionary;
import org.familysearch.standards.date.common.ImperialDictionary.Reign;

/**
 * @author wjohnson000
 *
 */
public class ImperialDictionaryReign {

    public static void main(String...args) {
        Reign reign = ImperialDictionary.lookupReign("zhou3-1");
        System.out.println(reign);
    }
}
