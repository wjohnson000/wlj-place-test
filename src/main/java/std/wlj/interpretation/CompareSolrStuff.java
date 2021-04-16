package std.wlj.interpretation;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;

public class CompareSolrStuff {

    static Random random = new Random();

    public static void main(String... args) throws PlaceDataException, IOException {
        SolrConnection solrConn = SolrManager.localEmbeddedConnection("C:/D-drive/solr/standalone-7.1.0");
        System.out.println("Write-Ready: " + solrConn.isWriteReady());

        long time0;
        long time1;
        long timeLOV = 0;
        long timeORS = 0;
        Set<String> repIds;

        // Do a couple of initial queries to get past any initial "start-up" time
        repIds = getRepIds(30);
        getDocsLOV(solrConn, repIds);
        repIds = getRepIds(30);
        getDocsORS(solrConn, repIds);

        // Execute 25,000 queries with both formats and accumulate the execution time
        for (int i=1;  i<25_000;  i++) {
            repIds = getRepIds(25);
            time0 = System.nanoTime();
            getDocsLOV(solrConn, repIds);
            time1 = System.nanoTime();
            timeLOV += (time1 - time0);

            repIds = getRepIds(25);
            time0 = System.nanoTime();
            getDocsORS(solrConn, repIds);
            time1 = System.nanoTime();
            timeORS += (time1 - time0);
        }

        System.out.println("\n\n");
        System.out.println("TimeLOV: " + (timeLOV / 1_000_000.0));
        System.out.println("TimeORS: " + (timeORS / 1_000_000.0));
        System.exit(0);
    }

    static List<PlaceRepDoc> getDocsLOV(SolrConnection conn, Set<String> repIds) throws PlaceDataException {
        String repIdStr = repIds.stream().collect(Collectors.joining(" ", "repId:(", ")"));
        SolrQuery query = new SolrQuery(repIdStr);
        query.addFilterQuery("-deleteId:[* TO *]");
        query.setRows(32);
//        System.out.println("QQ.LOV=" + query.getQuery());
        return conn.search(query);
    }

    static List<PlaceRepDoc> getDocsORS(SolrConnection conn, Set<String> repIds) throws PlaceDataException {
        String repIdStr = repIds.stream().map(id -> "repId:" + id).collect(Collectors.joining(" OR "));
        SolrQuery query = new SolrQuery(repIdStr);
        query.addFilterQuery("-deleteId:[* TO *]");
        query.setRows(32);
//        System.out.println("QQ.ORS=" + query.getQuery());
        return conn.search(query);
    }

    static Set<String> getRepIds(int count) {
        Set<String> repIds = new HashSet<>();
        for (int i=0;  i<count;  i++) {
            repIds.add(String.valueOf(random.nextInt(10_000_000)));
        }
        return repIds;
    }
}
