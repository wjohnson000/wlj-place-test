package std.wlj.solr;

import java.util.Arrays;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;

import std.wlj.util.SolrManager;

public class DoSearchEasy {

    /** Sample data for interpretation ... */
    private static String[] textes = {
        "Turtle Creek, Allegheny, Pennsylvania, United States",
        "Provo, Utah, Utah, US",
        "Altona, Ottensen, Hamburg-Altona, Hamburg, Germany",
        "Washington, Vermont",
        "Angu, Scotland",
    };

    public static void main(String... args) throws Exception {
        SolrService  solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.1.0");
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        for (String text : textes) {
            System.out.println("===================================================================\n\n");
            System.out.println("Text: " + text);
            PlaceRequestBuilder builder = placeService.createRequestBuilder(text, StdLocale.ENGLISH);
            builder.setShouldCollectMetrics(true);
            builder.setFilterResults(false);
            
            PlaceRequest request = builder.getRequest();
            PlaceResults results = placeService.requestPlaces(request);
            System.out.println("RES.found: " + results.getFoundCount());
            System.out.println("    retnd: " + results.getReturnedCount());
            
            PlaceRepresentation[] placeReps = results.getPlaceRepresentations();
            for (PlaceRepresentation placeRep : placeReps) {
                System.out.println("REP.repId: " + placeRep.getId());
                System.out.println("    fname: " + placeRep.getFullDisplayName(StdLocale.ENGLISH).get());
                System.out.println("    chain: " + Arrays.toString(placeRep.getJurisdictionChainIds()));
                System.out.println("    scRAW: " + placeRep.getMetadata().getScoring().getRawScore());
                System.out.println("    scREL: " + placeRep.getMetadata().getScoring().getRelevanceScore());
            }
        }

        solrService.shutdown();
        System.exit(0);
    }
}
