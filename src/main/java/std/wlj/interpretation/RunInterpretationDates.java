package std.wlj.interpretation;

import java.io.IOException;
import java.util.Arrays;

import org.familysearch.standards.GenealogicalDate;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.ResultMetadata;
import org.familysearch.standards.place.Scoring;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;
import org.familysearch.standards.place.search.ConfigurablePlaceRequestProfile;
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
public class RunInterpretationDates {

    public static void main(String... args) throws PlaceDataException, IOException {
//        SolrService  solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.1.0");
        SolrService  solrService = SolrManager.awsBetaService(true);

        PlaceService placeService = PlaceService.getInstance( new DefaultPlaceRequestProfile( null, solrService, null ) );
        PlaceService placeInterpService = PlaceService.getInstance( new ConfigurablePlaceRequestProfile( ConfigurablePlaceRequestProfile.URL_INTERP_PROPS, solrService ) );
        PlaceService placeMatchService = PlaceService.getInstance( new ConfigurablePlaceRequestProfile( ConfigurablePlaceRequestProfile.URL_MATCH_PROPS, solrService ) );

        // Seed the process
//        doIt(placeService, 0, "en", "US Alabama");
//        doIt(placeService, 0, "en", "US-Alabama");
//        doIt(placeService, 0, "en", "US, Alabama");
//        doIt(placeService, 0, "en", "US Kaput Bloomington, Indiana");
        doIt(placeService, 0, "en", "Hertfordshire, North Mimms");
        doIt(placeInterpService, 0, "en", "Hertfordshire, North Mimms");
        doIt(placeMatchService, 0, "en", "Hertfordshire, North Mimms");

        solrService.shutdown();
        System.exit(0);
    }

    static void doIt(PlaceService placeService, int ndx, String locale, String name) {
        try {
            PlaceRequestBuilder builder = placeService.createRequestBuilder(name, new StdLocale(locale));
            builder.setShouldCollectMetrics(true);
            builder.setFilterResults(false);
            builder.setOptionalDate(GenealogicalDate.getInstance("Jan 1 1573/Dec 31 1603"));

            long timeAA = System.nanoTime();
            PlaceRequest request = builder.getRequest();
            PlaceResults results = placeService.requestPlaces(request);
            long timeBB = System.nanoTime();

            System.out.println("\n\nService: " + placeService.getProfile().getName());
            System.out.println(ndx + " --> " + name);
            for (PlaceRepresentation rep : results.getPlaceRepresentations()) {
                System.out.println("    rep." + rep.getFullDisplayName(StdLocale.ENGLISH).get() +
                        " | " + Arrays.toString(rep.getJurisdictionChainIds()) +
                        " + " + getRawScore(rep) +
                        " + " + getRelevanceScore(rep));
            }

            if (results.getAlternatePlaceRepresentations() != null) {
                for (PlaceRepresentation rep : results.getAlternatePlaceRepresentations()) {
                    System.out.println("    alt." + rep.getFullDisplayName(StdLocale.ENGLISH).get() +
                        " | " + Arrays.toString(rep.getJurisdictionChainIds()) +
                        " + " + getRawScore(rep) +
                        " + " + getRelevanceScore(rep));
                }
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
}
