/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name.validate;

import org.familysearch.homelands.admin.parser.name.OxfordLastNameEnParser;

/**
 * @author wjohnson000
 *
 */
public class ValidateImportOxfordLast {

    static String DATA_FILE = "last_acref_9780195081374.xml";

    public static void main(String...args) throws Exception {
        ValidateNameTask engine = new ValidateNameTask("MMMM-98L", "MMM9-FRZ");
        engine.compareProdVsDev(new OxfordLastNameEnParser(), DATA_FILE, "LAST", "en", 450);
    }
}
