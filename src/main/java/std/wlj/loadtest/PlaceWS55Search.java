package std.wlj.loadtest;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import std.wlj.ws.rawhttp.HttpHelper;

public class PlaceWS55Search {

    /** Base URL of the application */
//    private static String baseUrl = "https://place-ws-dev-55.dev.fsglobal.org/int-std-ws-place-55/places";
    private static String baseUrl = "http://ws-55.place.std.cmn.dev.us-east-1.dev.fslocal.org/places";

    private static ExecutorService execService = Executors.newFixedThreadPool(10);

    private static Random random = new Random();
    private static List<String> requestText;

    private static long requestCnt = 0;
    private static long requestTime = 0;

    public static void main(String... args) throws Exception {
        HttpHelper.overrideHTTPS = true;
        HttpHelper.doVerbose = false;

        requestText =  Files.readAllLines(Paths.get("C:/temp/important/places-search-text.txt"), StandardCharsets.UTF_8)
            .stream()
            .map(val -> val.replace('"', ' ').trim())
            .filter(val -> val.length() > 4)
            .collect(Collectors.toList());

        long time0 = System.nanoTime();
        startRequest(10, 5000);
        execService.shutdown();
        execService.awaitTermination(60, TimeUnit.MINUTES);
        long time1 = System.nanoTime();

        System.out.println("\n\n");
        System.out.println("RequestCount: " + requestCnt);
        System.out.println("RequestTime:  " + requestTime / 1_000_000.0);
        System.out.println("TotalRunTime: " + (time1-time0) / 1_000_000.0);
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

    static void doRequest(String text) throws Exception {
        requestCnt++;

        URL url = new URL(baseUrl + "/request");

        long then = System.nanoTime();
        HttpHelper.doGET(url, "text", text, "metrics", "false", "partial", "false");
        long nnow = System.nanoTime();

        requestTime += nnow - then;
    }

}
