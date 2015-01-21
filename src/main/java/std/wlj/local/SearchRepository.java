package std.wlj.local;

//import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.CitationBridge;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;


public class SearchRepository {
    public static void main(String... args) throws Exception {
        String solrHome = "C:/Users/wjohnson000/.places/tokoro-ni";

        System.setProperty("solr.solr.home", solrHome);
        System.setProperty("solr.master.url", solrHome);
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");

//        SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://localhost:8983/solr/places");
        SolrConnection solrConn = SolrConnection.connectToEmbeddedInstance(solrHome);

        // Do a first look-up
        
//        solrConn.optimize();
        SolrQuery query = new SolrQuery("id:123456-*");
        List<PlaceRepDoc> docs = solrConn.search(query);
        for (PlaceRepDoc doc : docs) {
            System.out.println("DOC ... " + doc.getId());
            System.out.println("  V ... " + doc.getRevision() + " --> " + doc.getVersion());
            System.out.println("  C ... " + doc.getCentroid());
            System.out.println("  U ... " + doc.getUUID());
            for (CitationBridge citn : doc.getAllCitations()) {
                System.out.println("        " + citn.getCitationId() + " . " + citn.getSourceRef());
            }
        }
        solrConn.shutdown();
    }
}
