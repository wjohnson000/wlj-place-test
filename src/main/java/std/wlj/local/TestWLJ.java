package std.wlj.local;

//import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;


public class TestWLJ {
    public static void main(String... args) throws Exception {
        SolrConnection solrConn = SolrManager.awsDevConnection(false);

        solrConn.delete("id:11111111-1");
        solrConn.commit();

        SolrQuery query = new SolrQuery("id:*");
        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("Doc-Count: " + docs.size());
        for (PlaceRepDoc doc : docs) {
            System.out.println("Doc: " + doc + " --> " + doc.getId());
        }

        solrConn.shutdown();
    }
}
