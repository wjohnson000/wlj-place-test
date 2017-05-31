package std.wlj.jira;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.Metrics;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceResults.Annotation;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.ResultMetadata;
import org.familysearch.standards.place.Scoring;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.scoring.Scorecard;
import org.familysearch.standards.place.scoring.Scorer;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;
import org.familysearch.standards.place.search.interp.Interpretation;
import org.familysearch.standards.place.search.interp.ParsePath;

import std.wlj.util.SolrManager;


public class STD4147 {

    @SuppressWarnings("unchecked")
    public static void main(String... args) throws PlaceDataException, IOException {
//        SolrService  solrService = SolrManager.awsBetaService(false);
        SolrService  solrService = SolrManager.awsProdService(false);
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        String text = "Q, Canada";
        System.out.println(">>> Search for: " + text);

        PlaceRequestBuilder builder;
        builder = placeService.createRequestBuilder(text, StdLocale.ENGLISH);
//        builder.setUseWildcards(true);
        builder.setShouldCollectMetrics(true);
        builder.setFilterResults(false);

        PlaceResults results = placeService.requestPlaces(builder.getRequest());

        List<ParsePath> ppaths  = (List<ParsePath>)results.getAttr(PlaceResults.Attribute.PARSE_PATHS);
        // Parse paths ... interesting !!
        System.out.println("\n>>> PARSE PATHS <<<");
        for (ParsePath ppath : ppaths) {
            System.out.println("PPTH: " + ppath.getTokens().stream().map(tk -> tk.serializeToStr()).collect(Collectors.joining()));
        }

        System.out.println("\n>>> ANNOTATIONS <<<");
        Iterator<Annotation> annIter = results.getAnnotations();
        while (annIter.hasNext()) {
            System.out.println("ANNT: " + annIter.next().toString());
        }

        System.out.println("\n>>> RESULTS FOUND <<< COUNT=" + results.getPlaceRepresentations().length);
        for (PlaceRepresentation placeRep : results.getPlaceRepresentations()) {
            ResultMetadata rmd = placeRep.getMetadata();
            Interpretation itp = rmd.getInterpretation();
            Scoring        scr = rmd.getScoring();
            Scorecard      scd = itp.getScorecard();

            System.out.println("    -- repid: " + placeRep.getId() + " .. " + placeRep.getFullDisplayName(StdLocale.ENGLISH).get());
            System.out.println("       parse: " + itp.getParsedInput());
            System.out.println("       score: " + scd.getRawScore() + " .. " + scd.getRelevanceScore());
            System.out.println("       tokns: " + itp.getParsedInput().getTokens().stream().map(tk -> tk.serializeToStr()).collect(Collectors.joining()));
            itp.getParsedInput().getTokens()
                .forEach(tk -> System.out.println("        tokn: [" +
                                                  tk.serializeToStr() + "|" +
                                                  tk.getOriginalToken() + "|" +
                                                  tk.getOriginalNormalizedToken() + "|" +
                                                  tk.getScript() + "|" +
                                                  (tk.getTypes().stream()
                                                      .map(ttt -> ttt.getDeclaringClass().getSimpleName() + "::" + ttt.getIndex() + "::" + ttt.toString())
                                                      .collect(Collectors.joining(",", "(", ")"))) +
                                                  "]"));
            System.out.println("       chain: " + Arrays.stream(itp.getJurisdictionIds()).mapToObj(id -> String.valueOf(id)).collect(Collectors.joining(",", "[", "]")));
            System.out.println("       tkn-x: " + Arrays.stream(itp.getTokenIndexMatches()).map(pr -> pr == null ? "0" : String.valueOf(pr.getRepId())).collect(Collectors.joining(",", "[", "]")));
            System.out.println("       vrntN: " + Arrays.stream(itp.getMatchedVariants()).map(vr -> vr == null ? "null" : String.valueOf(vr.getNormalizedName())).collect(Collectors.joining(",", "[", "]")));
            System.out.println("       vrntU: " + Arrays.stream(itp.getMatchedVariants()).map(vr -> vr == null ? "null" : String.valueOf(vr.getName().get())).collect(Collectors.joining(",", "[", "]")));
            System.out.println("       tCode: " + Arrays.stream(itp.getMatchedVariants()).map(vr -> vr == null ? "null" : String.valueOf(vr.getType().getCode())).collect(Collectors.joining(",", "[", "]")));
            System.out.println("       level: " + Arrays.stream(itp.getJurisdictionLevelMatches()).mapToObj(ndx -> String.valueOf(ndx)).collect(Collectors.joining(",", "[", "]")));
            for (Scorer scorer : scd.getScorersThatScored()) {
                System.out.println("          sc: " + scorer.getClass().getSimpleName() + " .. " + scd.getScoreFromScorer(scorer) + " [" + scd.getScoreReason(scorer) + "]");
                System.out.println("            : " + scr.getRawScore() + " .. " + scr.getRelevanceScore() + " .. " + results.getMetrics().getMapNumberMetric(Metrics.MapNumberMetric.SCORER_BASIS_SCORE, scorer.getClass().getSimpleName()).intValue());
            }
        }

        solrService.shutdown();
        System.exit(0);
    }
}
