package std.wlj.solr;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;

public class SearchMasterByPlaceIdNoRep {

    public static int[] placeIds = {
        8021097, 8019173, 8429594, 8238440, 8020888, 8021649, 8276147,
        8050775, 8002313, 8614943, 8529253, 8441317, 8553362, 8174055,
        8529248, 8516669, 8555838, 8030707, 5060636, 8398969, 8197171
    };

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsProdConnection(true);

        for (int placeId : placeIds) {
            SolrQuery query = new SolrQuery("ownerId:" + placeId);
            query.setRows(32);
            query.setSort("revision", SolrQuery.ORDER.asc);
            List<PlaceRepDoc> docs = solrConn.search(query);
            System.out.println("\nID: " + placeId + " --> CNT: " + docs.size());

            for (PlaceRepDoc doc : docs) {
                System.out.println("ID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()) + " --> " + doc.getRevision());
                System.out.println("  Place:  " + doc.getPlaceId());
                System.out.println("  Owner:  " + doc.getOwnerId());
                System.out.println("  Par-Id: " + doc.getParentId());
                System.out.println("  D-Name: " + doc.getDisplayNameMap());
                System.out.println("  P-Rang: " + doc.getOwnerStartYear() + " - " + doc.getOwnerEndYear());
                System.out.println("  FromTo: " + doc.getFromYear() + " - " + doc.getToYear());
                System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
                for (String appData : doc.getVariantNames()) {
                    System.out.println("  " + appData);
                }
            }
        }

        System.exit(0);
    }
}
