package std.wlj.solr;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;


/**
 * Fix the chain in this documents ...
 * @author wjohnson000
 *
 */
public class FixSolrRepIdChain {

    public static void main(String... args) throws PlaceDataException {
        String solrHome = "http://familysearch.org/int-solr/places";

        System.setProperty("solr.solr.home", solrHome);
        System.setProperty("solr.master.url", solrHome);
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        SolrConnection solrConn = SolrConnection.connectToRemoteInstance(solrHome);

        // Do a look-up by documents ...
        SolrQuery query = new SolrQuery("id:8313703-*");
        query.setRows(1000);
        query.setSort("revision", ORDER.asc);
        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("CNT: " + docs.size());

        PlaceRepDoc xDoc = null;
        for (PlaceRepDoc doc : docs) {
            xDoc = doc;
            System.out.println("ID: " + doc.getId() + " --> " + doc.getRevision() + " --> " + doc.getType() + " --> " + doc.getDeleteId() + " --> " + doc.getPlaceDeleteId() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()));
            System.out.println("  PAR: " + doc.getParentId());
            System.out.println("  CHN: " + Arrays.toString(doc.getRepIdChain()));
            System.out.println("  CHN: " + Arrays.toString(doc.getRepIdChainAsInt()));
            System.out.println("  FWD: " + doc.getForwardRevision());
        }

        // Modify the document ...
        if (xDoc != null) {
//            int[] newChain = new int[xDoc.getRepIdChainAsInt().length];
//            System.arraycopy(xDoc.getRepIdChainAsInt(), 0, newChain, 1, newChain.length-1);
//            newChain[0] = xDoc.getRepId();
//            newChain[1] = xDoc.getParentId();
//            System.out.println("NewChain: " + Arrays.toString(newChain));
//            xDoc.setRepIdChainAsInt(newChain);
//            solrConn.add(xDoc);
//            solrConn.commit();
        }
        System.exit(0);
    }
}
