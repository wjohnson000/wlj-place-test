package std.wlj.analysis.service;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;

import std.wlj.util.SolrManager;

public class TestAnalysisShim {

    static Random random = new Random();
    static HttpHeaders httpHeaders = new SimpleHttpHeaders();
    static ScheduledExecutorService fScheduler =  Executors.newScheduledThreadPool(50);
    static Map<String, double[]> threadStats = new TreeMap<>();

    /** Sample data for interpretation ... */
    private static String[] textes = {
        ", Delaware, Delaware, United States",
        "heinolan mlk, mikkeli, finland",
        "アルゼンチン Argentink",
    };

    public static void main(String...args) throws Exception {
        System.setProperty("enable.save.analytics", "true");
//        System.setProperty("analytics.base.url", "https://place-ws-dev.dev.fsglobal.org/int-std-ws-analysis/interpretation");
//        System.setProperty("analytics.base.url", "http://ws.analysis.std.cmn.dev.us-east-1.dev.fslocal.org/interpretation/");
        System.setProperty("analytics.base.url", "http://localhost:8080/std-ws-analysis/interpretation");
        
        SolrService  solrService = SolrManager.localEmbeddedService("D:/solr/stand-alone-6.5.0");
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);
        
        List<String> placeNames = Files.readAllLines(Paths.get("C:/temp/places-search-text.txt"), Charset.forName("UTF-8"));
        System.out.println("PlaceNames.count=" + placeNames.size());
        mainX(placeService, 1, placeNames);
        long time0 = System.nanoTime();
        for (int i=0;  i<20;  i++) {
            fScheduler.submit(() -> mainX(placeService, 50, placeNames));
        }
        fScheduler.shutdown();
        fScheduler.awaitTermination(1200, TimeUnit.SECONDS);
        long time1 = System.nanoTime();

        System.out.println("\n");
        threadStats.entrySet().forEach(ee -> System.out.println(ee.getKey() + " --> " + ee.getValue()[0] + " vs. " + ee.getValue()[1]));

        System.out.println();
        System.out.println("EX-TIME: " + (time1 - time0) / 1_000_000.0);

        System.out.println();
        System.out.println("IN-OUT: " + AnalysisShimSyncHttpPool.inOut.longValue());
        System.out.println("  OOPS: " + AnalysisShimSyncHttpPool.oopsCnt.longValue());
        System.out.println("  HTTP: " + AnalysisShimSyncHttpPool.clientCnt.longValue());
        System.out.println("  HTTP: " + AnalysisShimSyncHttpPool.clientHash.size());

        System.out.println();
        System.out.println("INIT stats ...");
        System.out.println("  CT=" + AnalysisShimSyncHttpPool.initStats.count);
        System.out.println("  MN=" + AnalysisShimSyncHttpPool.initStats.minXX/1_000_000);
        System.out.println("  MX=" + AnalysisShimSyncHttpPool.initStats.maxXX/1_000_000);
        System.out.println("  TT=" + AnalysisShimSyncHttpPool.initStats.total.longValue()/1_000_000);
        if (AnalysisShimSyncHttpPool.initStats.count.longValue() > 0L) {
            System.out.println("  AV=" + (AnalysisShimSyncHttpPool.initStats.total.longValue()/AnalysisShimSyncHttpPool.initStats.count.longValue())/1_000_000);
        }

        System.out.println();
        System.out.println("POST stats ...");
        System.out.println("  CT=" + AnalysisShimSyncHttpPool.postStats.count);
        System.out.println("  MN=" + AnalysisShimSyncHttpPool.postStats.minXX/1_000_000);
        System.out.println("  MX=" + AnalysisShimSyncHttpPool.postStats.maxXX/1_000_000);
        System.out.println("  TT=" + AnalysisShimSyncHttpPool.postStats.total.longValue()/1_000_000);
        if (AnalysisShimSyncHttpPool.postStats.count.longValue() > 0L) {
            System.out.println("  AV=" + (AnalysisShimSyncHttpPool.postStats.total.longValue()/AnalysisShimSyncHttpPool.postStats.count.longValue())/1_000_000);
        }

        placeService.shutdown();
        System.exit(0);
    }

    public static void mainX(PlaceService placeService, int execCount, List<String> placeNames) {
        long total01 = 0L;
        long total02 = 0L;
        for (int cnt=0;  cnt<execCount;  cnt++) {
            String text;
            if (cnt < textes.length) {
                text = textes[cnt];
            } else {
                text = placeNames.get(random.nextInt(placeNames.size()));;
            }
            text = text.replace('"', ' ').trim();
            System.out.println(">>> Search for: " + text);

            if (text == null  ||  text.trim().length() == 0) continue;

            try {
                PlaceRequestBuilder builder = placeService.createRequestBuilder(text, StdLocale.ENGLISH);
                builder.setShouldCollectMetrics(true);
                builder.setFilterResults(false);
                long time0 = System.nanoTime();

                PlaceRequest request = builder.getRequest();
                PlaceResults results = placeService.requestPlaces(request);
                long time1 = System.nanoTime();

                UriInfo uriInfo = new SimpleUriInfo(text);

                AnalysisShimSyncHttpPool.postResults(uriInfo, httpHeaders, results, StdLocale.ENGLISH);
                long time2 = System.nanoTime();

                total01 += (time1 - time0);
                total02 += (time2 - time1);
            } catch(Exception ex) {
                System.out.println(">>> EXCEPTION for: " + text + " --> " + ex.getClass().getName() + " . " + ex.getMessage());
//                ex.printStackTrace();
            }
        }

        try { Thread.sleep(2000L); } catch(Exception ex) { }

        double[] stats = { total01 / 1_000_000.0, total02 / 1_000_000.0 };
        threadStats.put(Thread.currentThread().getName(), stats);
    }
}
