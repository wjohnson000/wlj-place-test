package std.wlj.interpretation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
import org.familysearch.standards.place.util.PlaceHelper;

import std.wlj.util.SolrManager;

/**
 * Based on the "C:\D-drive\important\place-search-text.txt" file, which contain details from about
 * 2M interpretation requests, run a bunch of them through the interpretation engine and see which
 * ones were processed by the "FullParsePathSearch" engine.
 * 
 * @author wjohnson000
 *
 */
public class TestCaching {

    static final int    SKIP_COUNT  = 973;
    static final Long   LONG_ZERO   = 0L;

    static final String BASE_DIR    = "C:/D-drive/important";
    static final String INTERP_FILE = "place-search-text.txt";

    static final List<String> metrics = new ArrayList<>();

    public static void main(String... args) throws PlaceDataException, IOException {
        System.setProperty("WARM_CACHE_EXPIRE_TIME", "10"); 
        System.setProperty("L1_CACHE_EXPIRE_TIME", "2"); 
        System.setProperty("L2_CACHE_EXPIRE_TIME", "2"); 
        System.setProperty("L3_CACHE_EXPIRE_TIME", "2"); 

        System.setProperty("enable.interp.cache", "false");
        System.setProperty("enable.solr.cache", "false");
        System.setProperty("enable.repid.chain.cache", "false");

        SolrService  solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.1.0");
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        // Seed the process
//        long timeAA = System.nanoTime();
//        doIt(placeService, 0, "en", "Provo, UT");
//        long timeBB = System.nanoTime();

        List<String> names = Files.readAllLines(Paths.get(BASE_DIR, INTERP_FILE), StandardCharsets.UTF_8);
        System.out.println("Name-Count: " + names.size());

        long time00 = System.nanoTime();
        Thread[] threads = new Thread[10];
        for (int ndx=0;  ndx<threads.length;  ndx++) {
            final int skipCount = SKIP_COUNT + ndx + ndx;
            threads[ndx] = new Thread(() -> doIt(placeService, names, skipCount), "THR-" + ndx);
            threads[ndx].start();
        }

        boolean active = true;
        while (active) {
            try { Thread.sleep(2000L); } catch(Exception ex) { }
            active = Arrays.stream(threads).anyMatch(Thread::isAlive);
        }
        long time01 = System.nanoTime();
        metrics.add("");
        metrics.add("TimeALL: " + (time01 - time00) / 1_000_000.0);

//        System.out.println("Start-up.time=" + (timeBB-timeAA) / 1_000_000.0);
//        metrics.forEach(System.out::println);

        solrService.shutdown();
        System.exit(0);
    }

    static void doIt(PlaceService placeService, List<String> names, int skipCount) {
        long time00 = System.nanoTime();

        for (int ndx=0;  ndx<names.size();  ndx+=skipCount) {
            String[] nameData = PlaceHelper.split(names.get(ndx), '|');
            doIt(placeService, ndx, nameData[1], nameData[0]);
        }

        long time01 = System.nanoTime();
        metrics.add("");
        metrics.add(Thread.currentThread().getName() + ".Time: " + (time01 - time00) / 1_000_000.0);
    }

    static void doIt(PlaceService placeService, int ndx, String locale, String name) {
        try {
            PlaceRequestBuilder builder = placeService.createRequestBuilder(name, new StdLocale(locale));
            builder.setShouldCollectMetrics(true);
            builder.setFilterResults(false);

            long timeAA = System.nanoTime();
            PlaceRequest request = builder.getRequest();
            PlaceResults results = placeService.requestPlaces(request);
            PlaceRepresentation[] placeReps = results.getPlaceRepresentations();
            long timeBB = System.nanoTime();

            System.out.println(ndx + " --> [" + name + ", " + locale + "]");
            System.out.println("      " + name + " --> " + results.getFoundCount() + " --> " + Arrays.stream(placeReps).map(rep -> String.valueOf(rep.getId())).collect(Collectors.joining(", ", "[", "]")));
            System.out.println("      Time=" + (timeBB - timeAA) / 1_000_000.0);
        } catch(Exception ex) {
            System.out.println("Exception for " + name + " --> " + ex.getMessage());
        }
    }
}
