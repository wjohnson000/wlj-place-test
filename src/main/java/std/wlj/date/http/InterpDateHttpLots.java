/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date.http;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.codahale.metrics.*;

import std.wlj.ws.rawhttp.HttpClientX;

/**
 * @author wjohnson000
 *
 */
public class InterpDateHttpLots {

    private static Random            RANDOM = new Random();
    private static DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("MMMM%20dd%20yyyy", Locale.ENGLISH);

    private static MetricRegistry    METRIC_REGISTRY;
    private static Timer             TIMER;
    private static Histogram         HISTOGRAM;

//    private static String baseUrl = "http://ws.date.standards.service.integ.us-east-1.dev.fslocal.org/dates/interp";
    private static String baseUrl = "http://localhost:8080/std-ws-date/dates/interp";

    public static void main(String... args) throws InterruptedException {
        METRIC_REGISTRY = new MetricRegistry();
        TIMER =     METRIC_REGISTRY.timer(MetricRegistry.name("http-meter", "timer"));
        HISTOGRAM = METRIC_REGISTRY.histogram(MetricRegistry.name("http-meter", "histo"));

        ScheduledExecutorService fScheduler = Executors.newScheduledThreadPool(32);
        for (int i=1;  i<=30;  i++) {
            fScheduler.schedule(() -> runLotsOnThread(), 25, TimeUnit.MILLISECONDS);
        }

        long time0 = System.nanoTime();
        fScheduler.shutdown();
        fScheduler.awaitTermination(30, TimeUnit.MINUTES);
        long time1 = System.nanoTime();
        System.out.println("\nDONE ... " + (time1 - time0) / 1_000_000.0);
        System.out.println("TIMER: " + TIMER.getMeanRate() + " . " + formatSnapshot(TIMER.getSnapshot()));
        System.out.println("HISTO: " + formatSnapshot(HISTOGRAM.getSnapshot()));
    }

    static void runLotsOnThread() {
        List<String> dateText = IntStream.range(0, 10000).mapToObj(ii -> createDate()).collect(Collectors.toList());
        System.out.println("Dates to test: " + dateText.size());

        long timeA = System.nanoTime();
        for (String dateStr : dateText) {
            final Timer.Context context = TIMER.time();
            long time0 = System.nanoTime();
            String dateResp = HttpClientX.doGetXML(baseUrl + "?text=" + dateStr);
            long time1 = System.nanoTime();
            context.close();
            HISTOGRAM.update(time1 - time0);

            if (dateResp == null) {
                System.out.println("OOPS!! -- " + dateStr);
            }
            try { Thread.sleep(3L); } catch(Exception ex) { }
        }
        long timeB = System.nanoTime();

        System.out.println(Thread.currentThread().getName() + ".TIME: " + (timeB - timeA) / 1_000_000.0);
    }

    static String createDate() {
        int year  = RANDOM.nextInt(1100) + 900;
        int month = RANDOM.nextInt(12) + 1;
        int dofm  = RANDOM.nextInt(28) + 1;

        LocalDate date = LocalDate.of(year, month, dofm);
        return FORMAT.format(date);
    }

    static String formatSnapshot(Snapshot snapshot) {
        StringBuilder buff = new StringBuilder();

        buff.append("min=").append(snapshot.getMin()/1_000_000.0);
        buff.append("; max=").append(snapshot.getMax()/1_000_000.0);
        buff.append("; men=").append(snapshot.getMean()/1_000_000.0);
        buff.append("; 75x=").append(snapshot.get75thPercentile()/1_000_000.0);
        buff.append("; 99x=").append(snapshot.get99thPercentile()/1_000_000.0);
        buff.append("; 999=").append(snapshot.get999thPercentile()/1_000_000.0);

        return buff.toString();
    }
}
