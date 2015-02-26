package std.wlj.solr;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;


public class SearchForInvalidRevision {

    public static void main(String... args) throws PlaceDataException {
//        String solrHome = "http://localhost:8983/solr/places";
        String solrHome = "http://familysearch.org/int-solr/places";

        System.setProperty("solr.solr.home", solrHome);
        System.setProperty("solr.master.url", solrHome);
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        SolrConnection solrConn = SolrConnection.connectToRemoteInstance(solrHome);

        // Do a look-up by documents ...
        int start = 1;
        for (int rev=786979;  rev<=800000;  rev+=32) {
            System.out.println(">>>>>>>>>>>>>>>>> " + rev + " <<<<<<<<<<<<<<<<<<<<");
            SolrQuery query = new SolrQuery("revision:[" + rev + " TO "  + (rev+31) + "]");
            query.setStart(start);
            query.setRows(100);
            query.setSort("revision", SolrQuery.ORDER.asc);
            List<PlaceRepDoc> docs = solrConn.search(query);
            for (PlaceRepDoc doc : docs) {
                if (doc.getForwardRevision() != null  &&  doc.getRevision() == doc.getForwardRevision()) {
                    System.out.println("ID: " + doc.getId() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()));
                    System.out.println("  Place:  " + doc.getPlaceId());
                    System.out.println("  F-Rev:  " + doc.getForwardRevision());
                    System.out.println("  D-Name: " + doc.getDisplayNameMap());
                    System.out.println("  P-Name: " + doc.getNames());
                    System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
                }
            }
        }

        solrConn.shutdown();
        System.exit(0);
    }
}
