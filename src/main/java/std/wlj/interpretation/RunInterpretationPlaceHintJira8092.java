package std.wlj.interpretation;

import java.io.IOException;
import java.util.Arrays;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.search.ConfigurablePlaceRequestProfile;

import std.wlj.util.SolrManager;

/**
 * Run an interpretation through the Place 2.0 engine.
 * 
 * @author wjohnson000
 *
 */
public class RunInterpretationPlaceHintJira8092 {

    public static void main(String... args) throws PlaceDataException, IOException {
        SolrService  solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.7.1");
//        SolrService  solrService = SolrManager.awsBetaService(true);

        PlaceService placeInterpService = PlaceService.getInstance( new ConfigurablePlaceRequestProfile( ConfigurablePlaceRequestProfile.URL_INTERP_PROPS, solrService ) );

        doIt(placeInterpService, 0, "en", "Washington, United States");
        doIt(placeInterpService, 0, "en", "Washington", 1);
        doIt(placeInterpService, 0, "en", "Washington, United States", 1);

        solrService.shutdown();
        System.exit(0);
    }

    static void doIt(PlaceService placeService, int ndx, String locale, String name, int... placeHint) {
        try {
            PlaceRequestBuilder builder = placeService.createRequestBuilder(name, new StdLocale(locale));
            builder.setShouldCollectMetrics(true);
            if (placeHint != null) {
                for (int id : placeHint) {
                    PlaceRepresentation placeRep = placeService.getPlaceRepresentation(id);
                    if (placeRep != null) {
                        builder.addPlaceHint(placeRep);
                    }
                }
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
