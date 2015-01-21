package std.wlj.solr;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;


public class AppDataDocument {

    public static void main(String... args) throws PlaceDataException {
//        String solrHome = "http://localhost:8983/solr/places";
        String solrHome = "http://familysearch.org/int-solr/places";
//        String solrHome = "http://place-solr-test.dev.fsglobal.org/int-solr/places";

        System.setProperty("solr.solr.home", solrHome);
        System.setProperty("solr.master.url", solrHome);
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        SolrConnection solrConn = SolrConnection.connectToRemoteInstance(solrHome);

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
