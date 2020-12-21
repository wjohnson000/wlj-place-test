package std.wlj.search;

import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import std.wlj.ws.rawhttp.HttpHelper;

public class DoGetRepLots {

    /** Base URL of the application */
//    private static String baseUrl = "http://localhost:8080/std-ws-place/places";
    private static String baseUrl = "http://ws-55.place.standards.service.dev.us-east-1.dev.fslocal.org/places";

    private static ExecutorService execService = Executors.newFixedThreadPool(20);

    private static long requestCnt = 0;
    private static long requestTime = 0;

    /**
     * Get reps by rep-id
     */
    public static void main(String[] args) throws Exception {
        HttpHelper.overrideHTTPS = true;
        HttpHelper.doVerbose = false;

        long time0 = System.nanoTime();
//        startRequest(2, 100);
        startRequest(3, 1000);
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
                    for (int cnt=1;  cnt<times;  cnt++) {
                        try {
                            doRequest(cnt);
                            Thread.sleep(9L);
                        } catch (Exception e) { }
                    }
                });
        }
    }

    private static void doRequest(int repId) throws Exception {
        requestCnt++;

        URL url = new URL(baseUrl + "/reps/" + repId);

        long then = System.nanoTime();
        HttpHelper.doGET(url, "metrics", "false");
        long nnow = System.nanoTime();

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("TEST-PLACE-REP: " + repId);
        System.out.println("TIME: " + (nnow-then)/1000000.0);

        requestTime += nnow - then;
    }
}
