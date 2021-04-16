package std.wlj.solr.helper;

import java.io.IOException;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.exceptions.PlaceDataException;

public class CalculateStorageNoCitations {
    public static void main(String... args) throws PlaceDataException, IOException {
        SolrConnectionX solrConn = SolrConnectionX.connectToEmbeddedInstance("D:/solr/stand-alone-6.1.0");
        System.out.println("SOLR-conn: " + solrConn);

        Map<Integer, PlaceRepDoc> docCache = new HashMap<>(500_000);

        long start = System.nanoTime();
        MemoryMXBean memoryBn = ManagementFactory.getMemoryMXBean();
        List<GarbageCollectorMXBean> gcBeans = ManagementFactory.getGarbageCollectorMXBeans();

        for (int repid=1;  repid < 11_111_111;  repid+=11) {
            if (repid % 1000 == 0) {
                MemoryUsage hmUse = memoryBn.getHeapMemoryUsage();

                double ttt = (System.nanoTime() - start) / 1_000_000.0;
                System.out.println("RepId: " + repid + " --> Count: " + docCache.size());
                System.out.println(" Time: " + ttt + (ttt > 250 ? " >> LONG LONG LONG <<" : ""));
                System.out.println("   HM: init=" + hmUse.getInit() + " | comm=" + hmUse.getCommitted() + " | max=" + hmUse.getMax() + " | used=" + hmUse.getUsed());
                for (GarbageCollectorMXBean gcBean : gcBeans) {
                    System.out.println("   GC: name=" + gcBean.getName() + " | count=" + gcBean.getCollectionCount() + " | time=" + (gcBean.getCollectionTime() / 1_000.0));
                }
                start = System.nanoTime();
            }

            SolrQuery query = new SolrQuery("repId:" + repid);
            query.setSort("repId", SolrQuery.ORDER.desc);
            query.setRows(11);

            List<PlaceRepDoc> docs = solrConn.search(query);
            docs.forEach(doc -> doc.clearCitations());
            docs.forEach(doc -> docCache.put(doc.getRepId(), doc));
        }

        MemoryUsage hmUse = memoryBn.getHeapMemoryUsage();
        MemoryUsage nhmUse = memoryBn.getNonHeapMemoryUsage();

        System.out.println("\nTOTAL: " + docCache.size());
        System.out.println("   HM: init=" + hmUse.getInit() + " | comm=" + hmUse.getCommitted() + " | max=" + hmUse.getMax() + " | used=" + hmUse.getUsed());
        System.out.println("  NHM: init=" + nhmUse.getInit() + " | comm=" + nhmUse.getCommitted() + " | max=" + nhmUse.getMax() + " | used=" + nhmUse.getUsed());
        System.exit(0);
    }

}
