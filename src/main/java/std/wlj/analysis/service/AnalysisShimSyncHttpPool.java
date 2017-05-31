package std.wlj.analysis.service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.LongAdder;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.familysearch.standards.analysis.model.InterpretationModel;
import org.familysearch.standards.analysis.model.RootModel;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.core.logging.Logger;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.ws.util.POJOMarshalUtil;

public class AnalysisShimSyncHttpPool {

    public static class MyStats {
        LongAdder count = new LongAdder();
        LongAdder total = new LongAdder();
        long minXX = 1_000_000_000;
        long maxXX = 0;
    }

    protected static final Logger logger = new Logger(AnalysisShimSyncHttpPool.class);

    private static boolean shareResults    = "true".equalsIgnoreCase(System.getProperty("enable.save.analytics", "false"));
    private static String  analyticsURL    = System.getProperty("analytics.base.url");
    private static ContentType contentType = ContentType.create(RootModel.APPLICATION_XML_PLACES, "UTF-8");

    private static PoolingHttpClientConnectionManager httpConnManager = new PoolingHttpClientConnectionManager();
    static {
        httpConnManager.setMaxTotal(30);
        httpConnManager.setDefaultMaxPerRoute(25);
    }

    public static LongAdder inOut   = new LongAdder();
    public static LongAdder oopsCnt = new LongAdder();
    public static LongAdder clientCnt = new LongAdder();

    public static MyStats initStats = new MyStats();
    public static MyStats postStats = new MyStats();
    public static Set<String> clientHash = new HashSet<>();

    private static ScheduledExecutorService fScheduler;
    static {
        fScheduler = Executors.newScheduledThreadPool(
            20,
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
        PlaceResultsMapper mapper = new PlaceResultsMapper();
        InterpretationModel interpModel = mapper.mapToModel(uriInfo, headers, results, locale);

        RootModel rootModel = new RootModel();
        rootModel.setInterpretation(interpModel);

        // POST the request, consume the result ... which are empty!!
        try {
            CloseableHttpClient client = HttpClients.createMinimal(httpConnManager);
            clientHash.add(String.valueOf(client.hashCode()));
            HttpPost httpPost = new HttpPost(analyticsURL);
            StringEntity entity = new StringEntity(POJOMarshalUtil.toXML(rootModel), contentType);
            httpPost.setEntity(entity);
            CloseableHttpResponse response = client.execute(httpPost);
System.out.println(">>> RESP: " + response.getStatusLine());
            clientCnt.increment();
            EntityUtils.consume(response.getEntity());
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
