package std.wlj.interpretation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.core.parse.Parser.ParseResults;
import org.familysearch.standards.place.Metrics;
import org.familysearch.standards.place.Metrics.MapNumberMetric;
import org.familysearch.standards.place.Metrics.MapObjectMetric;
import org.familysearch.standards.place.Metrics.SimpleNumberMetric;
import org.familysearch.standards.place.Metrics.SimpleStringMetric;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceResults.Annotation;
import org.familysearch.standards.place.PlaceResults.Attribute;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;
import org.familysearch.standards.place.search.parser.ParsePath;

import std.wlj.util.SolrManager;

/**
 * Try to duplicate the issue where different results are returned for the same interpretation
 * when submitted twice.  Presumably the only difference will be because of caching ...
 * 
 * @author wjohnson000
 *
 */
public class DifferentResultsWhenCachingSmall {

    static final int    SKIP_COUNT  = 1;
    static final Long   LONG_ZERO   = 0L;

    static final String BASE_DIR    = "C:/temp";
    static final String INTERP_FILE = "dup-interpretation-results.txt";

    static PlaceResults misResult01 = null;
    static PlaceResults misResult02 = null;
    
    public static void main(String... args) throws PlaceDataException, IOException {
        System.setProperty("WARM_CACHE_EXPIRE_TIME", "100"); 
        System.setProperty("L1_CACHE_EXPIRE_TIME", "60"); 
        System.setProperty("L2_CACHE_EXPIRE_TIME", "30"); 
        System.setProperty("L3_CACHE_EXPIRE_TIME", "20"); 
//
        System.setProperty("enable.interp.cache", "false");
        System.setProperty("enable.solr.cache", "true");
        System.setProperty("enable.repid.chain.cache", "false");

        System.setProperty("alt-rep.timeout.enforce", "false");

        List<String> names = Files.readAllLines(Paths.get(BASE_DIR, INTERP_FILE), StandardCharsets.UTF_8);

        SolrService  solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.1.0");
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        // Seed the process ...
        doIt(placeService, 0, "en", "Provo, UT");

        Thread[] threads = new Thread[6];
        for (int ndx=0;  ndx<threads.length;  ndx++) {
            final int startNdx  = ndx;
            threads[ndx] = new Thread(() -> doIt(placeService, names, startNdx), "THR-" + ndx);
            threads[ndx].start();
        }

        boolean active = true;
        while (active) {
            try { Thread.sleep(200L); } catch(Exception ex) { }
            active = Arrays.stream(threads).anyMatch(Thread::isAlive);
        }

        showDetail(misResult01);
        showDetail(misResult02);
        solrService.shutdown();
        System.exit(0);
    }

    static void doIt(PlaceService placeService, List<String> names, int startNdx) {
        for (int ndx=startNdx;  ndx<names.size();  ndx+=SKIP_COUNT) {
            doIt(placeService, ndx, "en", names.get(ndx));
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

            PlaceRequestBuilder builder02 = placeService.createRequestBuilder(name, new StdLocale(locale));
            builder02.setShouldCollectMetrics(true);
            builder02.setFilterResults(false);

            PlaceRequest request02 = builder01.getRequest();
            PlaceResults results02 = placeService.requestPlaces(request02);
            PlaceRepresentation[] placeReps02 = results02.getPlaceRepresentations();
            String abbr02 = Arrays.stream(placeReps02).map(rep -> String.valueOf(rep.getId())).collect(Collectors.joining(", ", "[", "]"));

            System.out.println(Thread.currentThread().getName() + "." + ndx + " --> [" + name + ", " + locale + "]");
            System.out.println(Thread.currentThread().getName() + "       " + abbr01);
            System.out.println(Thread.currentThread().getName() + "       " + abbr02);

            synchronized(BASE_DIR) {
                if (! abbr01.equals(abbr02)  &&  misResult01 == null) {
                    misResult01 = results01;
                    misResult02 = results02;
                }
            }
        } catch(Exception ex) {
            System.out.println("Exception for " + name + " --> " + ex.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    static void showDetail(PlaceResults results) {
        if (results == null) {
            return;
        }
        PlaceRepresentation[] placeReps = results.getPlaceRepresentations();
        String abbr = Arrays.stream(placeReps).map(rep -> String.valueOf(rep.getId())).collect(Collectors.joining(", ", "[", "]"));

        Iterator<Annotation> annIter = results.getAnnotations();
        Object parsePathsO = results.getAttr(Attribute.PARSE_PATHS);
        Object parseDataO  = results.getAttr(Attribute.PARSE_DATA);
        Metrics metrics = results.getMetrics();

        System.out.println();
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(abbr);
        annIter.forEachRemaining(ann -> System.out.println("  ANN: " + ann.getCode() + ":" + ann.getName()));

        if (parsePathsO instanceof List) {
            List<ParsePath> pps = (List<ParsePath>)parsePathsO;
            for (ParsePath pp : pps) {
                System.out.println("   PRS-PTH: " + pp);
            }
        }

        if (parseDataO instanceof ParseResults) {
            ParseResults pr = (ParseResults)parseDataO;
            System.out.println("   THE-TXT: " + pr.getText().getLocalizedStr().get());
            pr.getPreSegments().forEach(xx -> System.out.println("   PRE-SEG: " + xx));
            pr.getSegments().forEach(xx -> System.out.println("   SEGMENT: " + xx));
            pr.getPostSegments().forEach(xx -> System.out.println("   PST-SEG: " + xx));
            pr.getPreParsedObjects().forEach(xx -> System.out.println("   PRE-PRS: " + xx));
            pr.getParsedObjects().forEach(xx -> System.out.println("   PPARSED: " + xx));
        }

        Set<String> stuff = new TreeSet<>();
        for (SimpleNumberMetric xxx : metrics.getSimpleNumberMetricSet()) {
            stuff.add(xxx.getName() + " --> " + metrics.getSimpleNumberMetric(xxx));
        }
        stuff.forEach(stf -> System.out.println("   NUM-MET: " + stf));

        stuff = new TreeSet<>();
        for (SimpleStringMetric xxx : metrics.getSimpleStringMetricSet()) {
            stuff.add(xxx.getName() + " --> " + metrics.getSimpleStringMetric(xxx));
        }
        stuff.forEach(stf -> System.out.println("   STR-MET: " + stf));

        for (MapNumberMetric xxx : metrics.getMapNumberMetricSet()) {
            stuff = new TreeSet<>();
            for (String yyy : metrics.getMapNumberMetricSet(xxx)) {
                stuff.add(xxx.getName() + " --> " + yyy + "=" + metrics.getMapNumberMetric(xxx, yyy));
            }
            stuff.forEach(stf -> System.out.println("   MAP-NUM: " + stf));
        }

        for (MapObjectMetric xxx : metrics.getMapObjectMetricSet()) {
            stuff = new TreeSet<>();
            for (Object yyy : metrics.getMapObjectMetricSet(xxx)) {
                stuff.add(xxx.getName() + " --> " + yyy + "=" + metrics.getMapObjectMetric(xxx, yyy));
            }
            stuff.forEach(stf -> System.out.println("   MAP-OBJ: " + stf));
        }
    }
}
