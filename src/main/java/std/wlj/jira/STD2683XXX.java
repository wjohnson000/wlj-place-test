package std.wlj.jira;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;

import std.wlj.util.SolrManager;


public class STD2683XXX {

    public static void main(String... args) throws PlaceDataException {
        SolrService  solrService = SolrManager.awsProdService(true);

        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        PlaceRequestBuilder builder = new PlaceRequestBuilder();
        builder.setText("Dolinivka");
        builder.setUseCache(false);

        PlaceResults results = placeService.requestPlaces(builder.getRequest());
        System.out.println("Count: " + results.getReturnedCount());
        for (PlaceRepresentation resultModel : results.getPlaceRepresentations()) {
            System.out.println("    -- " + resultModel.getId() + " . " + resultModel.getPlaceId() + " --> " + resultModel.getRevision());
            System.out.println("    -- " + resultModel.getFullDisplayName(StdLocale.ENGLISH).get() + "  [" + resultModel.getType().getCode() + "]");
        }

        System.exit(0);
    }
}
