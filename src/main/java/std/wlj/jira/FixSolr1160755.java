package std.wlj.jira;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;


public class FixSolr1160755 {

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsProdConnection(false);

        PlaceRepDoc xDoc = lookupDocument(solrConn, "1160755");
//        lookupDocument(solrConn, "8158155");
//        lookupDocument(solrConn, "8158156");
//        lookupDocument(solrConn, "8158157");
//        lookupDocument(solrConn, "8158158");
//        lookupDocument(solrConn, "8158159");
//        lookupDocument(solrConn, "8161203");

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
