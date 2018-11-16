package std.wlj.thread;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import std.wlj.ws.rawhttp.HttpHelper;

public class TestInterpThreaded {

    /** Base URL of the application */
    private static String baseUrl = "http://ws-55.place.std.cmn.dev.us-east-1.dev.fslocal.org/places";

    private static ExecutorService execService = Executors.newFixedThreadPool(20);

    private static Random random = new Random();
    private static Charset UTF_8 = Charset.forName("UTF-8");
    private static List<String> bunchOfNames;

    private static long interpCnt = 0;
    private static long interpTime = 0;

    /**
     * Run two tests ... a GET of a specific place, and a search
     */
    public static void main(String[] args) throws Exception {
        bunchOfNames = getSearchValues("C:/temp/important/places-interp-name.txt");
        System.out.println("Name.count: " + bunchOfNames.size());
        bunchOfNames.addAll(getSearchValues("C:/temp/important/places-search-text.txt"));
        System.out.println("Name.count: " + bunchOfNames.size());

        HttpHelper.overrideHTTPS = true;
        HttpHelper.doVerbose = false;

        justAFew();
//        long sss = System.nanoTime();
//        startInterp(10, 16000);
//        startRequest(10, 16000);
//        execService.shutdown();
//        execService.awaitTermination(60, TimeUnit.MINUTES);
//        long eee = System.nanoTime();
//
//        System.out.println("\n=======================================================================================");
//        System.out.println("Time: " + (eee-sss) / 1_000_000.0);
//        System.out.println("Interp : " + interpCnt + " --> " + (interpTime/1_000_000.0));
    }

    protected static void justAFew() throws Exception {
        for (int i=1;  i<=3;  i++) {
            int ndx1 = random.nextInt(bunchOfNames.size());
            int ndx2 = random.nextInt(bunchOfNames.size());

            doRequest(bunchOfNames.get(ndx1));
            doInterp(bunchOfNames.get(ndx2));

            try { Thread.sleep(100L); } catch(Exception ex) { }
            doRequest(bunchOfNames.get(ndx1));
            doInterp(bunchOfNames.get(ndx2));
        }
    }

    protected static void startInterp(int thrCount, int times) {
        for (int i=0;  i<thrCount;  i++) {
            execService.submit(
                () -> {
                    for (int cnt=0;  cnt<times;  cnt++) {
                        try {
                            int ndx = random.nextInt(bunchOfNames.size());
                            doInterp(bunchOfNames.get(ndx));
                            Thread.sleep(8L);
                        } catch (Exception e) { }
                    }
                });
        }
    }

    protected static void startRequest(int thrCount, int times) {
        for (int i=0;  i<thrCount;  i++) {
            execService.submit(
                () -> {
                    for (int cnt=0;  cnt<times;  cnt++) {
                        try {
                            int ndx = random.nextInt(bunchOfNames.size());
                            doRequest(bunchOfNames.get(ndx));
                            Thread.sleep(8L);
                        } catch (Exception e) { }
                    }
                });
        }
    }

    protected static void doRequest(String text) throws Exception {
        interpCnt++;

        URL url = new URL(baseUrl + "/request");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("TEST-INTERP: " + text);

        long then = System.nanoTime();
        HttpHelper.doGET(url, "text", text, "metrics", "false");
        long nnow = System.nanoTime();
        System.out.println("TIME: " + (nnow-then)/1000000.0);

        interpTime += nnow - then;
    }

    protected static void doInterp(String text) throws Exception {
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

    protected static List<String> getSearchValues(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath), UTF_8)
                .stream()
                .map(val -> val.replace('"', ' ').trim())
                .filter(val -> val.length() > 4)
//                .limit(60000)
                .collect(Collectors.toList());
    }
}
