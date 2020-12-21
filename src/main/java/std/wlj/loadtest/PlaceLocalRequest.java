package std.wlj.loadtest;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.ThreadMXBean;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import std.wlj.ws.rawhttp.HttpHelper;

public class PlaceLocalRequest {

    /** Base URL of the application */
    private static String baseUrl = "http://localhost:8080/std-ws-place/places";

    private static ExecutorService execService = Executors.newFixedThreadPool(32);

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
        startRequest(24, 7500);
        execService.shutdown();
        execService.awaitTermination(60, TimeUnit.MINUTES);
        long time1 = System.nanoTime();

        
        System.out.println("\n\n");
        System.out.println("RequestCount: " + requestCnt);
        System.out.println("RequestTime:  " + requestTime / 1_000_000.0);
        System.out.println("TotalRunTime: " + (time1-time0) / 1_000_000.0);
        logMemory();
        logGC();
    }

    private static void startRequest(int thrCount, int times) {
        for (int i=0;  i<thrCount;  i++) {
            execService.submit(
                () -> {
                    for (int cnt=0;  cnt<times;  cnt++) {
                        try {
                            int ndx = random.nextInt(requestText.size());
                            doRequest(requestText.get(ndx));
                            Thread.sleep(6L);
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

    static void logMemory() {
        double                  freeMemory;
        double                  totalMemory;
        double                  usedMemory;
        ThreadMXBean            threadBean;
        OperatingSystemMXBean   osBean;
        DecimalFormat           formatter = new DecimalFormat( "####" );

        formatter.setMaximumFractionDigits( 2 );

            freeMemory = ( double ) Runtime.getRuntime().freeMemory() / 1000000.0;
            totalMemory = ( double ) Runtime.getRuntime().totalMemory() / 1000000.0;
            usedMemory = totalMemory - freeMemory;
            threadBean = ManagementFactory.getThreadMXBean();
            osBean = ManagementFactory.getOperatingSystemMXBean();

            System.out.println("\n==================================================================");
            System.out.println("freeMemory: " + formatter.format( freeMemory ));
            System.out.println("totalMemory: " + formatter.format( totalMemory ));
            System.out.println("usedMemory: " + formatter.format( usedMemory ));
            System.out.println("threadCount: " + String.valueOf( threadBean.getThreadCount() ));
            System.out.println("peakThreadCount: " + String.valueOf( threadBean.getPeakThreadCount() ));
            System.out.println("averageSystemLoad: " + String.valueOf( osBean.getSystemLoadAverage() ));
            System.out.println("vCPUs: " + String.valueOf( osBean.getAvailableProcessors() ) );
    }

    static void logGC() {
        System.out.println("\n==================================================================");
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            System.out.println("GC-name: " + gcBean.getName());
            System.out.println("    cnt: " + gcBean.getCollectionCount());
            System.out.println("    tim: " + gcBean.getCollectionTime());
        }
    }
}
