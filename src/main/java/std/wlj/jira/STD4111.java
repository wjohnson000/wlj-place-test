package std.wlj.jira;

import java.util.Arrays;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;

import std.wlj.util.SolrManager;


public class STD4111 {
    private static final double ONE_MILLION = 1000000.0;

    private static final String[] textes = {
        "Danbury, Connecticut",
        "Danbury, Danbury, Fairfield, Connecticut, British Colonial America"
    };

    public static void main(String... args) throws PlaceDataException {
//        SolrService solrService = SolrManager.localEmbeddedService();
        SolrService solrService = SolrManager.awsProdService(false);
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        System.out.println("Place-Type count: " + solrService.getTypes(TypeBridge.TYPE.PLACE, false).size());
        System.out.println("Name-Type count: " + solrService.getTypes(TypeBridge.TYPE.NAME, false).size());

        for (String text : textes) {
            PlaceRequestBuilder builder = placeService.createRequestBuilder(text, StdLocale.ENGLISH);

            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println(">>> Search for: " + text);
            long then = System.nanoTime();
            PlaceResults results = placeService.requestPlaces(builder.getRequest());
            long nnow = System.nanoTime();

            StringBuilder buff = new StringBuilder();
            buff.append(text);
            buff.append("|time=").append((nnow - then) / ONE_MILLION);
            buff.append("|cnt=").append(results.getReturnedCount());

            for (PlaceRepresentation resultModel : results.getPlaceRepresentations()) {
                System.out.println("    -- " + resultModel.getFullDisplayName(StdLocale.ENGLISH).get());
                System.out.println("       " + Arrays.toString(resultModel.getJurisdictionChainIds()));
            }
            System.out.println(buff.toString());
        }
        System.exit(0);
    }
}
