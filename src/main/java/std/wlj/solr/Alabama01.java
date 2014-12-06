package std.wlj.solr;

import java.util.HashMap;
import java.util.Map;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.PlaceType;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.data.TypeImpl;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;


public class Alabama01 {

    public static void main(String... args) throws PlaceDataException {
        String solrHome = "http://localhost:8983/solr/places";
//        String solrHome = "http://familysearch.org/int-solr/places";

        System.setProperty("solr.solr.home", solrHome);
        System.setProperty("solr.master.url", solrHome);
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");

        SolrService  solrService = new SolrService();
        PlaceService placeService = new PlaceService(new DefaultPlaceRequestProfile(solrService));

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

        System.exit(0);
    }
}
