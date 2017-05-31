package std.wlj.local;

//import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.CitationBridge;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;


public class SearchRepository {
    public static void main(String... args) throws Exception {
        SolrConnection solrConn = SolrManager.localEmbeddedConnection("D:/solr/tokoro");

        // Do a first look-up
        SolrQuery query = new SolrQuery("id:123456-*");
        List<PlaceRepDoc> docs = solrConn.search(query);
        for (PlaceRepDoc doc : docs) {
            System.out.println("DOC ... " + doc.getId());
            System.out.println("  C ... " + doc.getCentroid());
            System.out.println("  U ... " + doc.getUUID());
            for (CitationBridge citn : doc.getAllCitations()) {
                System.out.println("        " + citn.getCitationId() + " . " + citn.getSourceRef());
            }
        }
        solrConn.shutdown();
    }
}
