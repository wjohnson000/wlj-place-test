package std.wlj.solr;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;

import std.wlj.util.SolrManager;

public class GetInterpretationLotsThreads {

    static Random random = new Random();

    public static void main(String... args) throws Exception {
        List<String> placeNames = Files.readAllLines(Paths.get("C:/temp/places-search-text.txt"), StandardCharsets.UTF_8);
        System.out.println("PlaceNames.count=" + placeNames.size());

        SolrService  solrService = SolrManager.localEmbeddedService("D:/solr/stand-alone-6.5.0");
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        long time00 = System.nanoTime();
        List<String> iSpyWaldo = new ArrayList<>();
        int[] incrs = { 223, 334, 445, 556, 667, 778 };
        Thread[] threads = new Thread[incrs.length];
        for (int ndx=0;  ndx<incrs.length;  ndx++) {
            final int tNdx = ndx;
            threads[ndx] = new Thread(() -> {
                List<String> interps = doInterps(placeService, placeNames, incrs[tNdx]);
                iSpyWaldo.addAll(interps);
            });
            threads[ndx].start();
        }

        boolean isAlive = true;
        while (isAlive) {
            isAlive = Arrays.stream(threads).anyMatch(thr -> thr.isAlive());
            try { Thread.sleep(1000L); } catch(Exception ex) { }
        }
        long time01 = System.nanoTime();
        System.out.println("Clock time: " + ((time01 - time00) / 1_000_000.0));

        Files.write(Paths.get("C:/temp/place-search-new.txt"), iSpyWaldo, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        solrService.shutdown();
        System.exit(0);
    }

    static List<String> doInterps(PlaceService placeService, List<String> placeNames, int incr) {
        List<String> iSpyWaldo = new ArrayList<>();
        for (int cnt=0;  cnt<placeNames.size();  cnt+=incr) {
            String text = placeNames.get(cnt);
            text = text.replace('"', ' ').trim();
            if (text.trim().isEmpty()) continue;

            System.out.println(">>> Search for: " + text);

            try {
                PlaceRequestBuilder builder = placeService.createRequestBuilder(text, StdLocale.ENGLISH);
                builder.setShouldCollectMetrics(true);
                builder.setFilterResults(false);
                
                PlaceRequest request = builder.getRequest();
                PlaceResults results = placeService.requestPlaces(request);

                StringBuilder buff = new StringBuilder();
                buff.append(text);
                buff.append("|").append(results.getPlaceRepresentations().length);
                Arrays.stream(results.getPlaceRepresentations()).forEach(pr -> buff.append("|").append(pr.getId()));
                iSpyWaldo.add(buff.toString());
            } catch(Exception ex) {
                System.out.println(">>> EXCEPTION for: " + text + " --> " + ex.getClass().getName() + " . " + ex.getMessage());
            }
        }

        return iSpyWaldo;
    }
}
