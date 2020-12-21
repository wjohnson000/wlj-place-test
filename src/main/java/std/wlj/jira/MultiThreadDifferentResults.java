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
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.familysearch.standards.core.StdLocale;
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
public class MultiThreadDifferentResults {

    static final int condenseBy = 10;
    static final Object object = new Object();
    static final Random random = new Random();
    static final Map<String, String> resultMap = new HashMap<>();
    static final Set<String> resultMulti = new TreeSet<>();
    static final Set<String> resultBad = new TreeSet<>();

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

        solrService.shutdown();

        System.out.println("\n\n\n>>> Interps seen multiple times ...");
        resultMulti.forEach(System.out::println);

        System.out.println("\n\n\n>>> Interps with more than one result ...");
        resultBad.forEach(System.out::println);

        System.exit(0);
    }

    static void doStuff(PlaceService service, List<String> placeNames) {
        for (int cnt=0;  cnt<2500;  cnt++) {
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

        synchronized(object) {
            if (resultMap.containsKey(textAndLang)) {
                resultMulti.add(textAndLang);
                String currValue = resultMap.get(textAndLang);
                if (! currValue.equals(value)) {
                    resultBad.add(textAndLang);
                }
            } else {
                resultMap.put(textAndLang, value);
            }
        }
    }
}
