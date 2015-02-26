package std.wlj.solr;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.AppDataManager;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;


public class SearchLocal {

    public static void main(String... args) throws PlaceDataException {
        String solrHome = "C:/Users/wjohnson000/.places/test-delete-me";

        System.setProperty("solr.solr.home", solrHome);
        System.setProperty("solr.master.url", "");
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        SolrConnection solrConn = SolrConnection.connectToEmbeddedInstance(solrHome, false);

        // Do a look-up by documents ...
        SolrQuery query = new SolrQuery("id:*");
        query.setSort("repId", SolrQuery.ORDER.asc);
        query.setRows(132);
        List<PlaceRepDoc> docs = solrConn.search(query);

        for (PlaceRepDoc doc : docs) {
            if (AppDataManager.isAppDataDoc(doc)) {
                continue;
            }

            System.out.println("ID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()) + " --> " + doc.getRevision());
            System.out.println("  Place:  " + doc.getPlaceId());
            System.out.println("  F-Rev:  " + doc.getForwardRevision());
            System.out.println("  D-Name: " + doc.getDisplayNameMap());
            System.out.println("  P-Name: " + doc.getNames());
            System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
            for (String extXref : doc.getExtXrefs()) {
                System.out.println("  Ext-Xref: " + extXref);
            }
        }

        System.exit(0);
    }
}
