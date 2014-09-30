package std.wlj.ws.rawhttp;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.familysearch.standards.place.ws.model.MetricsModel;
import org.familysearch.standards.place.ws.model.PlaceSearchResultModel;
import org.familysearch.standards.place.ws.model.PlaceSearchResultsModel;
import org.familysearch.standards.place.ws.model.RootModel;
import org.familysearch.standards.place.ws.model.ScorerModel;


public class TestSearchMetricsThreads {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
//    private static String baseUrl = "http://place-ws-dev.dev.fsglobal.org/int-std-ws-place/places";
    private static String baseUrl = "http://place-ws-stage.dev.fsglobal.org/int-std-ws-place/places";

    private static final double ONE_MILLION = 1000000.0;

    private static final Random random = new Random();


    /**
     * Run a bunch of searches ...
     */
    public static void main(String[] args) throws Exception {
        int numThr = 12;  // 20;
        int nInterp = 25;  //50000;
        String inPath  = "C:/temp/local-all.txt";
        String outPath = "C:/temp/results-search-41k.txt";

        // First parameter is the IP address of the target server
        if (args.length > 0) {
            baseUrl = args[0];
        }

        // Second parameter is the path to the input file
        if (args.length > 1) {
            inPath = args[1];
        }

        // Third parameter is the path to the output file
        if (args.length > 2) {
            outPath = args[2];
        }

        // Fourth parameter is the number of threads
        if (args.length > 3) {
            numThr = Integer.parseInt(args[3]);
        }

        // Fifth parameter is the number of interpretations to perform
        if (args.length > 4) {
            nInterp = Integer.parseInt(args[4]);
        }

        FileSystem currFS = FileSystems.getDefault();

        final int numInterp = nInterp;
        final Path temp = currFS.getPath(inPath);
        final PrintWriter pwOut = new PrintWriter(new FileWriter(new File(outPath)));
        final List<String> textes = Files.readAllLines(temp, Charset.forName("UTF-8"));

        Date startDate = new Date();
        Thread[] threads = new Thread[numThr];
        for (int i=0;  i<threads.length;  i++) {
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        runAll(textes, numInterp, pwOut);
                    } catch (Exception e) {
                        System.out.println("OOPS!! " + e.getMessage());
                    }
                }
            },
            "thr-" + i);
            threads[i].start();
        }

        // Sit here until all the threads finish
        boolean isRunning = true;
        while(isRunning) {
            try { Thread.sleep(1000); } catch(Exception ex) { }
            isRunning = false;
            for (Thread thread : threads) {
                if (thread.isAlive()) {
                    isRunning = true;
                }
            }
        }

        // Final processing
        System.out.println("\n\n");
        System.out.println("Date: " + startDate);
        System.out.println("End:  " + new Date());
        pwOut.close();
        System.exit(0);
    }

    private static RootModel doSearch(String text) throws Exception {
        URL url = new URL(baseUrl + "/request?text=" + text + "&metrics=true");
        try {
            return TestUtil.doGET(url);
        } catch(Throwable th) {
            System.out.println(text + " --> Throwable: " + th.getMessage());
            return null;
        }
    }

    /**
     * Run 41K tests through the system ... run them in *random* order
     * 
     * @param textes List of strings to interpret
     * @param pwOut Writer where results are to be saved
     */
    private static void runAll(List<String> textes, int numInterp, PrintWriter pwOut) throws Exception {
        for (int i=0;  i<numInterp;  i++) {
            int ndx = random.nextInt(textes.size());
            String textx = textes.get(ndx);

//            try { Thread.sleep(1); } catch(Exception ex) { }

            long time = System.nanoTime();
            RootModel model = doSearch(textx);
            time = System.nanoTime() - time;

            StringBuilder buff = new StringBuilder();
            if (model == null  ||  model.getSearchResults() == null) {
                buff.append(textx);
                buff.append("|").append(Thread.currentThread().getName());
                pwOut.println(buff.toString());
                continue;
            }

            PlaceSearchResultsModel resultsModel = model.getSearchResults().get(0);
            MetricsModel metrics = resultsModel.getMetrics();

            buff.append(textx);
            buff.append("|").append(time / ONE_MILLION);
            buff.append("|").append(metrics.getTimings().getTotalTime() / ONE_MILLION);
            buff.append("|").append(metrics.getTimings().getIdentifyCandidatesLookupTime() / ONE_MILLION);
            buff.append("|").append(metrics.getTimings().getParseTime() / ONE_MILLION);
            buff.append("|").append(metrics.getTimings().getScoringTime() / ONE_MILLION);
            ScorerModel nvpScorer = null;
            for (ScorerModel scorer : metrics.getScorers().getScorers()) {
                if (scorer.getName().equals("NameVariantPriorityScorer")) {
                    nvpScorer = scorer;
                }
            }
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
    }
}
