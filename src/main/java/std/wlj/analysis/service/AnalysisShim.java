package std.wlj.analysis.service;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.familysearch.standards.analysis.model.InterpretationModel;
import org.familysearch.standards.analysis.model.RootModel;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.core.logging.Logger;
import org.familysearch.standards.place.PlaceResults;
import org.familysearch.standards.place.ws.util.POJOMarshalUtil;

public class AnalysisShim {

    protected static final Logger logger = new Logger(AnalysisShim.class);

    private static boolean shareResults    = "true".equalsIgnoreCase(System.getProperty("enable.save.analytics", "false"));
    private static String  analyticsURL    = System.getProperty("analytics.base.url");
    private static ContentType contentType = ContentType.create(RootModel.APPLICATION_XML_PLACES, "UTF-8");

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
        if (shareResults) {
            fScheduler.submit(() -> mapAndSubmit(uriInfo, headers, results, locale));
        }
    }

    protected static void mapAndSubmit(UriInfo uriInfo, HttpHeaders headers, PlaceResults results, StdLocale locale) {
        PlaceResultsMapper mapper = new PlaceResultsMapper();
        InterpretationModel interpModel = mapper.mapToModel(uriInfo, headers, results, locale);

        RootModel rootModel = new RootModel();
        rootModel.setInterpretation(interpModel);

        // POST the request, but don't show any concern about the response
        try(CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost httpPost = new HttpPost(analyticsURL);
            StringEntity entity = new StringEntity(POJOMarshalUtil.toXML(rootModel), contentType);
            httpPost.setEntity(entity);
            client.execute(httpPost);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
