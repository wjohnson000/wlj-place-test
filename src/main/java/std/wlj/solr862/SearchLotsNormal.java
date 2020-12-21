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
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.util.PlaceHelper;

import com.google.common.util.concurrent.AtomicDouble;
import std.wlj.util.SolrManager;

public class SearchLotsNormal {

    static final int MAX_ROWS = 1250;
    static final DateFormat SOLR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T00:00:00Z'"); 

    static SolrConnection solrConn = null;
    static Random         random   = new Random();
    static AtomicDouble   idTime   = new AtomicDouble();
    static AtomicDouble   nameTime = new AtomicDouble();

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
        solrConn = SolrManager.localEmbeddedConnection("C:/D-drive/solr/standalone-8.6.2");
        System.out.println("Write-Ready: " + solrConn.isWriteReady());

        // Read in a bunch of variant names
        System.out.println("Reading variant names ...");
        List<String> varNames = getVariantNames();
        System.out.println("Variant name count = " + varNames.size());
//        Files.write(Paths.get("C:/temp/varnames.txt"), varNames, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        // Seed the process ... execute an untimed search
        SolrQuery query = new SolrQuery("repId:1");
        query.setRows(MAX_ROWS);
        query.setSort("repId", SolrQuery.ORDER.desc);
        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("\nAll systems go ...\n");

        for (int i=0;  i<200_000;  i++) {
            int nameNdx = random.nextInt(varNames.size());
            execService.execute(() -> searchName(varNames.get(nameNdx)));

            int repId = random.nextInt(11_000_000);
            execService.execute(() -> searchId(repId));
        }
        execService.shutdown();
        execService.awaitTermination(30, TimeUnit.MINUTES);

        System.out.println();
        System.out.println("ID-TIME: " + idTime.doubleValue());
        System.out.println("NAME-TIME: " + nameTime.doubleValue());
        System.out.println();
        dumpGC();

        System.exit(0);
    }
    
    static void searchId(int repid) {
        SolrQuery query = new SolrQuery("repId:" + repid);
        query.setRows(MAX_ROWS);
        query.setSort("repId", SolrQuery.ORDER.desc);
        
        try {
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

    static void searchName(String name) {
        SolrQuery query = new SolrQuery("names:" + PlaceHelper.normalize(name).toLowerCase());
        query.setRows(MAX_ROWS);
        query.setSort("repId", SolrQuery.ORDER.desc);

        try {
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

    static List<String> getVariantNamesLong() throws Exception {
        List<String> varNames = Files.readAllLines(Paths.get("C:/temp/db-dump/variant-name-all.txt"), StandardCharsets.UTF_8);

        return varNames.stream()
                       .map(line -> PlaceHelper.split(line, '|'))
                       .filter(data -> data.length > 4)
                       .map(data -> data[3])
                       .filter(name -> ! name.contains(" "))
                       .limit(1_000_000)
                       .collect(Collectors.toList());
    }
}
