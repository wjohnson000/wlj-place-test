/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.services;

import org.familysearch.standards.place.data.PlaceNameBridge;

/**
 * @author wjohnson000
 *
 */
public class SVC_Variant_Add_OK extends BaseAll {

    public static void main(String...args) {
        try {
            setupServices();

            System.out.println("\n\n=====================================================================");
            PlaceNameBridge varName = dataService.createVariantName(333, 434, "my name", "en", "wjohnson000");
            System.out.println(varName.getNameId() + " . " + varName.getName().get() + " . " + varName.getName().getLocale() + " . " + varName.getRevision());
        } catch(Exception ex) {
            System.out.println("OOPS!! ... " + ex.getMessage());
        } finally {
            shutdownServices();
        }
    }
}
