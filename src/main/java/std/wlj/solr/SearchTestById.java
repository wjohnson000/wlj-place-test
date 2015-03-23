package std.wlj.solr;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;


public class SearchTestById {

    public static void main(String... args) throws PlaceDataException {
        String solrHome = "http://place-solr-dev.dev.fsglobal.org/int-solr/places";

        System.setProperty("solr.solr.home", solrHome);
        System.setProperty("solr.master.url", solrHome);
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        SolrConnection solrConn = SolrConnection.connectToRemoteInstance(solrHome);

        // Do a look-up by documents ...
        for (int repId=9007999;  repId<9008998;  repId+=29) {
            Map<Integer,PlaceRepDoc> uniqueDocs = new TreeMap<>();
            SolrQuery query = new SolrQuery("repId:" + repId);
            query.setSort("repId", SolrQuery.ORDER.asc);
            query.setRows(10);
            List<PlaceRepDoc> docs = solrConn.search(query);
            System.out.println("REP-ID: " + repId + " --> CNT: " + docs.size());
            for (PlaceRepDoc doc : docs) {
                PlaceRepDoc currDoc = uniqueDocs.get(doc.getRepId());
                if (currDoc == null) {
                    uniqueDocs.put(doc.getRepId(), doc);
                } else if (doc.getRevision() > currDoc.getRevision()) {
                    uniqueDocs.put(doc.getRepId(), doc);
                }
            }

            for (PlaceRepDoc doc : uniqueDocs.values()) {
                System.out.println("ID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()) + " --> " + doc.getRevision());
//                System.out.println("  Place:  " + doc.getPlaceId());
//                System.out.println("  F-Rev:  " + doc.getForwardRevision());
//                System.out.println("  D-Name: " + doc.getDisplayNameMap());
//                System.out.println("  P-Name: " + doc.getNames());
//                System.out.println("  P-Rang: " + doc.getOwnerStartYear() + " - " + doc.getOwnerEndYear());
//                System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
                for (String appData : doc.getVariantNames()) {
                    System.out.println("  " + appData);
                }
            }
        }
        System.exit(0);
    }
}
