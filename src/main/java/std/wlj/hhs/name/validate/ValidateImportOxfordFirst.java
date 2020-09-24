/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name.validate;

import org.familysearch.homelands.admin.parser.name.OxfordFirstNameEnParser;

/**
 * @author wjohnson000
 *
 */
public class ValidateImportOxfordFirst {

    static String DATA_FILE = "first_acref_9780198610601.xml";

    public static void main(String...args) throws Exception {
        ValidateNameTask engine = new ValidateNameTask("MMMM-98G", "MMM9-X78");
        engine.compareProdVsDev(new OxfordFirstNameEnParser(), DATA_FILE, "FIRST", "en", 150);
    }
}
