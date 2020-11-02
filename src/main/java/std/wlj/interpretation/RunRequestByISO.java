/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.interpretation;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.TypeGroup;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.search.ConfigurablePlaceRequestProfile;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.util.PlaceHelper;

import std.wlj.util.SolrManager;

/**
 * @author wjohnson000
 *
 */
public class RunRequestByISO {

    static TypeGroup typeGroup42;

    public static void main(String...args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("C:/temp/iso-country-codes.txt"), StandardCharsets.UTF_8);
        System.out.println("LINES: " + lines.size());

        SolrService  solrService = SolrManager.awsProdService(false);
        PlaceService placeService = PlaceService.getInstance( new DefaultPlaceRequestProfile( null, solrService, null ) );
        PlaceService placeInterpService = PlaceService.getInstance( new ConfigurablePlaceRequestProfile( ConfigurablePlaceRequestProfile.URL_INTERP_PROPS, solrService ) );

        typeGroup42 = placeService.getPlaceTypeGroupById(42);
        System.out.println("TG42: " + typeGroup42);
        for (String line : lines) {
            String[] chunk = PlaceHelper.split(line, '\t');
            if (chunk.length > 3) {
                doSearch(placeInterpService, chunk[0], chunk[1], chunk[2]);
            }
        }
    }

    static void doSearch(PlaceService service, String name, String iso2, String iso3) {
        PlaceRequestBuilder builder = service.createRequestBuilder(name, StdLocale.ENGLISH);
        builder.setShouldCollectMetrics(true);
        builder.setFilterResults(false);
        builder.addRequiredPlaceTypeGroup(typeGroup42);

        PlaceRequest request = builder.getRequest();
        PlaceResults results = service.requestPlaces(request);

        // If nothing is found, try the request w/out specifying the place.
        if (results.getPlaceRepresentations().length == 0) {
            builder = service.createRequestBuilder(name, StdLocale.ENGLISH);
            builder.setShouldCollectMetrics(true);
            builder.setFilterResults(false);

            request = builder.getRequest();
            results = service.requestPlaces(request);
        }

        PlaceRepresentation bestRep = Arrays.stream(results.getPlaceRepresentations())
                                            .filter(rep -> rep.getJurisdictionChainIds().length == 1)
                                            .findFirst().orElse(null);
        
        try {
            if (results.getPlaceRepresentations().length == 0) {
                System.out.println(name + "|" + iso2 + "|" + iso3 + "|" + name + "||||||");
            } else if (bestRep != null) {
                System.out.println(name + "|" + iso2 + "|" + iso3 +
                                          "|" + bestRep.getFullDisplayName(StdLocale.ENGLISH).get() +
                                          "|" + bestRep.getJurisdictionChainIds()[bestRep.getJurisdictionChainIds().length-1] +
                                          "|" + Arrays.toString(bestRep.getJurisdictionChainIds()) +
                                          "|" + bestRep.getJurisdictionFromYear() +
                                          "|" + bestRep.getJurisdictionToYear() +
                                          "|" + getRawScore(bestRep) +
                                          "|" + getRelevanceScore(bestRep));
            } else {
                for (PlaceRepresentation rep : results.getPlaceRepresentations()) {
                    System.out.println(name + "|" + iso2 + "|" + iso3 +
                                              "|" + rep.getFullDisplayName(StdLocale.ENGLISH).get() +
                                              "|" + rep.getJurisdictionChainIds()[rep.getJurisdictionChainIds().length-1] +
                                              "|" + Arrays.toString(rep.getJurisdictionChainIds()) +
                                              "|" + rep.getJurisdictionFromYear() +
                                              "|" + rep.getJurisdictionToYear() +
                                              "|" + getRawScore(rep) +
                                              "|" + getRelevanceScore(rep));
                }
            }
        } catch(Exception ex) {
            System.out.println("Exception for " + name + " --> " + ex.getMessage());
            ex.printStackTrace();
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