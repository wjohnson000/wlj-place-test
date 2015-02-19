package std.wlj.solr;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.CitationBridge;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;


public class SearchMasterById {

    public static void main(String... args) throws PlaceDataException {
//        String solrHome = "http://localhost:8983/solr/places";
        String solrHome = "http://familysearch.org/int-solr/places";

        System.setProperty("solr.solr.home", solrHome);
        System.setProperty("solr.master.url", solrHome);
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        SolrConnection solrConn = SolrConnection.connectToRemoteInstance(solrHome);

        // Do a look-up by documents ...
        Map<Integer,PlaceRepDoc> uniqueDocs = new TreeMap<>();
        SolrQuery query = new SolrQuery("repId:7095506");
//        SolrQuery query = new SolrQuery("ownerId:4219431");
        query.setSort("revision", SolrQuery.ORDER.asc);
        query.setRows(32);
        List<PlaceRepDoc> docs = solrConn.search(query);
//        System.out.println("CNT: " + docs.size());
//        for (PlaceRepDoc doc : docs) {
//            PlaceRepDoc currDoc = uniqueDocs.get(doc.getRepId());
//            if (currDoc == null) {
//                uniqueDocs.put(doc.getRepId(), doc);
//            } else if (doc.getRevision() > currDoc.getRevision()) {
//                uniqueDocs.put(doc.getRepId(), doc);
//            }
//        }

        for (PlaceRepDoc doc : docs) {
            System.out.println("ID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()) + " --> " + doc.getRevision());
            System.out.println("  Place:  " + doc.getPlaceId());
            System.out.println("  F-Rev:  " + doc.getForwardRevision());
            System.out.println("  D-Name: " + doc.getDisplayNameMap());
            System.out.println("  P-Name: " + doc.getNames());
            System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
//            for (CitationBridge citn : doc.getAllCitations()) {
//                System.out.println("   " + citn.getCitationId() + " . " + citn.getSourceRef() + " . " + citn.getDescription());
//            }
        }

        System.exit(0);
    }
}
