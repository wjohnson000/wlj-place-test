package std.wlj.access;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.PlaceBridge;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.PlaceSearchResults;
import org.familysearch.standards.place.data.SearchParameter;
import org.familysearch.standards.place.data.SearchParameters;


public class SearchHelper {
    public static PlaceBridge readPlace(PlaceDataServiceImpl dataService, int placeId) throws PlaceDataException {
        SearchParameters params = new SearchParameters();
        params.addParam(SearchParameter.PlaceParam.createParam(placeId));
        params.addParam(SearchParameter.NoCacheParam.createParam(true));
        params.addParam(SearchParameter.FilterDeleteParam.createParam(true));

        PlaceSearchResults results = dataService.search(params);
        if (results == null  ||  results.getReturnedCount() == 0) {
            return null;
        } else {
            return results.getResults().get(0).getAssociatedPlace();
        }
    }

    public static PlaceRepBridge readPlaceRep(PlaceDataServiceImpl dataService, int repId) throws PlaceDataException {
        SearchParameters params = new SearchParameters();
        params.addParam(SearchParameter.PlaceRepParam.createParam(repId));
        params.addParam(SearchParameter.NoCacheParam.createParam(true));
        params.addParam(SearchParameter.FilterDeleteParam.createParam(true));

        PlaceSearchResults results = dataService.search(params);
        if (results == null  ||  results.getReturnedCount() == 0) {
            return null;
        } else {
            return results.getResults().get(0);
        }
    }
}
