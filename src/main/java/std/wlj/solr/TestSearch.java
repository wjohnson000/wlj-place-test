package std.wlj.solr;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.search.DefaultPlaceRequestProfile;
import org.familysearch.standards.place.search.RequestMetrics;


public class TestSearch {

    private static final double ONE_MILLION = 1000000.0;

    public static void main(String... args) throws PlaceDataException, IOException {
        System.setProperty("solr.solr.home", "C:/Tools/solr/data/tokoro");
        System.setProperty("solr.master.url", "");
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");

        SolrService  solrService = new SolrService();
//        PlaceService placeService = new PlaceService(solrService);
        PlaceService placeService = new PlaceService(new DefaultPlaceRequestProfile(solrService));

        Path temp = Paths.get("C:", "temp", "local-all-random.txt");
        List<String> textes = Files.readAllLines(temp, Charset.forName("UTF-8"));

        PrintWriter pwOut = new PrintWriter(new FileWriter(new File("C:/temp/search-via-place-service-xx01.txt")));
        Date startDate = new Date();

//        textes.clear();
//        textes.add("*Radford*");
//        textes.add("Pleasant");
//        textes.add("darlington, south carolina");
//        textes.add("san isidro, collpa de nor cinti, chuquisaca, bolivi");
//        textes.add("long island");
//        textes.add("snowflake, navajo, arizona, united states");
//        textes.add("yarmouth, yarmouth, yarmouth town, yarmouth, nova scotia, canada");
//        textes.add("san miguel san julian, valladolid, valladolid, spain");

//        textes.add("smithfield, rhode island");
//        textes.add("benton, alabama");
//        textes.add("lejona, vizcaya, spain");
//        textes.add("le mars, plymouth, iowa, united states");

        for (String text : textes) {
            PlaceRequestBuilder builder;
            builder = placeService.createRequestBuilder(text, StdLocale.ENGLISH);
            builder.setUseWildcards(true);
            builder.setShouldCollectMetrics(true);
            builder.setFilterResults(false);

            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println(">>> Search for: " + text);
            long then = System.nanoTime();
            PlaceResults results = placeService.requestPlaces(builder.getRequest());
            long nnow = System.nanoTime();

            RequestMetrics metrics = results.getMetrics();
            StringBuilder buff = new StringBuilder();
            buff.append(text);
            buff.append("|").append((nnow - then) / ONE_MILLION);
            buff.append("|").append(metrics.getTotalTime() / ONE_MILLION);
            buff.append("|").append(metrics.getIdentifyCandidateLookupTime() / ONE_MILLION);
            buff.append("|").append(metrics.getParseTime() / ONE_MILLION);
            buff.append("|").append(metrics.getScoringTime() / ONE_MILLION);
            buff.append("|-1");

            buff.append("|").append(results.getReturnedCount());
            for (PlaceRepresentation resultModel : results.getPlaceRepresentations()) {
                buff.append("|").append(resultModel.getId());
                System.out.println("    -- " + resultModel.getFullDisplayName(StdLocale.ENGLISH).get());
            }
            pwOut.println(buff.toString());
        }

        System.out.println("\n\n");
        System.out.println("Date: " + startDate);
        System.out.println("End:  " + new Date());

        pwOut.close();
        solrService.shutdown();
        System.exit(0);
    }
}
