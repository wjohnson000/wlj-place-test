package std.wlj.interpretation;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.Metrics;
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
 * Based on the "C:\D-drive\important\place-search-text.txt" file, which contain details from about
 * 2M interpretation requests, run a bunch of them through the interpretation engine and see which
 * ones were processed by the "FullParsePathSearch" engine.
 * 
 * @author wjohnson000
 *
 */
public class TestFullParsePathSearch {

    static final int    SKIP_COUNT  = 23;
    static final Long   LONG_ZERO   = 0L;

    static final String BASE_DIR    = "C:/D-drive/important";
    static final String INTERP_FILE = "place-search-text.txt";

    static Map<String, long[]> hitsParse    = new TreeMap<>();
    static Map<String, long[]> missesParse  = new TreeMap<>();
    static Map<String, long[]> ignoresParse = new TreeMap<>();

    public static void main(String... args) throws PlaceDataException, IOException {
        SolrService  solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.1.0");
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        List<String> names = Files.readAllLines(Paths.get(BASE_DIR, INTERP_FILE), Charset.forName("UTF-8"));
        System.out.println("Name-Count: " + names.size());

        for (int ndx=0;  ndx<names.size();  ndx+=SKIP_COUNT) {
            String[] nameData = PlaceHelper.split(names.get(ndx), '|');
            System.out.println(ndx + " --> " + Arrays.toString(nameData));

            try {
                PlaceRequestBuilder builder = placeService.createRequestBuilder(nameData[0], new StdLocale(nameData[1]));
                builder.setShouldCollectMetrics(true);
                builder.setFilterResults(false);

                PlaceRequest request = builder.getRequest();
                PlaceResults results = placeService.requestPlaces(request);
                absorbResults(nameData[0], results);
            } catch(Exception ex) {
                System.out.println("Exception for " + names.get(ndx) + " --> " + ex.getMessage());
            }
        }

        ignoresParse.entrySet().forEach(entry -> System.out.println("IGN|" + entry.getKey() + "|" + entry.getValue()[0]));

        System.out.println("\n\n");
        missesParse.entrySet().forEach(entry -> System.out.println("MSS|" + entry.getKey() + "|" + entry.getValue()[0]));

        System.out.println("\n\n");
        hitsParse.entrySet().forEach(entry -> System.out.println("HIT|" + entry.getKey() + "|" + entry.getValue()[0]));

        System.out.println("\n\n");
        long total;
        total = ignoresParse.values().stream().mapToLong(val -> val[1]).sum();
        System.out.println("IGN.count: " + ignoresParse.size() + " || avg: " + (total / ignoresParse.size()) / 1_000_000.0);
        total = missesParse.values().stream().mapToLong(val -> val[1]).sum();
        System.out.println("MSS.avg: " + missesParse.size() + " || avg: " + (total / missesParse.size()) / 1_000_000.0);
        total = hitsParse.values().stream().mapToLong(val -> val[1]).sum();
        System.out.println("HIT.avg: " + hitsParse.size() + " || avg: "  + (total / hitsParse.size()) / 1_000_000.0);

        solrService.shutdown();
        System.exit(0);
    }

    static void absorbResults(String text, PlaceResults results) {
        Long time = results.getMetrics().getSimpleNumberMetric(Metrics.SimpleNumberMetric.FULL_PARSE_PATH_TIME);
        Long count = results.getMetrics().getSimpleNumberMetric(Metrics.SimpleNumberMetric.FULL_PARSE_PATH_FOUND_COUNT);
        Long total = results.getMetrics().getSimpleNumberMetric(Metrics.SimpleNumberMetric.REQUEST_TOTAL_TIME);
        if (total == null) {
            total = LONG_ZERO;
        }

        if (time == null) {
            ignoresParse.put(text, new long[] { 0L, total.longValue() });
        } else if (count == null  ||  count.longValue() == 0L) {
            missesParse.put(text, new long[] { time, total.longValue() });
        } else {
            hitsParse.put(text, new long[] { time, total.longValue() });
        }
    }
}
