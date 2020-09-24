/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.hhs.name.validate;

import org.familysearch.homelands.admin.parser.name.GeneanetLastNameParser;

/**
 * @author wjohnson000
 *
 */
public class ValidateImportGeneanetLast {
    
    static String DATA_FILE = "noms_fr.csv";

    public static void main(String... args) throws Exception {
        ValidateNameTask engine = new ValidateNameTask("MMMM-98P", "MMM9-DFC");
        engine.compareProdVsDev(new GeneanetLastNameParser(), DATA_FILE, "LAST", "fr", 350);
    }
}
