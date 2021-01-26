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
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.scoring.Scorecard;
import org.familysearch.standards.place.scoring.Scorer;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;

import std.wlj.util.SolrManager;

/**
 * Run an interpretation through the Place 2.0 engine.
 * 
 * @author wjohnson000
 *
 */
public class RunInterpretationCJKJira {

    private static String[][] names = {
        { "zh", "中國浙江衢州" },
        { "zh", "中國浙江衢州開化縣劉家" },
        { "zh", "中華人民共和國" },
        { "zh", "中華人民共和國浙江衢州開化縣劉家" },
        { "zh", "上海交通西路南中華坊" },
        { "zh", "今岱崮镇东上峪村哗啦泉自然村" },
        { "zh", "南昌新城又傅至新北十里羅坊西五里並仙里陽熙" },
        { "zh", "昇西乡本乡三图双滨里邾家桥刘家村" },
        { "zh", "无锡北门处十四都六啚高明桥东村" },
    };  

    public static void main(String... args) throws PlaceDataException, IOException {
        
//        SolrService  solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.7.1");
        SolrService  solrService = SolrManager.awsBetaService(true);

        PlaceService placeService = PlaceService.getInstance(new DefaultPlaceRequestProfile(null, solrService, null));

        long time0 = System.nanoTime();
        for (String[] name : names) {
            doIt(placeService, name[0], name[1], false);
        }
        long time1 = System.nanoTime();
        System.out.println("TTT: " + (time1 - time0) / 1_000_000.0);
 
        solrService.shutdown();
        System.exit(0);
    }

    static void doIt(PlaceService placeService, String locale, String name, boolean filterRequests) {
        try {
            PlaceRequestBuilder builder = placeService.createRequestBuilder(name, new StdLocale(locale));
            builder.setShouldCollectMetrics(true);
            builder.setFilterResults(filterRequests);

            long timeAA = System.nanoTime();
            PlaceRequest request = builder.getRequest();
            PlaceResults results = placeService.requestPlaces(request);
            long timeBB = System.nanoTime();

            System.out.println("\n---> " + name);
            for (PlaceRepresentation rep : results.getPlaceRepresentations()) {
                System.out.println("    rep." + rep.getFullDisplayName(StdLocale.ENGLISH).get() +
                        " | " + Arrays.toString(rep.getJurisdictionChainIds()) +
                        " | " + rep.getPreferredLocale() +
                        " + " + getRawScore(rep) +
                        " + " + getRelevanceScore(rep));
//                getScorers(rep).forEach((scorer, val) -> System.out.println("       scr: " + scorer + " --> " + val));
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
