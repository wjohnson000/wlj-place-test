package std.wlj.local;

//import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.data.solr.SolrSearchResults;


public class SearchByOwnerId {
    public static void main(String... args) throws Exception {
        System.setProperty("solr.solr.home", "C:/Tools/solr/data/tokoro");
        System.setProperty("solr.master.url", "");
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");

        SolrConnection solrConn = SolrConnection.connectToEmbeddedInstance("C:/Tools/solr/data/tokoro");

        // Do a first look-up
        SolrQuery query = new SolrQuery("id:1-*");
        List<PlaceRepDoc> docs = solrConn.search(query);

        Runtime.getRuntime().traceInstructions(true);
        Runtime.getRuntime().traceMethodCalls(true);

        String[] queriesXXX = {
            "q=ownerId%3A3541491",
            "q=ownerId%3A3541492+AND+revision%3A%5B0+TO+2147483647%5D",
            "q=ownerId%3A3541493+AND+revision%3A%5B0+TO+2147483647%5D&rows=100",
            "q=ownerId%3A3541494+AND+revision%3A%5B0+TO+2147483647%5D&rows=1&sort=revision+desc",
            "q=ownerId%3A3541495+AND+revision%3A%5B0+TO+2147483647%5D&sort=revision+desc",
            "q=ownerId%3A3541496&rows=100",
            "q=ownerId%3A3541497&rows=100&sort=revision+desc",
            "q=ownerId%3A3541498&sort=revision+desc"
        };

        String[] queries = {
            "q=ownerId:3541491",
            "q=ownerId:3541492 AND revision:(0 TO 2147483647)",
            "q=ownerId:3541493 AND revision:(0 TO 2147483647)&rows=100",
            "q=ownerId:3541494 AND revision:(0 TO 2147483647)&rows=1&sort=revision+desc",
            "q=ownerId:3541495 AND revision:(0 TO 2147483647)&sort=revision+desc",
            "q=ownerId:3541496&rows=100",
            "q=ownerId:3541497&rows=100&sort=revision+desc",
            "q=ownerId:3541498&sort=revision+desc"
        };

        SolrSearchResults ssr;
        for (int i=3541491;  i<3541499;  i++) {
            query = new SolrQuery("ownerId:" + i);
            long then = System.nanoTime();
            ssr = solrConn.search(query, null);
            long nnow = System.nanoTime();
            System.out.println(((nnow-then) / 1000000.0) + " " + query);

            query = new SolrQuery("ownerId:" + i);
            query.addSort("revision", ORDER.desc);
            query.setRows(1);
            then = System.nanoTime();
            ssr = solrConn.search(query, null);
            nnow = System.nanoTime();
            System.out.println(((nnow-then) / 1000000.0) + " " + query);

            query = new SolrQuery("ownerId:" + i + " AND revision:[0 TO " + Integer.MAX_VALUE + "]");
            then = System.nanoTime();
            ssr = solrConn.search(query, null);
            nnow = System.nanoTime();
            System.out.println(((nnow-then) / 1000000.0) + " " + query);
        }

        solrConn.shutdown();
    }
}
