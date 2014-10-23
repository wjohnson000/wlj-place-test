package std.wlj.util;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest.FuzzyType;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.scoring.Scorer;
import org.familysearch.standards.place.search.RequestMetrics;
import org.familysearch.standards.place.util.NamePriorityHelper;


public class TestPouillon {
    public static void main(String... args) throws PlaceDataException {
        SolrService solrService = SolrManager.getLocalTokoro();
        PlaceService placeService = new PlaceService(solrService);

        System.out.println("Place-Type count: " + solrService.getTypes(TypeBridge.TYPE.PLACE).size());
        System.out.println("Place-Name count: " + solrService.getTypes(TypeBridge.TYPE.NAME).size());
        System.out.println("Name-Priority: " + NamePriorityHelper.getInstance());

        System.out.println("--------------------------------------------------------------------------------------");

        PlaceRequestBuilder builder;
        builder = placeService.createRequestBuilder("Pouillon", StdLocale.ENGLISH);
        builder.setShouldCollectMetrics(true);
        builder.setFuzzyType(FuzzyType.EDIT_DISTANCE);

        PlaceResults results = placeService.requestPlaces(builder.getRequest());
        RequestMetrics metrics = results.getMetrics();
        System.out.println("Metrics.TotalTime: " + metrics.getTotalTime());
        System.out.println("Metrics.Assembly: " + metrics.getAssemblyTime());
        System.out.println("Metrics.FinalParsedInputTextCount: " + metrics.getFinalParsedInputTextCount());
        System.out.println("Metrics.IdentifyCandidateLookupTime: " + metrics.getIdentifyCandidateLookupTime());
        System.out.println("Metrics.IdentifyCandidateMaxHitFilterTime: " + metrics.getIdentifyCandidateMaxHitFilterTime());
        System.out.println("Metrics.IdentifyCandidatesTime: " + metrics.getIdentifyCandidatesTime());
        System.out.println("Metrics.IdentifyCandidateTailMatchTime: " + metrics.getIdentifyCandidateTailMatchTime());
        System.out.println("Metrics.InitialParsedInputTextCount: " + metrics.getInitialParsedInputTextCount());
        System.out.println("Metrics.ParseTime: " + metrics.getParseTime());
        System.out.println("Metrics.PreScoringCandidateCount: " + metrics.getPreScoringCandidateCount());
        System.out.println("Metrics.RawCandidateCount: " + metrics.getRawCandidateCount());
        System.out.println("Metrics.ThresholdScore: " + metrics.getThresholdScore());
        System.out.println("Metrics.TokenSetCount: " + metrics.getTokenSetCount());
        for (Scorer scorer : metrics.getTimedScorers()) {
            System.out.println("Scorer." + scorer.getClass().getName() + ": " + metrics.getScorerTime(scorer));
        }

        for (PlaceRepresentation placeRep : results.getPlaceRepresentations()) {
            System.out.println("Place-Rep: " + placeRep);
        }

        System.exit(0);
    }
}
