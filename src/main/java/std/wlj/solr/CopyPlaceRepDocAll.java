package std.wlj.solr;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;


public class CopyPlaceRepDocAll {

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn   = SolrManager.localHttpConnection();

        // Do a look-up by doc-id
        SolrQuery query;
        List<PlaceRepDoc> docs = new ArrayList<>();
        for (int repId=1;  repId<1_000;  repId++) {
            query = new SolrQuery("id:" + repId);
            query.setRows(2);
            docs.addAll(solrConn.search(query));

            if (docs.size() >= 100) {
                System.out.println("Adding docs -- count: " + docs.size());
                solrConn.add(docs);
                solrConn.commit();
                docs.clear();
            }
        }
        solrConn.add(docs);
        solrConn.commit();

        solrConn.shutdown();
        System.exit(0);
    }
}
