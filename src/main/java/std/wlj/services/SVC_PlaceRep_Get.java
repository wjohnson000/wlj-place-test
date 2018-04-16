/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.services;

import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.PlaceSearchResults;
import org.familysearch.standards.place.data.SearchParameter;
import org.familysearch.standards.place.data.SearchParameters;

/**
 * @author wjohnson000
 *
 */
public class SVC_PlaceRep_Get extends BaseAll {

    public static void main(String...args) {
        try {
            setupServices();

            SearchParameters params = new SearchParameters();
            params.addParam(SearchParameter.PlaceRepParam.createParam(1001));
            params.addParam(SearchParameter.FilterDeleteParam.createParam(false));
            PlaceSearchResults results = dataService.search(params);

            System.out.println("\n\n=====================================================================");
            for (PlaceRepBridge repB : results.getResults()) {
                System.out.println("Rep: " + repB + " . " + repB.getRevision());
                repB.getAllDisplayNames().entrySet().forEach(entry -> System.out.println("  N: " + entry));
            }
            System.out.println("\n\n");
        } catch(Exception ex) {
            System.out.println("OOPS!! ... " + ex.getMessage());
        } finally {
            shutdownServices();
        }
    }
}
