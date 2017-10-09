package std.wlj.solr;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import std.wlj.ws.rawhttp.HttpHelperAnalysis;


public class GetInterpretationLots {

    static Random random = new Random();

    /** Sample data for interpretation ... */
    private static String[] textes = {
        "heinolan mlk, mikkeli, finland",
        "アルゼンチン Argentink",
    };
    public static void main(String... args) throws Exception {
        HttpHelperAnalysis.doVerbose    = true;
        HttpHelperAnalysis.acceptType   = "application/standards-analysis-v2+xml";
        HttpHelperAnalysis.contentType  = "application/standards-analysis-v2+xml";

        List<String> placeNames = Files.readAllLines(Paths.get("C:/temp/places-search-text.txt"), Charset.forName("UTF-8"));
        System.out.println("PlaceNames.count=" + placeNames.size());

        SolrService  solrService = SolrManager.localEmbeddedService("D:/solr/stand-alone-6.5.0");
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        long total01 = 0L;
        for (int cnt=0;  cnt<12000;  cnt++) {
            String text = placeNames.get(random.nextInt(placeNames.size()));
            text = text.replace('"', ' ').trim();
            if (cnt == 0) {
                text = ", Delaware, Delaware, United States";
            } else if (cnt < textes.length) {
                text = textes[cnt];
            }
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

                total01 += (time1 - time0);
            } catch(Exception ex) {
                System.out.println(">>> EXCEPTION for: " + text + " --> " + ex.getClass().getName() + " . " + ex.getMessage());
            }
        }

        System.out.println("TOTAL01: " + (total01 / 1_000_000.0D));

        solrService.shutdown();
        System.exit(0);
    }
}
