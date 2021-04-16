package std.wlj.interpretation;

import java.io.IOException;
import java.util.Arrays;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.data.solr.SolrServiceFactory;
import org.familysearch.standards.place.exceptions.PlaceDataException;
import org.familysearch.standards.place.search.ConfigurablePlaceRequestProfile;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;

/**
 * Run an interpretation through the Place 2.0 engine.  The options for the {@link PlaceRequstBuilder}
 * class mirror the parameters described in the "Place Service Developer Guide" on Confluence (see
 * https://almtools.ldschurch.org/fhconfluence/display/Product/Place+Service+Developer+Guide).
 * <p/>
 * 
 * There are two main interpretations engines, corresponding roughly to the "/places/search" and
 * "/places/interp" endpoints.  The former will return whatever results it can, i.e., it's more lax.
 * The latter returns results only if they are very high quality, but it's slower.  The best results
 * are found by using ".getPlaceRepresentations()" method, the "close-but-no-cigar" results are found
 * by using the "getAlternatePlaceRepresentations()" method, which could return null.
 * <p/>
 * 
 * In the code below, the "placeRequestService" corresponds to doing a "/places/search".  It may return
 * lots of results, though you can set a limit to the number of results you want, and you can set a
 * minimum score for anything to be returned.  It will NEVER return anything in the "alternate"
 * place-representations list.
 * <p/>
 * 
 * The "placeInterpService" corresponds to doing a "/places/interp".  It is slower, but will return
 * only high-quality results.
 * <p/>
 * 
 * Test both, set other options, play around with different locales, etc., so you can determine which
 * PlaceService and which settings work best for you.
 * 
 * @author wjohnson000
 *
 */
public class RunPlace20Interpretation {

    public static void main(String... args) throws PlaceDataException, IOException {
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        System.setProperty("solr.replication.url", "");

        SolrService solrService = SolrServiceFactory.makeService("C:/D-drive/solr/standalone-7.1.0");
        PlaceService placeRequestService = PlaceService.getInstance(new DefaultPlaceRequestProfile(null, solrService, null));
        PlaceService placeInterpService  = PlaceService.getInstance(new ConfigurablePlaceRequestProfile(ConfigurablePlaceRequestProfile.URL_INTERP_PROPS, solrService));

        // A few modes examples of interpretations, each done two ways
        doIt(placeRequestService, "en", "US, Alabama");
        doIt(placeRequestService, "en", "US Kaput Bloomington, Indiana");
        doIt(placeRequestService, "en", "日本兵庫県神戸市");

        doIt(placeInterpService, "en", "US, Alabama");
        doIt(placeInterpService, "en", "US Kaput Bloomington, Indiana");
        doIt(placeInterpService, "en", "日本兵庫県神戸市");

        solrService.shutdown();
        System.exit(0);
    }

    static void doIt(PlaceService placeService, String locale, String name) {
        try {
            PlaceRequestBuilder builder = placeService.createRequestBuilder(name, new StdLocale(locale));
            builder.setShouldCollectMetrics(true);

            PlaceRequest request = builder.getRequest();
            PlaceResults results = placeService.requestPlaces(request);

            System.out.println("\n\nResults for interpretation: " + name + " [" + locale + "]");
            for (PlaceRepresentation rep : results.getPlaceRepresentations()) {
                System.out.println("    rep." + rep.getFullDisplayName(StdLocale.ENGLISH).get() +
                        " | " + Arrays.toString(rep.getJurisdictionChainIds()) +
                        " + " + getRawScore(rep) +
                        " + " + getRelevanceScore(rep));
//                PlaceRepresentation[] chain = rep.getJurisdictionChain();  // Return the Jurisdiction chain, which includes the result itself
            }

            if (results.getAlternatePlaceRepresentations() != null) {
                for (PlaceRepresentation rep : results.getAlternatePlaceRepresentations()) {
                    System.out.println("    alt." + rep.getFullDisplayName(StdLocale.ENGLISH).get() +
                        " | " + Arrays.toString(rep.getJurisdictionChainIds()) +
                        " + " + getRawScore(rep) +
                        " + " + getRelevanceScore(rep));
                }
            }
        } catch(Exception ex) {
            System.out.println("Exception for " + name + " --> " + ex.getMessage());
        }
    }

    /**
     * Illustrates how to pull the "raw score" from the {@link PlaceRepresentation} metrics.
     */
    static int getRawScore(PlaceRepresentation rep) {
        if (rep.getMetadata() != null  &&  rep.getMetadata().getScoring() != null) {
            return rep.getMetadata().getScoring().getRawScore();
        } else {
            return 0;
        }
    }

    /**
     * Illustrates how to pull the "relevance score" from the {@link PlaceRepresentation} metrics.
     */
    static int getRelevanceScore(PlaceRepresentation rep) {
        if (rep.getMetadata() != null  &&  rep.getMetadata().getScoring() != null) {
            return rep.getMetadata().getScoring().getRelevanceScore();
        } else {
            return 0;
        }
    }
}
