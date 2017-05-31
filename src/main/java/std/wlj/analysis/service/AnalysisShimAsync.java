package std.wlj.analysis.service;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.LongAdder;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.familysearch.standards.analysis.model.InterpretationModel;
import org.familysearch.standards.analysis.model.RootModel;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.core.logging.Logger;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.ws.util.POJOMarshalUtil;

public class AnalysisShimAsync {

    public static class MyStats {
        LongAdder count = new LongAdder();
        LongAdder total = new LongAdder();
        long minXX = 1_000_000_000;
        long maxXX = 0;
    }

    protected static final Logger logger = new Logger(AnalysisShimAsync.class);

    private static boolean shareResults    = "true".equalsIgnoreCase(System.getProperty("enable.save.analytics", "false"));
    private static String  analyticsURL    = System.getProperty("analytics.base.url");
    private static ContentType contentType = ContentType.create(RootModel.APPLICATION_XML_PLACES, "UTF-8");

    public static LongAdder inOut   = new LongAdder();
    public static LongAdder oopsCnt = new LongAdder();
    public static LongAdder clientCnt = new LongAdder();

    public static MyStats initStats = new MyStats();
    public static MyStats postStats = new MyStats();

    private static ScheduledExecutorService fScheduler;
    static {
        fScheduler = Executors.newScheduledThreadPool(
            15,
            runn -> {
                Thread thr = Executors.defaultThreadFactory().newThread(runn);
                thr.setDaemon(true);
                return thr;
            });
        }

    public static void postResults(UriInfo uriInfo, HttpHeaders headers, PlaceResults results, StdLocale locale) {
long time0 = System.nanoTime();
        if (shareResults  &&  inOut.longValue() < 100L) {
            inOut.increment();
            fScheduler.submit(() -> mapAndSubmit(uriInfo, headers, results, locale));
        }
long time1 = System.nanoTime();
long elapse = time1 - time0;
initStats.count.increment();
initStats.minXX = Math.min(initStats.minXX, elapse);
initStats.maxXX = Math.max(initStats.maxXX, elapse);
initStats.total.add(elapse);
    }

    protected static void mapAndSubmit(UriInfo uriInfo, HttpHeaders headers, PlaceResults results, StdLocale locale) {
        inOut.decrement();
long time0 = System.nanoTime();
System.out.println("IN-OUT: " + inOut.longValue());
        PlaceResultsMapper mapper = new PlaceResultsMapper();
        InterpretationModel interpModel = mapper.mapToModel(uriInfo, headers, results, locale);

        RootModel rootModel = new RootModel();
        rootModel.setInterpretation(interpModel);

        // POST the request asynchronously
        try(CloseableHttpAsyncClient client = HttpAsyncClients.createDefault()) {
            HttpPost httpPost = new HttpPost(analyticsURL);
            StringEntity entity = new StringEntity(POJOMarshalUtil.toXML(rootModel), contentType);
            httpPost.setEntity(entity);
            client.execute(httpPost, null);
        } catch (IOException ex) {
            oopsCnt.increment();
            ex.printStackTrace();
        }
long time1 = System.nanoTime();
long elapse = time1 - time0;
postStats.count.increment();
postStats.minXX = Math.min(postStats.minXX, elapse);
postStats.maxXX = Math.max(postStats.maxXX, elapse);
postStats.total.add(elapse);

    }
}
