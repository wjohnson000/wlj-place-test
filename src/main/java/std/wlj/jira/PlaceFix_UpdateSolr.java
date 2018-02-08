package std.wlj.jira;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;

public class PlaceFix_UpdateSolr {

    private static final int MAX_ROWS = 750;

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsProdConnection(true);

        SolrQuery query = new SolrQuery("!deleteId:[* TO *] AND placeDeleteId:[* TO *]");
        query.setRows(MAX_ROWS);
        query.setSort("repId", SolrQuery.ORDER.asc);
        System.out.println("QRY: " + query);

        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("CNT: " + docs.size());

        docs.forEach(doc -> doc.setPlaceDeleteId(null));
        solrConn.add(docs);
        solrConn.commit();

        solrConn.shutdown();
        System.exit(0);
    }
}
