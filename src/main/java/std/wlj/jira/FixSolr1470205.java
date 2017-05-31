package std.wlj.jira;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;


public class FixSolr1470205 {

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsProdConnection(false);

        PlaceRepDoc xDoc = lookupDocument(solrConn, "1470205");
//        lookupDocument(solrConn, "5950075");
//        lookupDocument(solrConn, "8160631");
//        lookupDocument(solrConn, "8160632");
//        lookupDocument(solrConn, "8160634");
//        lookupDocument(solrConn, "8160739");
//        lookupDocument(solrConn, "8160740");
//        lookupDocument(solrConn, "8160741");
//        lookupDocument(solrConn, "8160743");
//        lookupDocument(solrConn, "8160744");
//        lookupDocument(solrConn, "8160745");
//        lookupDocument(solrConn, "8160746");

        // Modify the document ...
        if (xDoc != null) {
//            System.out.println("\n\nUpdating the document ...");
//            xDoc.setDeleteId(null);
//            solrConn.add(xDoc);
//            solrConn.commit();
        }
        System.exit(0);
    }

    private static PlaceRepDoc lookupDocument(SolrConnection solrConn, String repId) throws PlaceDataException {
        // Do a look-up by documents ...
        SolrQuery query = new SolrQuery("id:" + repId + "-*");
        query.setRows(1000);
        query.setSort("revision", ORDER.asc);
        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("REP: " + repId + ";  CNT: " + docs.size());

        PlaceRepDoc xDoc = null;
        for (PlaceRepDoc doc : docs) {
            xDoc = doc;
            System.out.println("ID: " + doc.getId() + " --> " + doc.getRevision() + " --> " + doc.getType() + " --> " + doc.getDeleteId() + " --> " + doc.getPlaceDeleteId() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()));
            System.out.println("  DEL: " + doc.getDeleteId() + " :: " + doc.getPlaceId());
            for (String varn : doc.getVariantNames()) {
                System.out.println("  VAR: " + varn);
            }
        }

        return xDoc;
    }
}
