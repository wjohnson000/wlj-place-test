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
public class DB_GetVariantNames extends BaseAll {

    public static void main(String...args) {
        try {
            setupServices();
            List<PlaceNameBridge> varNames = dbService.readService.getVariantNames(333);
            varNames.forEach(name -> System.out.println(name.getNameId() + " . " + name.getName().get() + " . " + name.getName().getLocale()));
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            shutdownServices();
        }

        System.exit(0);
    }
}
