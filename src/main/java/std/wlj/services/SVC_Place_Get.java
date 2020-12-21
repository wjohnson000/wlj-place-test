/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.services;

import org.familysearch.standards.place.data.PlaceBridge;
import org.familysearch.standards.place.data.PlaceNameBridge;
import org.familysearch.standards.place.data.PlaceSearchResults;
import org.familysearch.standards.place.data.SearchParameter;
import org.familysearch.standards.place.data.SearchParameters;

/**
 * @author wjohnson000
 *
 */
public class SVC_Place_Get extends BaseAll {

    public static void main(String...args) {
        try {
            setupServices();

            SearchParameters params = new SearchParameters();
            params.addParam(SearchParameter.PlaceParam.createParam(333));
            params.addParam(SearchParameter.FilterDeleteParam.createParam(false));
            PlaceSearchResults results = dataService.search(params);

            if (! results.getResults().isEmpty()) {
                PlaceBridge placeB = results.getResults().get(0).getAssociatedPlace();
                System.out.println("\n\n=====================================================================");
                System.out.println("Place: " + placeB + " . " + placeB.getPlaceRevision());
                for (PlaceNameBridge nameB : placeB.getAllVariantNames()) {
                    System.out.println("   N: " + nameB.getNameId() + " . " + nameB.getName().get() + " . " + nameB.getName().getLocale() + " . " + nameB.getType().getCode() + " . " + nameB.getRevision());
                }
            }
        } catch(Exception ex) {
            System.out.println("OOPS!! ... " + ex.getMessage());
        } finally {
            shutdownServices();
        }
    }
}
