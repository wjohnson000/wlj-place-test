package std.wlj.solr;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.marshal.POJOMarshalUtil;
import std.wlj.util.SolrManager;

public class BackupSolr {

    static final int MAX_ROWS = 5000;
    static final DateFormat SOLR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T00:00:00Z'"); 

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.localEmbeddedConnection("C:/D-drive/solr/standalone-7.7.1");
        System.out.println("Write-Ready: " + solrConn.isWriteReady());

        SolrQuery query = new SolrQuery("repId:56");
        query.setRows(MAX_ROWS);
        query.setSort("repId", SolrQuery.ORDER.desc);
        System.out.println("QRY: " + query);

        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("CNT: " + docs.size());

        String json = POJOMarshalUtil.toJSON(docs.get(0));
        System.out.println("\nJSON\n: " + json);

//        solrConn.backupIndex("2019-06-12", "C:\\temp");

        solrConn.shutdown();
        System.exit(0);
    }
}
