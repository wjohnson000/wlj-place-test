package org.familysearch.std.wlj.local;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;


public class ZzzRepIdChain {
    public static void main(String... args) throws PlaceDataException {
		SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://localhost:8983/solr/places");
//        SolrConnection solrConn = SolrConnection.connectToEmbeddedInstance("C:/tools/Solr/data");

        SolrQuery query = new SolrQuery("repIdChainInt:64");
        query.setRows(64);
        query.setSort("id", ORDER.asc);
        List<PlaceRepDoc> docs = solrConn.search(query);
        for (PlaceRepDoc doc : docs) {
            System.out.println("  doc-id: " + doc.getId());
            System.out.println("      cx: " + doc.getRepIdChain());
            System.out.println("      cx: " + Arrays.toString(doc.getRepIdChain()));
            System.out.println("      cx: " + Arrays.toString(doc.getRepIdChainAsInt()));
        }

        solrConn.shutdown();
    }
}
