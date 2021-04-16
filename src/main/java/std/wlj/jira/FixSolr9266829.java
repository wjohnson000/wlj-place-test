package std.wlj.jira;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;


public class FixSolr9266829 {

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsProdConnection(false);

        // Do a look-up by documents ...
        SolrQuery query = new SolrQuery("id:1830533-*");
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
//            xDoc.setId("9266829-591351");
//            xDoc.setRevision(591351);
//            xDoc.setForwardRevision(null);
//            xDoc.setCentroid("17.930266,100.61622");
//            xDoc.getCitations().add("64490401|398|460|2014-11-12|ห้วยมะยม|13255813");
//            solrConn.add(xDoc);
//            solrConn.commit();
        }
        System.exit(0);
    }
}
