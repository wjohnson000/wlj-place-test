package std.wlj.thread;

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

public class TestSearchRawThreaded {

    /** Base URL of the application */
    private static String baseUrl = "https://place-ws-dev.dev.fsglobal.org/int-std-ws-place/places";

    private static ExecutorService execService = Executors.newFixedThreadPool(20);

    private static Random random = new Random();
    private static List<String> requestText;
    private static List<String> interpName;

    private static long requestCnt = 0;
    private static long requestTime = 0;

    private static long interpCnt = 0;
    private static long interpTime = 0;

    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        requestText = getSearchValues("C:/temp/places-search-text.txt");
        interpName = getSearchValues("C:/temp/places-interp-name.txt");

        HttpHelper.overrideHTTPS = true;
        HttpHelper.doVerbose = false;

        long sss = System.nanoTime();
        startRequest(4, 500);
        startInterp(4, 500);
        execService.shutdown();
        execService.awaitTermination(60, TimeUnit.MINUTES);
        long eee = System.nanoTime();

        System.out.println("\n=======================================================================================");
        System.out.println("Time: " + (eee-sss) / 1_000_000.0);
        System.out.println("Request: " + requestCnt + " --> " + (requestTime/1_000_000.0));
        System.out.println("Interp : " + interpCnt + " --> " + (interpTime/1_000_000.0));
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

    private static void startInterp(int thrCount, int times) {
        for (int i=0;  i<thrCount;  i++) {
            execService.submit(
                () -> {
                    for (int cnt=0;  cnt<times;  cnt++) {
                        try {
                            int ndx = random.nextInt(interpName.size());
                            doInterp(interpName.get(ndx));
                            Thread.sleep(8L);
                        } catch (Exception e) { }
                    }
                });
        }
    }

    private static void doRequest(String text) throws Exception {
        requestCnt++;

        URL url = new URL(baseUrl + "/request");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("TEST-REQUEST: " + text);

        long then = System.nanoTime();
        HttpHelper.doGET(url, "text", text, "metrics", "false");
        long nnow = System.nanoTime();
        System.out.println("TIME: " + (nnow-then)/1000000.0);

        requestTime += nnow - then;
    }

    private static void doInterp(String text) throws Exception {
        interpCnt++;

        URL url = new URL(baseUrl + "/interp");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("TEST-INTERP: " + text);

        long then = System.nanoTime();
        HttpHelper.doGET(url, "name", text, "metrics", "false");
        long nnow = System.nanoTime();
        System.out.println("TIME: " + (nnow-then)/1000000.0);

        interpTime += nnow - then;
    }

    private static List<String> getSearchValues(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath), StandardCharsets.UTF_8)
                .stream()
                .map(val -> val.replace('"', ' ').trim())
                .filter(val -> val.length() > 4)
                .collect(Collectors.toList());
    }
}
