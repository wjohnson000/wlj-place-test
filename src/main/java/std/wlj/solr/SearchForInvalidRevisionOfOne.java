package std.wlj.solr;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.data.solr.SolrSearchResults;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;


public class SearchForInvalidRevisionOfOne {

    public static void main(String... args) throws PlaceDataException {
//        SolrConnection solrConn = SolrManager.localHttpConnection();
//        SolrConnection solrConn = SolrManager.awsBetaConnection(true);
        SolrConnection solrConn = SolrManager.awsProdConnection(true);

//        SolrQuery query = new SolrQuery("forwardRevision:1");
        SolrQuery query = new SolrQuery("id:PLACE-*");
        query.setRows(100);
        query.setSort("revision", SolrQuery.ORDER.asc);
        SolrSearchResults searchResults = solrConn.search(query, null);
        System.out.println("F-CNT: " + searchResults.getFoundCount());
        System.out.println("R-CNT: " + searchResults.getReturnedCount());
//        List<PlaceRepDoc> docs = solrConn.search(query);
//
//        System.out.println("Count: " + docs.size());
//        System.out.println("Rows? "  + query.getRows());
//        System.out.println();
//        for (PlaceRepDoc doc : docs) {
//            System.out.println("ID: " + doc.getId() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()));
//            System.out.println("  Revsn:  " + doc.getRevision());
//            System.out.println("  Place:  " + doc.getPlaceId());
//            System.out.println("  F-Rev:  " + doc.getForwardRevision());
//            System.out.println("  D-Name: " + doc.getDisplayNameMap());
//            System.out.println("  P-Name: " + doc.getNames());
//            System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
//        }

//        solrConn.shutdown();
        System.exit(0);
    }
}
