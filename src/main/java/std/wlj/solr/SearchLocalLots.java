package std.wlj.solr;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.data.solr.SolrSearchResults;

import std.wlj.util.SolrManager;


public class SearchLocalLots {

    public static void main(String... args) throws PlaceDataException, IOException {
//        SolrConnection solrConn = SolrManager.localEmbeddedConnection("D:/solr/newbie-6.1.0");
        SolrConnection solrConn = SolrManager.awsBetaConnection(true);

        getCount(solrConn, "*:*");
        getCount(solrConn, "published:1");
        getCount(solrConn, "deleteId:[* TO *]");
        getCount(solrConn, "-deleteId:[* TO *]");

        // Do a bunch of look-ups
        List<String> queries = Files.readAllLines(Paths.get("C:/temp/place-sample-queries.txt"), StandardCharsets.UTF_8);

        int progress   = 0;
        int totalTotal = 0;
        long then = System.nanoTime();
        for (String query : queries) {
            if (++progress % 50 == 0) {
                System.out.println("Prog: " + progress);
            }
            SolrQuery solrQuery = new SolrQuery(query);
//            solrQuery.setFilterQueries("published:1");
//            solrQuery.setFilterQueries("published:1", "-deleteId:[* TO *]");
            solrQuery.setFilterQueries("published:1", "-deleteId:[* TO *]", "-forwardRevision:[* TO *]");
//            solrQuery.setFilterQueries("published:1 -deleteId:[* TO *] -forwardRevision:[* TO *]");
            solrQuery.setRows(11);
            System.out.println("Solr query: " + solrQuery);
            List<PlaceRepDoc> docs = solrConn.search(solrQuery);
            totalTotal += docs.size();
            if (progress > 600) break;
        }
        long nnow = System.nanoTime();
        System.out.println("Cnt: " + totalTotal);
        System.out.println("Time: " + (nnow - then) / 1_000_000D);

        System.exit(0);
    }

    static void getCount(SolrConnection solrConn, String query) throws PlaceDataException {
        // Do a look-up by documents ...
        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.setRows(1);
        
        SolrSearchResults searchResults = solrConn.search(solrQuery, null);
        System.out.println("Qry: " + query + " --> " + searchResults.getFoundCount());
    }
}
