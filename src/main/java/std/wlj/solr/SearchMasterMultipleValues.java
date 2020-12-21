package std.wlj.solr;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;

public class SearchMasterMultipleValues {

    static final int MAX_ROWS = 5000;

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsDevConnection(true);
        System.out.println("Write-Ready: " + solrConn.isWriteReady());

        String query01 = "( ( names:belgi* ) ) AND " +
                "( ( ( ( ( ( ( ( ( ( ( ( ( ( ( ( ( ( ( ( " +
                "( repIdChain:3789071 ) ) OR ( repIdChain:10745764 ) ) OR ( repIdChain:267 ) ) OR " +
                "( repIdChain:10579912 ) ) OR ( repIdChain:2870 AND repIdChain:212) ) OR ( repIdChain:4725 ) ) OR " +
                "( repIdChain:2210 ) ) OR ( repIdChain:705803 ) ) OR ( repIdChain:705804 ) ) OR " +
                "( repIdChain:705805 ) ) OR ( repIdChain:5251449 ) ) OR ( repIdChain:5251450 ) ) OR " +
                "( repIdChain:3491207 ) ) OR ( repIdChain:6814199 ) ) OR ( repIdChain:6814200 ) ) OR " +
                "( repIdChain:6814202 ) ) OR ( repIdChain:3482611 ) ) OR ( repIdChain:6064640 ) ) OR " +
                "( repIdChain:8104545 ) ) OR ( repIdChain:2091 ) )";

        String query02 = "( ( names:belgi* ) ) AND " +
                "( repIdChain: ( 3789071 10745764 267 10579912 (2870 AND 212) 4725 2210 705803 705804 705805 5251449 5251450 3491207 6814199 6814200 6814202 3482611 6064640 8104545 2091 ) )";

        SolrQuery solrQuery01 = new SolrQuery(query01);
        solrQuery01.setRows(MAX_ROWS);
        solrQuery01.setSort("repId", SolrQuery.ORDER.desc);
        System.out.println("QRY01: " + solrQuery01);
        
        SolrQuery solrQuery02 = new SolrQuery(query02);
        solrQuery02.setRows(MAX_ROWS);
        solrQuery02.setSort("repId", SolrQuery.ORDER.desc);
        System.out.println("QRY: " + solrQuery02);

        List<PlaceRepDoc> docs01 = solrConn.search(solrQuery01);
        System.out.println("CNT-01: " + docs01.size());

        List<PlaceRepDoc> docs02 = solrConn.search(solrQuery01);
        System.out.println("CNT-02: " + docs02.size());

        docs01.removeIf(doc1 -> docs02.stream().anyMatch(doc2 -> doc2.getRepId() == doc1.getRepId()));
        System.out.println("CNT-DIFF: " + docs01.size());
        
        System.exit(0);
    }
}
