package std.wlj.ws.rawhttp;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

import org.familysearch.standards.place.ws.model.HealthCheckModel;
import org.familysearch.standards.place.ws.model.MetricModel;
import org.familysearch.standards.place.ws.model.MetricsModel;
import org.familysearch.standards.place.ws.model.PlaceSearchResultModel;
import org.familysearch.standards.place.ws.model.PlaceSearchResultsModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.ScorerModel;


public class TestSearchMetrics41K {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
    private static String baseUrl = "http://place-ws-test.dev.fsglobal.org/int-std-ws-place/places";

    private static final double ONE_MILLION = 1000000.0;


    /**
     * Run a bunch of searches ...
     */
    public static void main(String[] args) throws Exception {
        String inFileName = "C:/temp/local-all-random.txt";
        String outFileName = "C:/temp/search-new-random.txt";
        String jsonFileDir = null;

        if (args.length < 2) {
            System.out.println("Usage: TestSearchMetrics41K <input-file-path> <output-file-path>");
        } else {
            inFileName = args[0];
            outFileName = args[1];
            if (args.length > 2) {
                jsonFileDir = args[2];
            }
        }

        System.out.println("Input: " + inFileName);
        System.out.println("Output: " + outFileName);
        System.out.println("JSON: " + jsonFileDir);

        FileSystem currFS = FileSystems.getDefault();
        Path temp = currFS.getPath(inFileName);
        List<String> textes = Files.readAllLines(temp, StandardCharsets.UTF_8);

        PrintWriter pwOut = new PrintWriter(new FileWriter(new File(outFileName)));

        textes.clear();
        textes.add("*Radford*");
        textes.add("Pleasant");
        textes.add("darlington, south carolina");
        textes.add("san isidro, collpa de nor cinti, chuquisaca, bolivi");
        textes.add("long island");
        textes.add("snowflake, navajo, arizona, united states");
        textes.add("yarmouth, yarmouth, yarmouth town, yarmouth, nova scotia, canada");
        textes.add("san miguel san julian, valladolid, valladolid, spain");
        textes.add("Roswell, Lincoln, New Mexiso, United States");

        int cnt = 0;
        Date startDate = new Date();
        for (String textx : textes) {
            cnt++;
            if (cnt % 1000 == 0) {
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                RootModel model = doHealthCheck();
                HealthCheckModel hcModel = model.getHealthCheck();
                StringBuilder buff = new StringBuilder("METRICS");
                for (MetricModel mm : hcModel.getMetrics()) {
                    buff.append("|").append(mm.getMetricName());
                    buff.append("=").append(mm.getMetricValue());
                }
                pwOut.println(buff.toString());
            }

            long time = System.nanoTime();
            RootModel model = doSearch(textx);
            time = System.nanoTime() - time;

            if (model == null  ||  model.getSearchResults() == null) {
                continue;
            }

            if (jsonFileDir != null) {
                Path json = currFS.getPath(jsonFileDir, cnt+".txt");
                Files.write(json, model.toJSON().getBytes(StandardCharsets.UTF_8));
            }
            PlaceSearchResultsModel resultsModel = model.getSearchResults().get(0);
            MetricsModel metrics = resultsModel.getMetrics();

            StringBuilder buff = new StringBuilder();
            buff.append(textx);
            buff.append("|").append(time / ONE_MILLION);
//            buff.append("|").append(metrics.getTimings().getTotalTime() / ONE_MILLION);
//            buff.append("|").append(metrics.getTimings().getIdentifyCandidatesLookupTime() / ONE_MILLION);
//            buff.append("|").append(metrics.getTimings().getParseTime() / ONE_MILLION);
//            buff.append("|").append(metrics.getTimings().getScoringTime() / ONE_MILLION);
            ScorerModel nvpScorer = null;
//            for (ScorerModel scorer : metrics.getScorers().getScorers()) {
//                if (scorer.getName().equals("NameVariantPriorityScorer")) {
//                    nvpScorer = scorer;
//                }
//            }
            if (nvpScorer == null) {
                buff.append("|-1");
            } else {
                buff.append("|").append(nvpScorer.getTime() / ONE_MILLION);
            }

            buff.append("|").append(resultsModel.getCount());
            for (PlaceSearchResultModel resultModel : resultsModel.getResults()) {
                buff.append("|").append(resultModel.getRep().getId());
            }
            pwOut.println(buff.toString());
        }

        RootModel model = doHealthCheck();
        HealthCheckModel hcModel = model.getHealthCheck();
        StringBuilder buff = new StringBuilder("METRICS");
        for (MetricModel mm : hcModel.getMetrics()) {
            buff.append("|").append(mm.getMetricName());
            buff.append("=").append(mm.getMetricValue());
        }
        pwOut.println(buff.toString());
        
        System.out.println("\n\n");
        System.out.println("Date: " + startDate);
        System.out.println("End:  " + new Date());
        pwOut.close();
        System.exit(0);
    }

    private static RootModel doSearch(String text) throws Exception {
        URL url = new URL(baseUrl + "/request?text=" + text + "&metrics=true");
        try {
            return HttpHelper.doGET(url);
        } catch(Throwable th) {
            System.out.println(text + " --> Throwable: " + th.getMessage());
            return null;
        }
    }

    private static RootModel doHealthCheck() throws Exception {
        URL url = new URL(baseUrl + "?metrics=true");
        try {
            return HttpHelper.doGET(url);
        } catch(Throwable th) {
            System.out.println("HealthCheck --> Throwable: " + th.getMessage());
            return null;
        }
    }
}
