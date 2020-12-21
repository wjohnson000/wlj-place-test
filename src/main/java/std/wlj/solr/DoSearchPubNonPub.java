package std.wlj.solr;

import java.util.Arrays;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequest.PublishedType;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;

import std.wlj.util.SolrManager;

public class DoSearchPubNonPub {

    public static void main(String... args) throws Exception {
        SolrService  solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.1.0");
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        String text = "Provo, Utah, , US";
        for (int i=0;  i<3;  i++) {
            for (PublishedType pubType : Arrays.asList(null, PublishedType.PUB_AND_NON_PUB, PublishedType.PUB_ONLY, PublishedType.NON_PUB_ONLY)) {
                System.out.println("\n\n===================================================================");
                System.out.println("Text: " + text);
                PlaceRequestBuilder builder = placeService.createRequestBuilder(text, StdLocale.ENGLISH);
                builder.setPublishedType(pubType);
                builder.setShouldCollectMetrics(false);
                builder.setFilterResults(false);
                
                long time0 = System.nanoTime();
                PlaceRequest request = builder.getRequest();
                PlaceResults results = placeService.requestPlaces(request);
                long time1 = System.nanoTime();
                System.out.println("RES.found: " + results.getFoundCount());
                System.out.println("    retnd: " + results.getReturnedCount());
                System.out.println("     time: " + (time1-time0)/1_000_000.0);
                
                PlaceRepresentation[] placeReps = results.getPlaceRepresentations();
                for (PlaceRepresentation placeRep : placeReps) {
                    System.out.println("REP.repId: " + placeRep.getId());
                    System.out.println("    fname: " + placeRep.getFullDisplayName(StdLocale.ENGLISH).get());
                    System.out.println("    chain: " + Arrays.toString(placeRep.getJurisdictionChainIds()));
                    System.out.println("    scRAW: " + placeRep.getMetadata().getScoring().getRawScore());
                    System.out.println("    scREL: " + placeRep.getMetadata().getScoring().getRelevanceScore());
                }
            }
        }

        solrService.shutdown();
        System.exit(0);
    }
}
