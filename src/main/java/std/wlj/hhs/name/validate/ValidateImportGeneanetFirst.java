/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name.validate;

import org.familysearch.homelands.admin.parser.name.GeneanetFirstNameParser;

/**
 * @author wjohnson000
 *
 */
public class ValidateImportGeneanetFirst {

    static String DATA_FILE = "signification_geneanet.csv";

    public static void main(String...args) throws Exception {
        ValidateNameTask engine = new ValidateNameTask("MMMM-98P", "MMM9-DFC");
        engine.compareProdVsDev(new GeneanetFirstNameParser(), DATA_FILE, "FIRST", "fr", 150);
    }
}
