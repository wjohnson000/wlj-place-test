package std.wlj.solr862;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.util.PlaceHelper;

import com.google.common.util.concurrent.AtomicDouble;
import std.wlj.util.SolrManager;

public class SearchLotsTrifecta {

    static final int MAX_ROWS = 1250;
    static final DateFormat SOLR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T00:00:00Z'"); 

    static Random         random   = new Random();
    static SolrConnection solrConnNormal   = null;
    static SolrConnection solrConnMismatch = null;
    static SolrConnection solrConnOldDocs  = null;
    static AtomicLong     idCount          = new AtomicLong();
    static AtomicLong     nameCount        = new AtomicLong();
    static AtomicDouble   idTimeNormal     = new AtomicDouble();
    static AtomicDouble   idTimeMismatch   = new AtomicDouble();
    static AtomicDouble   idTimeOldDocs    = new AtomicDouble();
    static AtomicDouble   nameTimeNormal   = new AtomicDouble();
    static AtomicDouble   nameTimeMismatch = new AtomicDouble();
    static AtomicDouble   nameTimeOldDocs  = new AtomicDouble();

    /** Create a pool for an executor service, with all threads being "daemon=true" */
    static final ScheduledExecutorService execService =
                      Executors.newScheduledThreadPool(
                          16,
                          runn -> {
                              Thread thr = Executors.defaultThreadFactory().newThread(runn);
                              thr.setDaemon(true);
                              return thr;
                          });

    public static void main(String... args) throws Exception {
        List<PlaceRepDoc> docs;

        solrConnNormal   = SolrManager.localEmbeddedConnection("C:/D-drive/solr/standalone-8.6.2");
        solrConnMismatch = SolrManager.localEmbeddedConnection("C:/D-drive/solr/standalone-8.6.2-mismatch");
        solrConnOldDocs  = SolrManager.localEmbeddedConnection("C:/D-drive/solr/standalone-8.6.2-olddata");
        System.out.println("Write-Ready: " + solrConnNormal.isWriteReady());
        System.out.println("Write-Ready: " + solrConnMismatch.isWriteReady());
        System.out.println("Write-Ready: " + solrConnOldDocs.isWriteReady());

        // Read in a bunch of variant names
        System.out.println("Reading variant names ...");
        List<String> varNames = getVariantNames();
        System.out.println("Variant name count = " + varNames.size());

        // Seed the process ... execute an untimed search
        SolrQuery query = new SolrQuery("repId:1");
        query.setRows(MAX_ROWS);
        query.setSort("repId", SolrQuery.ORDER.desc);

        docs = solrConnNormal.search(query);
        System.out.println("count.normal=" + docs.size());
        docs = solrConnMismatch.search(query);
        System.out.println("count.mismatch=" + docs.size());
        docs = solrConnOldDocs.search(query);
        System.out.println("count.oldDocs=" + docs.size());
        System.out.println("\nAll systems go ...\n");

        for (int i=0;  i<150_000;  i++) {
            int repId = random.nextInt(11_000_000);
            execService.execute(() -> searchId(repId, solrConnOldDocs, idTimeOldDocs));
            execService.execute(() -> searchId(repId, solrConnNormal, idTimeNormal));
            execService.execute(() -> searchId(repId, solrConnMismatch, idTimeMismatch));

//            int nameNdx = random.nextInt(varNames.size());
//            execService.execute(() -> searchName(varNames.get(nameNdx), solrConnOldDocs, nameTimeOldDocs));
//            execService.execute(() -> searchName(varNames.get(nameNdx), solrConnNormal, nameTimeNormal));
//            execService.execute(() -> searchName(varNames.get(nameNdx), solrConnMismatch, nameTimeMismatch));
        }
        execService.shutdown();
        execService.awaitTermination(30, TimeUnit.MINUTES);

        System.out.println();
        System.out.println("ID-TIME.normal: " + idTimeNormal.doubleValue());
        System.out.println("NAME-TIME.normal: " + nameTimeNormal.doubleValue());
        System.out.println();
        System.out.println("ID-TIME.mismatch: " + idTimeMismatch.doubleValue());
        System.out.println("NAME-TIME.mismatch: " + nameTimeMismatch.doubleValue());
        System.out.println();
        System.out.println("ID-TIME.oldDocs: " + idTimeOldDocs.doubleValue());
        System.out.println("NAME-TIME.oldDocs: " + nameTimeOldDocs.doubleValue());
        System.out.println();
        System.out.println("ID-COUNT: " + idCount.longValue());
        System.out.println("NAME-COUNT: " + nameCount.longValue());
        System.out.println();
        dumpGC();

        System.exit(0);
    }
    
    static void searchId(int repid, SolrConnection solrConn, AtomicDouble idTime) {
        SolrQuery query = new SolrQuery("repId:" + repid);
        query.setRows(MAX_ROWS);
        query.setSort("repId", SolrQuery.ORDER.desc);
        
        try {
            idCount.incrementAndGet();
            long time0 = System.nanoTime();
            List<PlaceRepDoc> docs = solrConn.search(query);
            long time1 = System.nanoTime();
            double millis = (time1 - time0) / 1_000_000.0;
            idTime.addAndGet(millis);
            System.out.println("THR.id: " + Thread.currentThread().getName() + " ... time: " + millis +
                                    " ... id: " + repid + " ... count: " + docs.size());
        } catch(Exception ex) {
            System.out.println("THR.YY: " + Thread.currentThread().getName() + " ... ex: " + ex.getMessage());
        }
    }

    static void searchName(String name, SolrConnection solrConn, AtomicDouble nameTime) {
        SolrQuery query = new SolrQuery("names:" + PlaceHelper.normalize(name).toLowerCase());
        query.setRows(MAX_ROWS);
        query.setSort("repId", SolrQuery.ORDER.desc);

        try {
            nameCount.incrementAndGet();
            long time0 = System.nanoTime();
            List<PlaceRepDoc> docs = solrConn.search(query);
            long time1 = System.nanoTime();
            double millis = (time1 - time0) / 1_000_000.0;
            nameTime.addAndGet(millis);
            System.out.println("THR.nm: " + Thread.currentThread().getName() + " ... time: " + millis +
                               " ... name: " + name + " ... count: " + docs.size());
        } catch(Exception ex) {
            System.out.println("THR.XX: " + Thread.currentThread().getName() + " ... ex: " + ex.getMessage());
        }
    }

    static void dumpGC() {
        for (GarbageCollectorMXBean gcBean : ManagementFactory.getGarbageCollectorMXBeans()) {
            System.out.println("Name: " + gcBean.getName());
            System.out.println("  GC count: " + Long.toString(gcBean.getCollectionCount()));
            System.out.println("  GC time: " + Long.toString(gcBean.getCollectionTime()));
        }

    }
    static List<String> getVariantNames() throws Exception {
        return Files.readAllLines(Paths.get("C:/temp/varnames.txt"), StandardCharsets.UTF_8);
    }
}
