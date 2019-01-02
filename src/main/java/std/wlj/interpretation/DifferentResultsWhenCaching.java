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
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;
import org.familysearch.standards.place.util.PlaceHelper;

import std.wlj.util.SolrManager;

/**
 * Try to duplicate the issue where different results are returned for the same interpretation
 * when submitted twice.  Presumably the only difference will be because of caching ...
 * 
 * @author wjohnson000
 *
 */
public class DifferentResultsWhenCaching {

    static final int    SKIP_COUNT  = 137;
    static final Long   LONG_ZERO   = 0L;

    static final String BASE_DIR    = "C:/D-drive/important";
    static final String INTERP_FILE = "place-search-text.txt";

    static final List<String> badBad = new ArrayList<>();

    public static void main(String... args) throws PlaceDataException, IOException {
//        System.setProperty("WARM_CACHE_EXPIRE_TIME", "100"); 
//        System.setProperty("L1_CACHE_EXPIRE_TIME", "60"); 
//        System.setProperty("L2_CACHE_EXPIRE_TIME", "30"); 
//        System.setProperty("L3_CACHE_EXPIRE_TIME", "20"); 
//
        System.setProperty("enable.interp.cache", "true");
        System.setProperty("enable.solr.cache", "true");
        System.setProperty("enable.repid.chain.cache", "true");

        List<String> names = Files.readAllLines(Paths.get(BASE_DIR, INTERP_FILE), StandardCharsets.UTF_8);

        SolrService  solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.1.0");
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        // Seed the process ...
        doIt(placeService, 0, "en", "Provo, UT");

        Thread[] threads = new Thread[12];
        for (int ndx=0;  ndx<threads.length;  ndx++) {
            final int startNdx  = ndx;
            threads[ndx] = new Thread(() -> doIt(placeService, names, startNdx), "THR-" + ndx);
            threads[ndx].start();
        }

        boolean active = true;
        while (active) {
            try { Thread.sleep(2000L); } catch(Exception ex) { }
            active = Arrays.stream(threads).anyMatch(Thread::isAlive);
        }

        System.out.println();
        System.out.println("===========================================================================================");
        badBad.forEach(System.out::println);
        solrService.shutdown();
        System.exit(0);
    }

    static void doIt(PlaceService placeService, List<String> names, int startNdx) {
        for (int ndx=startNdx;  ndx<names.size();  ndx+=SKIP_COUNT) {
            String[] nameData = PlaceHelper.split(names.get(ndx), '|');
            doIt(placeService, ndx, nameData[1], nameData[0]);
        }
    }

    static void doIt(PlaceService placeService, int ndx, String locale, String name) {
        try {
            PlaceRequestBuilder builder01 = placeService.createRequestBuilder(name, new StdLocale(locale));
            builder01.setShouldCollectMetrics(true);
            builder01.setFilterResults(false);

            PlaceRequest request01 = builder01.getRequest();
            PlaceResults results01 = placeService.requestPlaces(request01);
            PlaceRepresentation[] placeReps01 = results01.getPlaceRepresentations();
            String abbr01 = Arrays.stream(placeReps01).map(rep -> String.valueOf(rep.getId())).collect(Collectors.joining(", ", "[", "]"));

            PlaceRequestBuilder builder02 = placeService.createRequestBuilder(name.replaceAll(" ", "  "), new StdLocale(locale));
            builder02.setShouldCollectMetrics(true);
            builder02.setFilterResults(false);

            PlaceRequest request02 = builder02.getRequest();
            PlaceResults results02 = placeService.requestPlaces(request02);
            PlaceRepresentation[] placeReps02 = results02.getPlaceRepresentations();
            String abbr02 = Arrays.stream(placeReps02).map(rep -> String.valueOf(rep.getId())).collect(Collectors.joining(", ", "[", "]"));

            System.out.println(Thread.currentThread().getName() + "." + ndx + " --> [" + name + ", " + locale + "]");
            System.out.println(Thread.currentThread().getName() + "       " + abbr01);
            System.out.println(Thread.currentThread().getName() + "       " + abbr02);

            if (! abbr01.equals(abbr02)) {
                badBad.add("");
                badBad.add(ndx + " --> [" + name + ", " + locale + "]");
                badBad.add("      " + results01.getFoundCount() + " --> " + abbr01);
                badBad.add("      " + results02.getFoundCount() + " --> " + abbr02);
            }
        } catch(Exception ex) {
            System.out.println("Exception for " + name + " --> " + ex.getMessage());
        }
    }

}
