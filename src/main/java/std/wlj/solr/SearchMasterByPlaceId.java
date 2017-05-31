package std.wlj.solr;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;


public class SearchMasterByPlaceId {

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.localEmbeddedConnection("D:/solr/stand-alone-6.1.0");

        // Do a look-up by place identifier
        Map<Integer,PlaceRepDoc> uniqueDocs = new TreeMap<>();
//        SolrQuery query = new SolrQuery("revision:[786979 TO 800000]");
        SolrQuery query = new SolrQuery("ownerId:8238440");
//        SolrQuery query = new SolrQuery("parentId:1");
        query.setRows(32);
        query.setSort("revision", SolrQuery.ORDER.asc);
//        query.setFilterQueries("-forwardRevision: [* TO *]");
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
            System.out.println("  Par-Id: " + doc.getParentId());
            System.out.println("  D-Name: " + doc.getDisplayNameMap());
            System.out.println("  P-Rang: " + doc.getOwnerStartYear() + " - " + doc.getOwnerEndYear());
            System.out.println("  FromTo: " + doc.getFromYear() + " - " + doc.getToYear());
            System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
            for (String appData : doc.getVariantNames()) {
                System.out.println("  " + appData);
            }
        }

        System.exit(0);
    }
}
