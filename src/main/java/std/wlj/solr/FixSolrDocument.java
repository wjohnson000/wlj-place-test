package std.wlj.solr;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;


public class FixSolrDocument {

    public static void main(String... args) throws PlaceDataException {
//        String solrHome = "http://localhost:8983/solr/places";
        String solrHome = "http://familysearch.org/int-solr/places";
//        String solrHome = "http://place-solr-test.dev.fsglobal.org/int-solr/places";

        System.setProperty("solr.solr.home", solrHome);
        System.setProperty("solr.master.url", solrHome);
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        SolrConnection solrConn = SolrConnection.connectToRemoteInstance(solrHome);

        // Do a look-up by documents ...
        SolrQuery query = new SolrQuery("id:9161141-*");  // new SolrQuery("id:8866947-*");
        query.setRows(1000);
        query.setSort("revision", ORDER.asc);
        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("CNT: " + docs.size());

        PlaceRepDoc xDoc = null;
        for (PlaceRepDoc doc : docs) {
            xDoc = doc;
            System.out.println("ID: " + doc.getId() + " --> " + doc.getRevision() + " --> " + doc.getType() + " --> " + doc.getDeleteId() + " --> " + doc.getPlaceDeleteId() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()));
            System.out.println("  FWD: " + doc.getForwardRevision());
            System.out.println("  DSP: " + doc.getAllDisplayNames());
            System.out.println("  CTR: " + doc.getCentroid());
            for (String varn : doc.getVariantNames()) {
                System.out.println("  VAR: " + varn);
            }
            for (String citn: doc.getCitations()) {
            	System.out.println("  CIT: " + citn);
            }
        }

        // Modify the document ...
//        if (xDoc != null) {
//            xDoc.setId("9161141-483440");
//            xDoc.setRevision(483440);
//            xDoc.setForwardRevision(null);
//            xDoc.setCentroid("19.429945,97.855056");
//            solrConn.add(xDoc);
//            solrConn.commit();
//        }
        System.exit(0);
    }
}
