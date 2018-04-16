/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.services;

import org.familysearch.standards.place.data.DisplayNameBridge;

/**
 * @author wjohnson000
 *
 */
public class SVC_Display_Add_OK extends BaseAll {

    public static void main(String...args) {
        try {
            setupServices();

            DisplayNameBridge dispName = dataService.createDisplayName(1001, "HiHi", "es", "wjohnson000");
            System.out.println(dispName.getName().get() + " . " + dispName.getName().getLocale() + " . " + dispName.getRevision());
        } catch(Exception ex) {
            System.out.println("OOPS!! ... " + ex.getMessage());
        } finally {
            shutdownServices();
        }
    }
}
