package std.wlj.solr;

import java.util.Arrays;

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

public class SearchMasterByGeo {

    public static void main(String... args) throws PlaceDataException {
        SolrService  solrService = SolrManager.awsBetaService(false);
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        PlaceRequestBuilder builder = new PlaceRequestBuilder();
        builder.setResultsLimit(1250);
        builder.setCentroid(-86.5264, 39.1653);
        builder.setDistanceInKM(17.2);
        PlaceResults results = placeService.requestPlaces(builder.getRequest());

        System.out.println("Count: " + results.getReturnedCount() + " --> " + results.getFoundCount());
        for (PlaceRepresentation repModel : results.getPlaceRepresentations()) {
            System.out.println();
            System.out.println("RepID: " + repModel.getId() + "");
            System.out.println(" Name: " + repModel.getFullDisplayName(StdLocale.ENGLISH).get());
            System.out.println(" Jurs: " + Arrays.toString(repModel.getJurisdictionChainIds()));
            System.out.println("  L/L: " + repModel.getCentroidLatitude() + "," + repModel.getCentroidLongitude());
        }

        solrService.shutdown();
        System.exit(0);
    }
}
