/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.services;

import org.familysearch.standards.place.data.DisplayNameBridge;

/**
 * @author wjohnson000
 *
 */
public class SVC_Display_Update_Invalid extends BaseAll {

    public static void main(String...args) {
        try {
            setupServices();

            // No display name w/ locale of "pt" exists
            DisplayNameBridge dispName = dataService.updateDisplayName(1001, "HiHi-new", "pt", "wjohnson000");
            System.out.println(dispName.getName().get() + " . " + dispName.getName().getLocale() + " . " + dispName.getRevision());
        } catch(Exception ex) {
            System.out.println("OOPS!! ... " + ex.getMessage());
        } finally {
            shutdownServices();
        }
    }
}
