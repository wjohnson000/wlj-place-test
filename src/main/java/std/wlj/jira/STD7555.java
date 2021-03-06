package std.wlj.jira;

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
 * Based on the "C:\temp\important\place-xxx.txt" file, which contain details for search requests
 * with wild-cards, run a bunch of them through the interpretation engine with and without the
 * wild-card characters and compare results.
 * 
 * @author wjohnson000
 *
 */
public class STD7555 {

    static final int    SKIP_COUNT  = 11;
    static final Long   LONG_ZERO   = 0L;

    public static void main(String... args) throws PlaceDataException, IOException {
        System.setProperty("WARM_CACHE_EXPIRE_TIME", "10"); 
        System.setProperty("L1_CACHE_EXPIRE_TIME", "2"); 
        System.setProperty("L2_CACHE_EXPIRE_TIME", "2"); 
        System.setProperty("L3_CACHE_EXPIRE_TIME", "2"); 

//        System.setProperty("enable.interp.cache", "false");
//        System.setProperty("enable.solr.cache", "false");
//        System.setProperty("enable.repid.chain.cache", "false");

        SolrService  solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.7.1");
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        // Seed the process
//        for (String text : Arrays.asList("Mount View Church of Christ Christian Cemetery", "Mount View Church of Christ ?Christian? Cemetery")) {
        for (String text : Arrays.asList("Mount View Church of Christ ?Christian? Cemetery")) {
            long timeAA = System.nanoTime();
            doIt(placeService, 0, "en", text);
            long timeBB = System.nanoTime();
            System.out.println("Start-up.time=" + (timeBB - timeAA) / 1_000_000.0);
        }

        solrService.shutdown();
        System.exit(0);
    }

    static void doIt(PlaceService placeService, int ndx, String locale, String name) {
        try {
            PlaceRequestBuilder builder = placeService.createRequestBuilder(name, new StdLocale(locale));
            builder.setShouldCollectMetrics(true);
            builder.setFilterResults(false);

            long timeAA = System.nanoTime();
            PlaceRequest request = builder.getRequest();
            PlaceResults results = placeService.requestPlaces(request);
            PlaceRepresentation[] placeReps = results.getPlaceRepresentations();
            long timeBB = System.nanoTime();

            System.out.println("\n" + ndx + " --> " + name);
            int count = 0;
            for (PlaceRepresentation rep : placeReps) {
                if (++count > 100) break;
                System.out.println("    " + rep.getFullDisplayName(StdLocale.ENGLISH).get() + " | " + Arrays.toString(rep.getJurisdictionChainIds()));
                getScorers(rep).forEach((scorer, val) -> System.out.println("       scr: " + scorer + " --> " + val));
            }
            System.out.println("    Time=" + (timeBB - timeAA) / 1_000_000.0);
        } catch(Exception ex) {
            System.out.println("Exception for " + name + " --> " + ex.getMessage());
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
