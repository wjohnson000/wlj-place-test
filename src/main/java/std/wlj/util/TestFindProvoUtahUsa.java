package std.wlj.util;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;

public class TestFindProvoUtahUsa {
    public static void main(String... args) throws PlaceDataException {
//      SolrService solrService = SolrManager.localEmbeddedService();
      SolrService solrService = SolrManager.awsProdService(true);
      PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
      PlaceService placeService = new PlaceService(profile);

      PlaceRequestBuilder builder = placeService.createRequestBuilder("Kohanaiki, Hawaii, Hawaii Territory, United States", StdLocale.ENGLISH);
      builder.setShouldCollectMetrics(true);
      PlaceRequest request = builder.getRequest();

      PlaceResults results = placeService.requestPlaces(request);
      for (PlaceRepresentation placeRep : results.getPlaceRepresentations()) {
          System.out.println("Place-Rep: " + placeRep);
      }

      System.exit(0);
    }
}
