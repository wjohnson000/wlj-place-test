package std.wlj.solr;

import java.util.Map;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;


public class JiraStd2683 {

    public static void main(String... args) throws PlaceDataException {
//        String solrHome = "http://localhost:8983/solr/places";
        String solrHome = "http://familysearch.org/int-solr/places";

        System.setProperty("solr.solr.home", solrHome);
        System.setProperty("solr.master.url", solrHome);
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");

        SolrService  solrService = new SolrService();
        PlaceService placeService = new PlaceService(new DefaultPlaceRequestProfile(solrService));

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
