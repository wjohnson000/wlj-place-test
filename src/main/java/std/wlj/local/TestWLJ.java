package std.wlj.local;

//import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;


public class TestWLJ {
    public static void main(String... args) throws Exception {
        SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://place-solr.dev.fsglobal.org/solr/places");

        SolrQuery query = new SolrQuery("id:*");
        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("Doc-Count: " + docs.size());
        for (PlaceRepDoc doc : docs) {
            System.out.println("Doc: " + doc);
        }
        solrConn.shutdown();
    }
}
