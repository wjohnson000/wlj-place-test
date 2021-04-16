package std.wlj.solr;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;
import java.util.Random;

public class SearchLots {

    public static void main(String... args) throws PlaceDataException {
        Random random = new Random();
        SolrConnection solrConn = SolrManager.awsDevConnection(true);
        System.out.println("Write-Ready: " + solrConn.isWriteReady());

        long total = 0L;
        for (int cnt=0;  cnt<5555;  cnt++) {
            int repId = random.nextInt(11_111_111);
            SolrQuery query = new SolrQuery("repId:" + repId);
            query.setRows(2);
            query.setSort("repId", SolrQuery.ORDER.desc);

            long time0 = System.nanoTime();
            List<PlaceRepDoc> docs = solrConn.search(query);
            long time1 = System.nanoTime();
            total += time1 - time0;

            System.out.println("Rep: " + cnt + "." + repId + " --> cnt: " + docs.size());
        }

        System.out.println("Total.time=" + (total / 1_000_000.0));
        System.exit(0);
    }
}
