package std.wlj.solr;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;


public class SearchDevById {

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsDevConnection(true);

        // Do a look-up by documents ...
        Map<Integer,PlaceRepDoc> uniqueDocs = new TreeMap<>();
        SolrQuery query = new SolrQuery("id:553594-*");
//        SolrQuery query = new SolrQuery("ownerId:3491780");
        query.setSort("revision", SolrQuery.ORDER.asc);
        query.setRows(32);
        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("CNT: " + docs.size());
        for (PlaceRepDoc doc : docs) {
            PlaceRepDoc currDoc = uniqueDocs.get(doc.getRepId());
            if (currDoc == null) {
                uniqueDocs.put(doc.getRepId(), doc);
            } else if (doc.getRevision() > currDoc.getRevision()) {
                uniqueDocs.put(doc.getRepId(), doc);
            }
        }

        for (PlaceRepDoc doc : docs) {
            System.out.println("ID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()) + " --> " + doc.getRevision());
            System.out.println("  Place:  " + doc.getPlaceId());
            System.out.println("  D-Name: " + doc.getDisplayNameMap());
            System.out.println("  P-Name: " + doc.getNames());
            System.out.println("  P-Rang: " + doc.getOwnerStartYear() + " - " + doc.getOwnerEndYear());
            System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
//            for (String citn : doc.getCitations()) {
//                System.out.println("  Citn:  " + citn);
//            }
            for (String attr : doc.getAttributes()) {
                System.out.println("  Attr:  " + attr);
            }
        }

        System.exit(0);
    }
}
