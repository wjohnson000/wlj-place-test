/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.Metrics;
import org.familysearch.standards.place.Metrics.MapNumberMetric;
import org.familysearch.standards.place.Metrics.MapObjectMetric;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;
import org.familysearch.standards.place.util.PlaceHelper;

import std.wlj.util.SolrManager;

/**
 * @author wjohnson000
 *
 */
public class MultiThreadDifferentResultsDetails {

    static final int condenseBy = 10;
    static final Object object = new Object();
    static final Random random = new Random();
    static final Map<String, Map<String, String>> resultDetails = new HashMap<>();


    public static void main(String...args) throws IOException {
        List<String> placeNames = Files.readAllLines(Paths.get("C:/D-drive/request-splunk/place-search-text.txt"), StandardCharsets.UTF_8);
        System.out.println("PlaceNames.count=" + placeNames.size());

        SolrService  solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.1.0");
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        try {
            ExecutorService exSvc = Executors.newFixedThreadPool(30);
            for (int i=0;  i<25;  i++) {
                exSvc.submit(new Runnable() {
                    @Override public void run() {
                        try { Thread.sleep(200L); } catch(Exception ex) { }
                        doStuff(placeService, placeNames);
                    }
                });
            }

            exSvc.shutdown();
            System.out.println("SHUT? " + exSvc.isShutdown());
            System.out.println("TERM? " + exSvc.isTerminated());

            exSvc.awaitTermination(30, TimeUnit.MINUTES);
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            System.out.println("Shutting down ...");
        }

        System.out.println("\n\n\n>>> Interps with more than one result ...");
        resultDetails.entrySet().stream()
            .filter(entry -> entry.getValue().size() > 1)
            .forEach(entry -> {
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                entry.getValue().entrySet().forEach(System.out::println);
            });

        solrService.shutdown();
        System.exit(0);
    }

    static void doStuff(PlaceService service, List<String> placeNames) {
        for (int cnt=0;  cnt<2500;  cnt++) {
            if (cnt % 100 == 0) {
                System.out.println(">>> " + Thread.currentThread().getName() + " :: " + cnt);
            }
            int ndx = random.nextInt(placeNames.size());
            ndx = ndx / condenseBy * condenseBy;
            String text = placeNames.get(random.nextInt(placeNames.size()));
            text = text.replace('"', ' ').trim();
            interp(service, text);
        }
    }

    static void interp(PlaceService service, String textAndLang) {
        String[] chunks = PlaceHelper.split(textAndLang, '|');
        String text = chunks[0];
        StdLocale locale = (chunks.length == 1) ? StdLocale.ENGLISH : new StdLocale(chunks[1]);
        PlaceRequestBuilder builder = service.createRequestBuilder(text, locale);
        builder.setShouldCollectMetrics(true);
        builder.setFilterResults(false);
        
        PlaceRequest request = builder.getRequest();
        PlaceResults results = service.requestPlaces(request);
        addResults(textAndLang, results);
    }

    static void addResults(String textAndLang, PlaceResults results) {
        PlaceRepresentation[] reps = results.getPlaceRepresentations();
        String value = Arrays.stream(reps)
            .map(rep -> String.valueOf(rep.getId()))
            .collect(Collectors.joining("|", String.valueOf(reps.length), ""));

        Map<String, String> resultDetail = resultDetails.get(textAndLang);
        if (resultDetail == null) {
            resultDetail = new HashMap<>();
            resultDetails.put(textAndLang, resultDetail);
        }
        resultDetail.put(value, formatResults(textAndLang, results));
        if (resultDetail.size() > 1) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println(textAndLang);
            resultDetail.values().forEach(System.out::println);
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        }
    }
    
    static String formatResults(String textAndLang, PlaceResults results) {
        StringBuilder buff = new StringBuilder(16 * 1024);
        buff.append(textAndLang);

        buff.append("\n    f-cnt: ").append(results.getFoundCount());
        buff.append("\n    r-cnt: ").append(results.getReturnedCount());
        Arrays.stream(results.getPlaceRepresentations()).forEach(rep -> buff.append("\n      rep: ").append(rep.getId()));
        buff.append("\n    annot: ");
        results.getAnnotations().forEachRemaining(ann -> buff.append(ann).append("  "));
        buff.append("\n");

        Metrics metrics = results.getMetrics();
        metrics.getSimpleNumberMetricSet().forEach(mm -> buff.append("\n  Number: ").append(mm.getName()).append(" --> ").append(metrics.getSimpleNumberMetric(mm)));
        metrics.getSimpleStringMetricSet().forEach(mm -> buff.append("\n  String: ").append(mm.getName()).append(" --> ").append(metrics.getSimpleStringMetric(mm)));
        for (MapNumberMetric mnm : metrics.getMapNumberMetricSet()) {
            buff.append("\n  NumMap: ").append(mnm.getName());
            for (String met : metrics.getMapNumberMetricSet(mnm)) {
                buff.append("\n     Met: ").append(met).append(" --> ").append(metrics.getMapNumberMetric(mnm, met));
            }
        }
        for (MapObjectMetric mom : metrics.getMapObjectMetricSet()) {
            buff.append("\n  ObjMap: ").append(mom.getName());
            for (Object obj : metrics.getMapObjectMetricSet(mom)) {
                buff.append("\n     Met: ").append(obj).append(" --> ").append(metrics.getMapObjectMetric(mom, obj));
            }
        }
        buff.append("\n");
        return buff.toString();
    }
}
