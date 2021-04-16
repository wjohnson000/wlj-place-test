package std.wlj.util;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest.FuzzyType;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;


public class TestPouillon {
    public static void main(String... args) throws PlaceDataException {
        SolrService solrService = SolrManager.localEmbeddedService();
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        System.out.println("Place-Type count: " + solrService.getTypes(TypeBridge.TYPE.PLACE).size());
        System.out.println("Place-Name count: " + solrService.getTypes(TypeBridge.TYPE.NAME).size());
//        System.out.println("Name-Priority: " + NamePriorityHelper.getInstance());

        System.out.println("--------------------------------------------------------------------------------------");

        PlaceRequestBuilder builder;
        builder = placeService.createRequestBuilder("Pouillon", StdLocale.ENGLISH);
        builder.setShouldCollectMetrics(true);
        builder.setFuzzyType(FuzzyType.EDIT_DISTANCE);

        PlaceResults results = placeService.requestPlaces(builder.getRequest());

        for (PlaceRepresentation placeRep : results.getPlaceRepresentations()) {
            System.out.println("Place-Rep: " + placeRep);
        }

        System.exit(0);
    }
}
