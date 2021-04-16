package std.wlj.solr;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;


public class AppDataDocument {

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsProdConnection(true);

        // Do a look-up by documents ...
        SolrQuery query = new SolrQuery("id:SOURCE");
        query.setRows(10);
        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("CNT: " + docs.size());

        for (PlaceRepDoc doc : docs) {
            System.out.println("ID: " + doc.getId() + " --> " + doc.getRevision());
            for (String appD: doc.getAppData()) {
            	System.out.println(appD);
            }
        }

        System.exit(0);
    }
}
