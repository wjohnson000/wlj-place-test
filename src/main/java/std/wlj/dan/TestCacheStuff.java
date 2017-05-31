package std.wlj.dan;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.DataMetrics;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;

import std.wlj.util.SolrManager;


public class TestCacheStuff {

    /** List of values to interpret ... */
    static String[] textToInterpret = {
        "orem, utah",
        "new york, new york",
        "paris, france",
        ",Idaho",
        "portland, or",
        "London, England",
        "Windsor, New South Wales",
        "Taiamai",
        "Saint-Martin-en-Bresse",
        "Beit Mirrah, Lebanon",
        "Le Noir, North Carolina",
        "Lynn City 2, Essex, Massachusetts",
        "Hessen Lande",
        "Holland, , IN",
        "Cork, Cork, Ireland",
        "Baltimore,Md",
        "Hidalgo co., Texas",
        "Tart l'Abbaye, France",
        "Spanish Fork Cemetery, Lot 6, Block 1, Position 7",
        "Macau, Macau, China",
        "Hung Mo, China",
        "Baretswil, Zurich, Switzerland",
        "Carthage Jail, Hancock, LI.",
        "Salt Lake, Utah",
        "Aguacalientes, Aguacalientes, Aguacalientes, Mexico",
        ",,Aguacalientes, Mexico",
        "Aguacalientes, Mexico",
        "Jasper,, Georgia, United States",
        "New Hampshire, USA",
        "UT",
        ",,UT",
        "of UT",
        "Of,Turkey",
        ",,NH",
        "Munich, Bavaria, Germany"
    };

    public static void main(String... args) {
        long totalTime = 0;
        long parseTime = 0;
        StdLocale en = new StdLocale("en");

//        System.setProperty("solr.skip.warmup", "true");

        SolrService solrService = SolrManager.localEmbeddedService();
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        // Throw away task to get things going ...
        PlaceRequestBuilder requestBldr = new PlaceRequestBuilder();
        requestBldr.setText("denver,co", en);
        PlaceRequest request = requestBldr.getRequest();
        placeService.requestPlaces(request);

        // Do an interpretation on all of the places
        for (int i=0;  i<2;  i++) {
            for (String text : textToInterpret) {
                long beginTime = System.nanoTime();
                requestBldr = placeService.createRequestBuilder(text, en);
                requestBldr.setFilterResults(false).setShouldCollectMetrics(true);
                PlaceResults results = placeService.requestPlaces(requestBldr.getRequest());

                long execTime = System.nanoTime() - beginTime;
                totalTime += execTime;

                System.out.println("|" + text + "|" + execTime);
                try { Thread.sleep(15L); } catch (Exception ex) { }
            }
        }

        System.gc();
        System.out.println("|Total Time|" + totalTime);
        long avgTime = totalTime / textToInterpret.length;
        System.out.println("|Average Time (nano)|" + avgTime);
        System.out.println("|Used Memory|" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024));
        System.out.println("|Average parse time|" + (parseTime / textToInterpret.length));

        DataMetrics metrics = placeService.getProfile().getDataService().getMetrics();
        for (String metricName: metrics.nameIterator()) {
            System.out.println(metricName + "=" + metrics.getNamedMetric(metricName).getValue());
        }

        System.exit(0);
    }
}
