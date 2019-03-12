package std.wlj.interpretation;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.familysearch.standards.GenealogicalDate;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.PlaceType;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.scoring.Scorecard;
import org.familysearch.standards.place.scoring.Scorer;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;

import std.wlj.util.SolrManager;

/**
 * Request stuff from the place-service.  See what comes back.  Figure out what is going on.
 * 
 * @author wjohnson000
 *
 */
public class RunRequest {

    public static void main(String... args) throws PlaceDataException, IOException {
        SolrService  solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.1.0");
//        SolrService  solrService = SolrManager.awsBetaService(true);

        PlaceService placeService = PlaceService.getInstance( new DefaultPlaceRequestProfile( null, solrService, null ) );

//        doIt(placeService, 0, "en", "Champlain", null, null, null);
//        doIt(placeService, 0, "en", "Champlain, Clinton, New York, USA", null, null, null);
        doIt(placeService, 0, "en", "Champlain", null, 362, null);
        doIt(placeService, 0, "en", "Champlain", null, 339, null);
//        doIt(placeService, 0, "en", "Champlain", null, 393135, null);

        solrService.shutdown();
        System.exit(0);
    }

    static void doIt(PlaceService placeService, int ndx, String locale, String name, String date, Integer parentId, Integer typeId) {
        try {
            PlaceRequestBuilder builder = placeService.createRequestBuilder(name, new StdLocale(locale));
            builder.setShouldCollectMetrics(true);
            builder.setFilterResults(false);
            if (date != null) {
                builder.setOptionalDate(GenealogicalDate.getInstance(date));
            }
            if (parentId != null) {
                PlaceRepresentation parent = placeService.getPlaceRepresentation(parentId);
                builder.addRequiredParent(parent);
            }
            if (typeId != null) {
                PlaceType type = placeService.getPlaceTypeById(typeId);
                builder.addRequiredPlaceType(type);
            }
            

            long timeAA = System.nanoTime();
            PlaceRequest request = builder.getRequest();
            PlaceResults results = placeService.requestPlaces(request);
            long timeBB = System.nanoTime();

            System.out.println("\n\nService: " + placeService.getProfile().getName());
            System.out.println(ndx + " --> NAME: " + name);
            System.out.println("  --> DATE: " + date);
            System.out.println("  -->  PAR: " + parentId);
            System.out.println("  --> TYPE: " + typeId);
            System.out.println("  --> REP#: " + results.getPlaceRepresentations().length);

            for (PlaceRepresentation rep : results.getPlaceRepresentations()) {
                System.out.println("    rep." + rep.getFullDisplayName(StdLocale.ENGLISH).get() +
                        " | " + Arrays.toString(rep.getJurisdictionChainIds()) +
                        " | " + rep.getJurisdictionFromYear() +
                        " | " + rep.getJurisdictionToYear() +
                        " | " + getRawScore(rep) +
                        " + " + getRelevanceScore(rep));
                getScorers(rep).forEach((scorer, val) -> System.out.println("       scr: " + scorer + " --> " + val));
            }

            System.out.println("    Time=" + (timeBB - timeAA) / 1_000_000.0);
        } catch(Exception ex) {
            System.out.println("Exception for " + name + " --> " + ex.getMessage());
        }
    }

    static String getText(String line) {
        int ndx0 = line.indexOf('"');
        int ndx1 = line.indexOf('"', ndx0+1);
        if (ndx0 == 0  &&  ndx1 > ndx0) {
            return line.substring(ndx0+1, ndx1);
        } else {
            return null;
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
