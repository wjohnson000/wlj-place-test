package std.wlj.local;

import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;


public class ClearRepository {
    public static void main(String... args) throws Exception {
        SolrConnection solrConn = SolrManager.localHttpConnection();

        solrConn.delete("*:*");

        solrConn.commit();
        solrConn.shutdown();
    }
}
