package std.wlj.jira;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;

import std.wlj.util.SolrManager;

public class STD_Date_20170314 {

    static String[] placeNames = {
//            "Berry Township, Cochrane",
            "Gagnoa, Fromager, Côte d'Ivoire"
//            "Berry Township, Cochrane, Ontario, British Colonial America",
//            "Beverly Hills Judicial Township, Los Angeles, California, United States",
//            "Beverly Hills Judicial Township"
    };

    public static void main(String... args) throws PlaceDataException {
//      SolrService solrService = SolrManager.localEmbeddedService();
        SolrService solrService = SolrManager.awsProdService(false);
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        for (String placeName : placeNames) {
            PlaceRequestBuilder builder = placeService.createRequestBuilder(placeName, StdLocale.ENGLISH);
            builder.setTargetLanguage(new StdLocale("da"));
//            builder.setPartialInput(true);
            PlaceRequest request = builder.getRequest();
            PlaceResults results = placeService.requestPlaces(request);

            System.out.println("============================================================================================================");
            System.out.println("Search for >> " + placeName + " <<");
            for (PlaceRepresentation placeRep : results.getPlaceRepresentations()) {
                System.out.println("Place-Rep: " + placeRep);
            }
            System.out.println("============================================================================================================");
            System.out.println();
        }

        System.exit(0);
    }

}
