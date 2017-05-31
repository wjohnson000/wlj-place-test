package std.wlj.jira;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;


public class FixSolr8913097 {

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsProdConnection(false);

        // Do a look-up by documents ...
        SolrQuery query = new SolrQuery("id:8913097-*");
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
//            xDoc.setId("8913097-567287");
//            xDoc.setRevision(567287);
//            xDoc.setForwardRevision(null);
//            xDoc.getCitations().add("64480772|398|460|2014-11-12|บ้านบ่อ|13760535");
//            solrConn.add(xDoc);
//            solrConn.commit();
        }
        System.exit(0);
    }
}
