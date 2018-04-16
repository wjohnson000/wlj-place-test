/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.services;

/**
 * @author wjohnson000
 *
 */
public class SVC_Display_Delete extends BaseAll {

    public static void main(String...args) {
        try {
            setupServices();

            int revision = dataService.deleteDisplayName(1001, "es", "wjohnson000");
            System.out.println("New.revision: " + revision);
        } catch(Exception ex) {
            System.out.println("OOPS!! ... " + ex.getMessage());
        } finally {
            shutdownServices();
        }
    }
}
