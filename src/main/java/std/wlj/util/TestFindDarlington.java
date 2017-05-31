package std.wlj.util;

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


public class TestFindDarlington {
    public static void main(String... args) throws PlaceDataException {
        SolrService solrService = SolrManager.localEmbeddedService();  //.awsService55();

        System.out.println("Place-Type count: " + solrService.getTypes(TypeBridge.TYPE.PLACE, false).size());
        System.out.println("Place-Name count: " + solrService.getTypes(TypeBridge.TYPE.NAME, false).size());
//        System.out.println("Name-Priority: " + NamePriorityHelper.getInstance());

        for (int i=0;  i<6;  i++) {
            System.out.println("--------------------------------------------------------------------------------------");
            PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
            PlaceService placeService = new PlaceService(profile);


            PlaceRequestBuilder builder;
            if (i == 0  ||  i == 2 ||  i == 4) {
                builder = placeService.createRequestBuilder("Utah, USA", StdLocale.ENGLISH);
            } else {
                builder = placeService.createRequestBuilder("Darlington, South Carolina", StdLocale.ENGLISH);
            }
            builder.setShouldCollectMetrics(true);

            PlaceResults results = placeService.requestPlaces(builder.getRequest());
//            RequestMetrics metrics = results.getMetrics();
//            System.out.println("Metrics.TotalTime: " + metrics.getTotalTime());
//            System.out.println("Metrics.Assembly: " + metrics.getAssemblyTime());
//            System.out.println("Metrics.FinalParsedInputTextCount: " + metrics.getFinalParsedInputTextCount());
//            System.out.println("Metrics.IdentifyCandidateLookupTime: " + metrics.getIdentifyCandidateLookupTime());
//            System.out.println("Metrics.IdentifyCandidateMaxHitFilterTime: " + metrics.getIdentifyCandidateMaxHitFilterTime());
//            System.out.println("Metrics.IdentifyCandidatesTime: " + metrics.getIdentifyCandidatesTime());
//            System.out.println("Metrics.IdentifyCandidateTailMatchTime: " + metrics.getIdentifyCandidateTailMatchTime());
//            System.out.println("Metrics.InitialParsedInputTextCount: " + metrics.getInitialParsedInputTextCount());
//            System.out.println("Metrics.ParseTime: " + metrics.getParseTime());
//            System.out.println("Metrics.PreScoringCandidateCount: " + metrics.getPreScoringCandidateCount());
//            System.out.println("Metrics.RawCandidateCount: " + metrics.getRawCandidateCount());
//            System.out.println("Metrics.ThresholdScore: " + metrics.getThresholdScore());
//            System.out.println("Metrics.TokenSetCount: " + metrics.getTokenSetCount());
//            for (Scorer scorer : metrics.getTimedScorers()) {
//                System.out.println("Scorer." + scorer.getClass().getName() + ": " + metrics.getScorerTime(scorer));
//            }

            for (PlaceRepresentation placeRep : results.getPlaceRepresentations()) {
                System.out.println("Place-Rep: " + placeRep);
            }

            try { Thread.sleep(2500); } catch(Exception ex) { }
        }

        
        System.exit(0);
    }
}
