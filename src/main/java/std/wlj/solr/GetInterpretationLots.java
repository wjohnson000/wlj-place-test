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


public class GetInterpretationLots {

    static Random random = new Random();

    public static void main(String... args) throws Exception {
        List<String> placeNames = Files.readAllLines(Paths.get("C:/temp/places-search-text.txt"), StandardCharsets.UTF_8);
        System.out.println("PlaceNames.count=" + placeNames.size());

        SolrService  solrService = SolrManager.localEmbeddedService("D:/solr/standalone-7.1.0");
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        long total01 = 0L;
        List<String> iSpyWaldo = new ArrayList<>();
        for (int cnt=0;  cnt<placeNames.size();  cnt+=11) {
            String text = placeNames.get(cnt);
            text = text.replace('"', ' ').trim();
            if (text.trim().isEmpty()) continue;

            System.out.println(">>> Search for: " + text);

            try {
                PlaceRequestBuilder builder = placeService.createRequestBuilder(text, StdLocale.ENGLISH);
                builder.setShouldCollectMetrics(true);
                builder.setFilterResults(false);
                long time0 = System.nanoTime();
                
                PlaceRequest request = builder.getRequest();
                PlaceResults results = placeService.requestPlaces(request);
                long time1 = System.nanoTime();

                StringBuilder buff = new StringBuilder();
                buff.append(text);
                buff.append("|").append(results.getPlaceRepresentations().length);
                Arrays.stream(results.getPlaceRepresentations()).forEach(pr -> buff.append("|").append(pr.getId()));
                iSpyWaldo.add(buff.toString());

                total01 += (time1 - time0);
            } catch(Exception ex) {
                System.out.println(">>> EXCEPTION for: " + text + " --> " + ex.getClass().getName() + " . " + ex.getMessage());
            }
        }

        System.out.println("TOTAL01: " + (total01 / 1_000_000.0D));
        Files.write(Paths.get("C:/temp/place-search-new.txt"), iSpyWaldo, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        solrService.shutdown();
        System.exit(0);
    }
}
