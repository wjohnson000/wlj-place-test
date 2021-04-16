package std.wlj.jira;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;


public class FixSolr1470205 {

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsBetaConnection(false);

        PlaceRepDoc xDoc = lookupDocument(solrConn, "1470205");
        PlaceRepDoc xDoc1 = lookupDocument(solrConn, "1470206");
        PlaceRepDoc xDoc2 = lookupDocument(solrConn, "1470207");
        PlaceRepDoc xDoc3 = lookupDocument(solrConn, "1470208");

        // Modify the document ...
        if (xDoc != null) {
//            System.out.println("\n\nUpdating the document ...");
//            xDoc.setCentroid("13.873111,100.596778");
//            solrConn.add(xDoc);
//            solrConn.commit();
        }
        System.exit(0);
    }

    private static PlaceRepDoc lookupDocument(SolrConnection solrConn, String repId) throws PlaceDataException {
        // Do a look-up by documents ...
        SolrQuery query = new SolrQuery("id:" + repId);
        query.setRows(1000);
        query.setSort("revision", ORDER.asc);
        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("REP: " + repId + ";  CNT: " + docs.size());

        PlaceRepDoc xDoc = null;
        for (PlaceRepDoc doc : docs) {
            xDoc = doc;
            System.out.println("ID: " + doc.getId() + " --> " + doc.getRevision() + " --> " + doc.getType() + " --> " + doc.getDeleteId() + " --> " + doc.getPlaceDeleteId() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()));
            System.out.println("  DEL: " + doc.getDeleteId() + " :: " + doc.getPlaceDeleteId());
            System.out.println("  LAT: " + doc.getCentroid() + " --> " + doc.getLatitude() + " :: " + doc.getLongitude());
            for (String varn : doc.getVariantNames()) {
                System.out.println("  VAR: " + varn);
            }
        }

        return xDoc;
    }
}
