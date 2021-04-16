package std.wlj.solr;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;


public class SearchMasterAppData {

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsProdConnection(true);
//        SolrConnection solrConn = SolrManager.localEmbeddedConnection("C:/D-drive/solr/standalone-dbload-7.1.0");
        System.out.println("Write-Ready: " + solrConn.isWriteReady());

        SolrQuery query = new SolrQuery("id:PLACE-TYPE");
        query.setRows(1);
        System.out.println("QRY: " + query);

        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("CNT: " + docs.size());

        for (PlaceRepDoc doc : docs) {
            System.out.println("\nID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()) + " --> " + doc.getRevision());
            doc.getAppData().stream().forEach(appData -> System.out.println("    AppD: " + appData));
        }

        System.exit(0);
    }
}
