package std.wlj.jira;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;


public class FixSolr8872031 {

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsProdConnection(false);

        // Do a look-up by documents ...
        SolrQuery query = new SolrQuery("id:8872031-*");
        query.setRows(1000);
        query.setSort("revision", ORDER.asc);
        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("CNT: " + docs.size());

        PlaceRepDoc xDoc = null;
        for (PlaceRepDoc doc : docs) {
            xDoc = doc;
            System.out.println("ID: " + doc.getId() + " --> " + doc.getRevision() + " --> " + doc.getType() + " --> " + doc.getDeleteId() + " --> " + doc.getPlaceDeleteId() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()));
            System.out.println("  DSP: " + doc.getAllDisplayNames());
            System.out.println("  CTR: " + doc.getCentroid());
            for (String varn : doc.getVariantNames()) {
                System.out.println("  VAR: " + varn);
            }
            for (String citn: doc.getCitations()) {
            	System.out.println("  CIT: " + citn);
            }
            for (String xref: doc.getExtXrefs()) {
                System.out.println("  XRF: " + xref);
            }
        }

        // Modify the document ...
        if (xDoc != null) {
//            xDoc.setId("8872031-567789");
//            xDoc.setRevision(567789);
//            xDoc.setForwardRevision(null);
//            xDoc.getCitations().add("64480972|398|464|2014-11-12||-3400409");
//            solrConn.add(xDoc);
//            solrConn.commit();
        }
        System.exit(0);
    }
}
