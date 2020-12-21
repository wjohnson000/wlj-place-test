/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.services;

import java.util.List;

import org.familysearch.standards.place.data.PlaceNameBridge;

/**
 * @author wjohnson000
 *
 */
public class SVC_Variant_GetNames extends BaseAll {

    public static void main(String...args) {
        try {
            setupServices();

            System.out.println("\n\n=====================================================================");
            List<PlaceNameBridge> varNames = dataService.getVariantNames(333);
            varNames.forEach(name -> System.out.println(name.getNameId() + " . " + name.getName().get() + " . " + name.getName().getLocale()));
        } catch(Exception ex) {
            System.out.println("OOPS!! ... " + ex.getMessage());
        } finally {
            shutdownServices();
        }
    }
}
