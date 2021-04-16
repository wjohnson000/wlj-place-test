package std.wlj.interpretation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;
import org.familysearch.standards.place.scoring.Scorecard;
import org.familysearch.standards.place.scoring.Scorer;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;

import std.wlj.util.SolrManager;

/**
 * Compare results of a wildcard vs. partial match ...
 * 
 * @author wjohnson000
 *
 */
public class TestWildcardVsPartial {

    static String[] UGLY_TEXT = {
        "Buffalo Ha*",
        "Bathurst West*, Gloucester, New Brunswick, British Colony",
        "Columbus, St*",
        "Humbolt, Sa*",
        "Bloomington, Sc*",
        "Wa*, , Illinois",
        "Greendale Cem. Midland Co, Mi*",
        "Lovenjoel, Brabant, Belgi*",
    };

    public static void main(String... args) throws PlaceDataException, IOException {
        System.setProperty("WARM_CACHE_EXPIRE_TIME", "10"); 
        System.setProperty("L1_CACHE_EXPIRE_TIME", "2"); 
        System.setProperty("L2_CACHE_EXPIRE_TIME", "2"); 
        System.setProperty("L3_CACHE_EXPIRE_TIME", "2"); 

        SolrService  solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.1.0");
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        // Seed the process
        for (String text : UGLY_TEXT) {
            doIt(placeService, "en", text, false);
//            doIt(placeService, "en", text.replace('*', ' '), true);
        }

        solrService.shutdown();
        System.exit(0);
    }

    static void doIt(PlaceService placeService, String locale, String name, boolean isPartial) {
        try {
            PlaceRequestBuilder builder = placeService.createRequestBuilder(name, new StdLocale(locale));
            builder.setShouldCollectMetrics(true);
            builder.setFilterResults(false);
            builder.setPartialInput(isPartial);

            long timeAA = System.nanoTime();
            PlaceRequest request = builder.getRequest();
            PlaceResults results = placeService.requestPlaces(request);
            PlaceRepresentation[] placeReps = results.getPlaceRepresentations();
            long timeBB = System.nanoTime();

            System.out.println("\n>>> " + name + " .. partial=" + isPartial + " --> found=" + placeReps.length);
            int count = 0;
            for (PlaceRepresentation rep : placeReps) {
                if (++count > 40) break;
                System.out.println("    " + rep.getFullDisplayName(StdLocale.ENGLISH).get() + " | " + Arrays.toString(rep.getJurisdictionChainIds()));
                System.out.println("    RAW: " + getRawScore(rep));
                System.out.println("    REL: " + getRelevanceScore(rep));
                getScorers(rep).forEach((scorer, val) -> System.out.println("       scr: " + scorer + " --> " + val));
            }
            System.out.println("    Time=" + (timeBB - timeAA) / 1_000_000.0);
        } catch(Exception ex) {
            System.out.println("Exception for " + name + " --> " + ex.getMessage());
        }
    }

    static int getRawScore(PlaceRepresentation rep) {
        if (rep.getMetadata() != null  &&  rep.getMetadata().getScoring() != null) {
            return rep.getMetadata().getScoring().getRawScore();
        } else {
            return 0;
        }
    }

    static int getRelevanceScore(PlaceRepresentation rep) {
        if (rep.getMetadata() != null  &&  rep.getMetadata().getScoring() != null) {
            return rep.getMetadata().getScoring().getRelevanceScore();
        } else {
            return 0;
        }
    }

    static Map<String, Integer> getScorers(PlaceRepresentation rep) {
        if (rep.getMetadata() != null  &&  rep.getMetadata().getScoring() != null) {
            Scorecard scorecard = rep.getMetadata().getInterpretation().getScorecard();
            Set<Scorer> scorers = scorecard.getScorersThatScored();
            return scorers.stream()
                    .collect(Collectors.toMap(
                            sc -> sc.getClass().getSimpleName(),
                            sc -> scorecard.getScoreFromScorer(sc)));
        }

        return Collections.emptyMap();
    }
}
