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
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;
import org.familysearch.standards.place.scoring.Scorecard;
import org.familysearch.standards.place.scoring.Scorer;
import org.familysearch.standards.place.search.ConfigurablePlaceRequestProfile;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;

import std.wlj.util.SolrManager;

/**
 * Based on the "C:\temp\important\place-xxx.txt" file, which contain details for search requests
 * with wild-cards, run a bunch of them through the interpretation engine with and without the
 * wild-card characters and compare results.
 * 
 * @author wjohnson000
 *
 */
public class RunInterpretationThree {

    public static void main(String... args) throws PlaceDataException, IOException {
//        SolrService  solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.1.0");
        SolrService  solrService = SolrManager.awsService55(); //.awsBetaService(true);

        PlaceService placeService = PlaceService.getInstance( new DefaultPlaceRequestProfile( null, solrService, null ) );
        PlaceService placeInterpService = PlaceService.getInstance( new ConfigurablePlaceRequestProfile( ConfigurablePlaceRequestProfile.URL_INTERP_PROPS, solrService ) );

        doIt(placeService, 0, "en", "Hertfordshire, North Mimms", null, null, null);
        doIt(placeService, 0, "en", "Hertfordshire, North Mimms", "Jan 1 1573/Dec 31 1603", null, null);
        doIt(placeInterpService, 0, "en", "Hertfordshire, North Mimms", null, null, null);
        doIt(placeInterpService, 0, "en", "Hertfordshire, North Mimms", "Jan 1 1573/Dec 31 1603", null, null);
        doIt(placeInterpService, 0, "en", "Hertfordshire, North Mymms", null, null, null);
        doIt(placeInterpService, 0, "en", "Hertfordshire, North Mymms", "Jan 1 1573/Dec 31 1603", null, null);

        doIt(placeInterpService, 0, "en", "Porto, Portugal", null, null, null);
        doIt(placeInterpService, 0, "en", "Porto, Portugal", "May 1 1938/Jun 30 1938", null, null);

        doIt(placeInterpService, 0, "en", "Sevier", null, null, null);
        doIt(placeInterpService, 0, "en", "Sevier", null, 342, null);
        doIt(placeInterpService, 0, "en", "Sevier", "1700", null, null);
        doIt(placeInterpService, 0, "en", "Sevier", "1700", 342, null);
        doIt(placeInterpService, 0, "en", "Sevier", "1800", null, null);
        doIt(placeInterpService, 0, "en", "Sevier", "1800", 342, null);
        doIt(placeInterpService, 0, "en", "Sevier", "1850", null, null);
        doIt(placeInterpService, 0, "en", "Sevier", "1850", 342, null);
        doIt(placeInterpService, 0, "en", "Sevier", "1900", null, null);

        doIt(placeService, 0, "en", "Zierstorf/Bartelshäger Ziegelei", null, null, null);
        doIt(placeService, 0, "en", "Mount Zion First Baptist Church", null, null, null);
        doIt(placeInterpService, 0, "en", "Mount Zion First Baptist Church", null, null, null);
        doIt(placeService, 0, "en", "First Baptist Church Chapel", null, null, null);
        doIt(placeInterpService, 0, "en", "First Baptist Church Chapel", null, null, null);
        doIt(placeService, 0, "en", "Thompson First Baptist Church", null, null, null);
        doIt(placeInterpService, 0, "en", "Thompson First Baptist Church", null, null, null);
        doIt(placeService, 0, "en", "Greenview First Baptist Church", null, null, null);
        doIt(placeInterpService, 0, "en", "Greenview First Baptist Church", null, null, null);

        doIt(placeService, 0, "en", "First Baptist Church", null, null, null);
        doIt(placeService, 0, "en", "***,+Virginia", null, null, null);
        doIt(placeService, 0, "en", "Santa Maria", null, null, null);
        doIt(placeService, 0, "en", "Asbacher Mühle / Prússia Renana (Atual Alemanha, Estado de Rheinland-Pfalz", null, null, null);
        doIt(placeService, 0, "en", "Gaon(???)", null, null, null);
        doIt(placeService, 0, "en", "The Church of Jesus Christ of Latter-day Saints", null, null, null);

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

//            if (results.getAlternatePlaceRepresentations() != null) {
//                for (PlaceRepresentation rep : results.getAlternatePlaceRepresentations()) {
//                    System.out.println("    alt." + rep.getFullDisplayName(StdLocale.ENGLISH).get() +
//                        " | " + Arrays.toString(rep.getJurisdictionChainIds()) +
//                        " | " + rep.getJurisdictionFromYear() +
//                        " | " + rep.getJurisdictionToYear() +
//                        " | " + getRawScore(rep) +
//                        " + " + getRelevanceScore(rep));
//                    getScorers(rep).forEach((scorer, val) -> System.out.println("       scr: " + scorer + " --> " + val));
//                }
//            }

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
