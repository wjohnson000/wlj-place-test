package std.wlj.jira;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;


public class FixSolr8555838 {

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsBetaConnection(true);

        // Do a look-up by documents ...
        SolrQuery query = new SolrQuery("id:1111");
        query.setRows(100);
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
//            xDoc.setForwardRevision(null);
            solrConn.add(xDoc);
            solrConn.commit();
        }

        System.exit(0);
    }
}
