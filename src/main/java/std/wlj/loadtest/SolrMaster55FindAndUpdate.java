package std.wlj.loadtest;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;

public class SolrMaster55FindAndUpdate {

    static final Date   TODAY  = new Date();
    static final Random RANDOM = new Random();

    public static void main(String... args) throws PlaceDataException {
        SolrConnection conn = SolrManager.awsDev55Connection(true);

        long time0 = System.nanoTime();
        Thread[] threads = new Thread[5]; 
        for (int i=0;  i<threads.length;  i++) {
            threads[i] = new Thread(() -> updateDocs(conn, 5_000), "Thread." + i);
            threads[i].start();
        }

        boolean isActive = true;
        while (isActive) {
            try { Thread.sleep(5000L); } catch(Exception ex) { }
            isActive = Arrays.stream(threads).anyMatch(thr -> thr.isAlive());
        }
        long time1 = System.nanoTime();
        System.out.println("\nTotal time: " + (time1-time0)/1_000_000.0);

        conn.shutdown();
        System.exit(0);
    }

    static void updateDocs(SolrConnection conn, int count) {
        long time0 = System.nanoTime();

        try {
            for (int cnt=0;  cnt<count;  cnt++) {
                int repId = RANDOM.nextInt(11_111_111);
                updateDoc(conn, repId);

                if (cnt % 100 == 0) System.out.println(Thread.currentThread().getName() + " -> " + cnt + " of " + count);
                if (cnt % 500 == 0) conn.commit();
            }
            conn.commit();
        } catch(PlaceDataException ex) {
            System.out.println("Thread: " + Thread.currentThread().getName() + " -> oops: " + ex.getMessage());
        }

        long time1 = System.nanoTime();
        System.out.println("Thread: " + Thread.currentThread().getName() + " -> count: " + count + " -> time: " + (time1-time0)/1_000_000.0);
    }

    static void updateDoc(SolrConnection conn, int repId) throws PlaceDataException {
        SolrQuery query = new SolrQuery("repId:" + repId);
        query.setRows(2);
        List<PlaceRepDoc> docs = conn.search(query);
        for (PlaceRepDoc doc : docs) {
            doc.setLastUpdateDate(TODAY);
            String citn = (doc.getCitations() == null  ||  doc.getCitations().isEmpty()) ? null : doc.getCitations().get(0);
            if (citn != null) {
                citn = citn + ".xyz";
                doc.addCitation(citn);
            }
            conn.add(doc);
        }
    }
}
