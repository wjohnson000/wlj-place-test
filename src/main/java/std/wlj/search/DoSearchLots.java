package std.wlj.search;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import std.wlj.ws.rawhttp.HttpHelper;

public class DoSearchLots {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
    private static String baseUrl = "http://ws-55.place.standards.service.dev.us-east-1.dev.fslocal.org/places";

    private static ExecutorService execService = Executors.newFixedThreadPool(20);

    private static Random random = new Random();
    private static List<String> requestText;

    private static long requestCnt = 0;
    private static long requestTime = 0;

    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        requestText = getSearchValues("C:/temp/important/places-search-text.txt");

        HttpHelper.overrideHTTPS = true;
        HttpHelper.doVerbose = false;

        long time0 = System.nanoTime();
        startRequest(5, 1000);
        execService.shutdown();
        execService.awaitTermination(60, TimeUnit.MINUTES);
        long time1 = System.nanoTime();

        System.out.println("\n=======================================================================================");
        System.out.println("Time: " + (time1-time0) / 1_000_000.0);
        System.out.println("Request: " + requestCnt + " --> " + (requestTime/1_000_000.0));
    }

    private static void startRequest(int thrCount, int times) {
        for (int i=0;  i<thrCount;  i++) {
            execService.submit(
                () -> {
                    for (int cnt=0;  cnt<times;  cnt++) {
                        try {
                            int ndx = random.nextInt(requestText.size());
                            doRequest(requestText.get(ndx));
                            Thread.sleep(9L);
                        } catch (Exception e) { }
                    }
                });
        }
    }

    private static void doRequest(String text) throws Exception {
        requestCnt++;

        URL url = new URL(baseUrl + "/request");

        long then = System.nanoTime();
        HttpHelper.doGET(url, "text", text, "metrics", "false", "partial", "false", "pubType", "pub_only");
        long nnow = System.nanoTime();

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("TEST-REQUEST: " + text);
        System.out.println("TIME: " + (nnow-then)/1000000.0);

        requestTime += nnow - then;
    }

    private static List<String> getSearchValues(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8)
                .stream()
                .map(val -> val.replace('"', ' ').trim())
                .filter(val -> val.length() > 4)
                .collect(Collectors.toList());
    }
}
