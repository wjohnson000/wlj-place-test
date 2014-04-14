package org.familysearch.std.wlj.local;

import org.familysearch.standards.place.data.solr.SolrConnection;


public class ClearRepository {
    public static void main(String... args) throws Exception {
        SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://localhost:8983/solr/places");

        solrConn.delete("*:*");

        solrConn.commit();
        solrConn.shutdown();
    }
}
