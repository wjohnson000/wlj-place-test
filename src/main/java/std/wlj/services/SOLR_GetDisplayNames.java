/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.services;

import java.util.List;

import org.familysearch.standards.place.data.DisplayNameBridge;

/**
 * @author wjohnson000
 *
 */
public class SOLR_GetDisplayNames extends BaseAll {

    public static void main(String...args) {
        try {
            setupServices();

            List<DisplayNameBridge> varNames = solrService.getDisplayNames(1001);
            varNames.forEach(name -> System.out.println(name.getName().get() + " . " + name.getName().getLocale()));
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            shutdownServices();
        }

        System.exit(0);
    }
}
