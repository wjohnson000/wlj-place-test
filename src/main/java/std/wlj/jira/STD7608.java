package std.wlj.jira;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.PlaceNameBridge;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;
import org.familysearch.standards.place.scoring.Scorecard;
import org.familysearch.standards.place.scoring.Scorer;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.parser.PlaceNameToken;

import std.wlj.util.SolrManager;

/**
 * Run an request through the Place 2.0 engine, with and without type-ahead.
 * 
 * @author wjohnson000
 *
 */
public class STD7608 {

    public static void main(String... args) throws PlaceDataException, IOException {
        SolrService  solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.7.1");

        PlaceService placeService = PlaceService.getInstance(new DefaultPlaceRequestProfile(null, solrService, null));

        doIt(placeService, 0, "en", "潮州铁铺", false);
        doIt(placeService, 0, "en", "潮州铁铺", true);

        solrService.shutdown();
        System.exit(0);
    }

    static void doIt(PlaceService placeService, int ndx, String locale, String name, boolean partial) {
        try {
            PlaceRequestBuilder builder = placeService.createRequestBuilder(name, new StdLocale(locale));
            builder.setShouldCollectMetrics(true);
            if (partial) {
                builder.setPartialInput(partial);
            }

            long timeAA = System.nanoTime();
            PlaceRequest request = builder.getRequest();
            PlaceResults results = placeService.requestPlaces(request);
            long timeBB = System.nanoTime();

            System.out.println("\n" + ndx + " --> " + name);
            for (PlaceRepresentation rep : results.getPlaceRepresentations()) {
                System.out.println("    rep." + rep.getFullDisplayName(StdLocale.ENGLISH).get() +
                        " | " + Arrays.toString(rep.getJurisdictionChainIds()) +
                        " + " + getRawScore(rep) +
                        " + " + getRelevanceScore(rep));
                System.out.println(getScoringDetails(rep));
                System.out.println(getTokenMatchDetails(rep));
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

    static String getScoringDetails(PlaceRepresentation rep) {
        StringBuilder buff = new StringBuilder();

        Scorecard sCard = rep.getMetadata().getInterpretation().getScorecard();
        for (Scorer scorer : sCard.getScorersThatScored()) {
            buff.append("      SC.").append(scorer.getClass().getSimpleName());
            buff.append(": ").append(sCard.getScoreFromScorer(scorer));
            buff.append("  [").append(sCard.getScoreReason(scorer)).append("]\n");
        }

        return buff.toString();
    }

    static String getTokenMatchDetails(PlaceRepresentation rep) {
        StringBuilder buff = new StringBuilder();

        PlaceRepBridge[] reps = rep.getMetadata().getInterpretation().getTokenIndexMatches();
        PlaceNameBridge[] nameMatches = rep.getMetadata().getInterpretation().getMatchedVariants();
        List<PlaceNameToken> tokens = rep.getMetadata().getInterpretation().getParsedInput().getTokens();

        for (int i = 0; i < reps.length; i++) {
            PlaceNameToken pToken = tokens.get(i);
            buff.append("      TK.").append(pToken.getOriginalToken()).append("\n");
            buff.append("        .").append(pToken.toDebugString()).append("\n");

            if (reps[i] != null) {
                buff.append("        ID.").append(reps[i].getRepId()).append("\n");
            }

            if (nameMatches[i] != null) {
                buff.append("        NM.").append(nameMatches[i].getName().getLocale());
                buff.append(", ").append(nameMatches[i].getName().get());
                if (nameMatches[i].getType() != null) {
                    buff.append(", ").append(nameMatches[i].getType().getCode());
                }
                buff.append("\n");
            }
        }
        return buff.toString();
    }
}
