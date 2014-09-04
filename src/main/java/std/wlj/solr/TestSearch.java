package std.wlj.solr;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRequestBuilder;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.PlaceService;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.SolrDataService;
import org.familysearch.standards.place.search.RequestMetrics;


public class TestSearch {
    public static void main(String... args) throws PlaceDataException, IOException {
        System.setProperty("solr.solr.home", "C:/Tools/solr/data/tokoro");
        System.setProperty("solr.master.url", "");
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");

        SolrDataService solrService = new SolrDataService();
        PlaceService placeService = new PlaceService(solrService);

        FileSystem currFS = FileSystems.getDefault();
        Path temp = currFS.getPath("C:", "temp", "local-all.txt");
        List<String> textes = Files.readAllLines(temp, Charset.forName("UTF-8"));

        for (String text : textes) {
            PlaceRequestBuilder builder;
            builder = placeService.createRequestBuilder(text, StdLocale.ENGLISH);
            builder.setShouldCollectMetrics(true);

            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------");
            System.out.println(">>> Search for: " + text);
            long then = System.nanoTime();
            PlaceResults results = placeService.requestPlaces(builder.getRequest());
            long nnow = System.nanoTime();

            RequestMetrics metrics = results.getMetrics();
            System.out.println("    Metrics.TotalTime: " + metrics.getTotalTime() / 1000000.0);
            System.out.println("    Client.TotalTime: " + (nnow-then) / 1000000.0);
            System.out.println("    Place-Rep.Count: " + results.getPlaceRepresentations().length);
//            System.out.println("Metrics.Assembly: " + metrics.getAssemblyTime());
//            System.out.println("Metrics.FinalParsedInputTextCount: " + metrics.getFinalParsedInputTextCount());
//            System.out.println("Metrics.IdentifyCandidateLookupTime: " + metrics.getIdentifyCandidateLookupTime());
//            System.out.println("Metrics.IdentifyCandidateMaxHitFilterTime: " + metrics.getIdentifyCandidateMaxHitFilterTime());
//            System.out.println("Metrics.IdentifyCandidatesTime: " + metrics.getIdentifyCandidatesTime());
//            System.out.println("Metrics.IdentifyCandidateTailMatchTime: " + metrics.getIdentifyCandidateTailMatchTime());
//            System.out.println("Metrics.InitialParsedInputTextCount: " + metrics.getInitialParsedInputTextCount());
//            System.out.println("Metrics.ParseTime: " + metrics.getParseTime());
//            System.out.println("Metrics.PreScoringCandidateCount: " + metrics.getPreScoringCandidateCount());
//            System.out.println("Metrics.RawCandidateCount: " + metrics.getRawCandidateCount());
//            System.out.println("Metrics.ThresholdScore: " + metrics.getThresholdScore());
//            System.out.println("Metrics.TokenSetCount: " + metrics.getTokenSetCount());
//            for (Scorer scorer : metrics.getTimedScorers()) {
//                System.out.println("Scorer." + scorer.getClass().getName() + ": " + metrics.getScorerTime(scorer));
//            }
//
//            for (PlaceRepresentation placeRep : results.getPlaceRepresentations()) {
//                System.out.println("Place-Rep: " + placeRep);
//            }
        }

        solrService.shutdown();
        System.exit(0);
    }
}
