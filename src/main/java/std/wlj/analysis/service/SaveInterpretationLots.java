package std.wlj.analysis.service;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.familysearch.standards.analysis.model.InterpretationModel;
import org.familysearch.standards.analysis.model.RootModel;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRequest;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.PlaceRequestProfile;
import org.familysearch.standards.place.ws.util.POJOMarshalUtil;

import std.wlj.util.SolrManager;
import std.wlj.ws.rawhttp.HttpHelperAnalysis;


public class SaveInterpretationLots {

    static URL    svcUrl = null;
    static Random random = new Random();
    private static ContentType contentType = ContentType.create(RootModel.APPLICATION_XML_PLACES, "UTF-8");

    /** Sample data for interpretation ... */
    private static String[] textes = {
        "heinolan mlk, mikkeli, finland",
        "アルゼンチン Argentink",
    };
    public static void main(String... args) throws Exception {
        HttpHelperAnalysis.doVerbose    = true;
        HttpHelperAnalysis.acceptType   = "application/standards-analysis-v2+xml";
        HttpHelperAnalysis.contentType  = "application/standards-analysis-v2+xml";

        svcUrl = new URL("http://localhost:8080/std-ws-analysis/interpretation");
//        svcUrl = new URL("https://place-ws-dev.dev.fsglobal.org/int-std-ws-analysis/interpretation");
        System.out.println("The URL: " + svcUrl);

        List<String> placeNames = Files.readAllLines(Paths.get("C:/temp/places-search-text.txt"), Charset.forName("UTF-8"));
        System.out.println("PlaceNames.count=" + placeNames.size());

        SolrService  solrService = SolrManager.localEmbeddedService("D:/solr/stand-alone-6.5.0");
        PlaceRequestProfile profile = new DefaultPlaceRequestProfile("default", solrService, null);
        PlaceService placeService = new PlaceService(profile);

        long total01 = 0L;
        long total02 = 0L;
        long total03 = 0L;
        PlaceResultsMapper mapper = new PlaceResultsMapper();
        for (int cnt=0;  cnt<1200;  cnt++) {
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
                
                InterpretationModel interpModel = mapper.mapToModel(request, results, null, StdLocale.ENGLISH);
                RootModel rootModel = new RootModel();
                rootModel.setInterpretation(interpModel);
                long time2 = System.nanoTime();
                System.out.println("IM: " + interpModel.getResult().getTotalRepCount());
                System.out.println(" C: " + interpModel.getResult().getResultReps());
                HttpHelperAnalysis.doPOST(svcUrl, rootModel);

                // POST the request, but don't show any concern about the response
                try(CloseableHttpClient client = HttpClients.createDefault()) {
                    HttpPost httpPost = new HttpPost(String.valueOf(svcUrl));
                    StringEntity entity = new StringEntity(POJOMarshalUtil.toXML(rootModel), contentType);
                    httpPost.setEntity(entity);
                    client.execute(httpPost);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                long time3 = System.nanoTime();

                total01 += (time1 - time0);
                total02 += (time2 - time1);
                total03 += (time3 - time2);
            } catch(Exception ex) {
                System.out.println(">>> EXCEPTION for: " + text + " --> " + ex.getClass().getName() + " . " + ex.getMessage());
            }
        }

        System.out.println("TOTAL01: " + (total01 / 1_000_000.0D));
        System.out.println("TOTAL02: " + (total02 / 1_000_000.0D));
        System.out.println("TOTAL03: " + (total03 / 1_000_000.0D));

        solrService.shutdown();
        System.exit(0);
    }
}
