package std.wlj.jira;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.RequestMetrics;


public class STD2644Raw {
    private static final double ONE_MILLION = 1000000.0;

    private static final String[] textes = {
        "Северная, Аромашевский район, Тюменская область, Россия",
        "Severnaya, Aromashevskiy Rayon, Tyumen Oblast, Russia"
    };


    public static void main(String... args) throws PlaceDataException {
        System.setProperty("solr.master.url", "");
        System.setProperty("solr.solr.home", "C:/tools/solr/data/tokoro");
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");

        SolrService solrService = new SolrService();
        PlaceService placeService = new PlaceService(new DefaultPlaceRequestProfile(solrService));

        System.out.println("Place-Type count: " + solrService.getTypes(TypeBridge.TYPE.PLACE, false).size());
        System.out.println("Name-Type count: " + solrService.getTypes(TypeBridge.TYPE.NAME, false).size());

        for (String text : textes) {
            PlaceRequestBuilder builder;
//            builder = placeService.createRequestBuilder(text, StdLocale.ENGLISH);
            builder = placeService.createRequestBuilder(text, StdLocale.RUSSIAN);
            builder.setFuzzyType(PlaceRequest.FuzzyType.EDIT_DISTANCE);
            builder.setShouldCollectMetrics(true);
            builder.setFilterResults(false);

            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println(">>> Search for: " + text);
            long then = System.nanoTime();
            PlaceResults results = placeService.requestPlaces(builder.getRequest());
            long nnow = System.nanoTime();

            RequestMetrics metrics = results.getMetrics();
            StringBuilder buff = new StringBuilder();
            buff.append(text);
            buff.append("|").append((nnow - then) / ONE_MILLION);
            buff.append("|").append(metrics.getTotalTime() / ONE_MILLION);
            buff.append("|").append(metrics.getIdentifyCandidateLookupTime() / ONE_MILLION);
            buff.append("|").append(metrics.getParseTime() / ONE_MILLION);
            buff.append("|").append(metrics.getScoringTime() / ONE_MILLION);
            buff.append("|-1");

            buff.append("|").append(results.getReturnedCount());
            for (PlaceRepresentation resultModel : results.getPlaceRepresentations()) {
                buff.append("|").append(resultModel.getId());
                System.out.println("    -- " + resultModel.getFullDisplayName(StdLocale.ENGLISH).get());
            }
            System.out.println(buff.toString());
        }
        System.exit(0);
    }
}
