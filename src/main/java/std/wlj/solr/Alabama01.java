package std.wlj.solr;

import java.util.HashMap;
import java.util.Map;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.PlaceType;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.data.TypeImpl;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;

import std.wlj.util.SolrManager;


public class Alabama01 {

    public static void main(String... args) throws PlaceDataException {
        SolrService  solrService = SolrManager.localHttpService();
//        SolrService  solrService = SolrManager.localEmbeddedService("D:/solr/stand-alone-6.5.0");
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        PlaceRepDoc repDoc = new PlaceRepDoc();
        repDoc.setRepId(351);
        PlaceRepresentation alabama = new PlaceRepresentation(repDoc);

        Map<String,String> emptyMap = new HashMap<>();
        TypeImpl type186 = new TypeImpl(null, TypeBridge.TYPE.PLACE, 186, "TOWN", emptyMap, emptyMap, true);
        TypeImpl type376 = new TypeImpl(null, TypeBridge.TYPE.PLACE, 376, "CITY", emptyMap, emptyMap, true);

        PlaceRequestBuilder builder = new PlaceRequestBuilder();
        builder.setShouldCollectMetrics(true);
        builder.addRequiredParent(alabama);
        builder.addRequiredPlaceType(new PlaceType(type186));
        builder.addRequiredPlaceType(new PlaceType(type376));

        PlaceResults results = placeService.requestPlaces(builder.getRequest());
        System.out.println("Count: " + results.getReturnedCount());
        for (PlaceRepresentation resultModel : results.getPlaceRepresentations()) {
            System.out.println("    -- " + resultModel.getFullDisplayName(StdLocale.ENGLISH).get() + "  [" + resultModel.getType().getCode() + "]");
        }

        placeService.shutdown();

//        System.exit(0);
    }
}
