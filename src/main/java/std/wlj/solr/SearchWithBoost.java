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


public class SearchWithBoost {

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsIntConnection(true);

        // Do a look-up by documents ...
        Map<Integer,PlaceRepDoc> uniqueDocs = new TreeMap<>();
        SolrQuery query = new SolrQuery("repId:374148^2.0");
        query.setSort("repId", SolrQuery.ORDER.asc);
        query.setRows(32);
        query.addFilterQuery("parentId:138^1.0");
        System.out.println("QQ: " + query);

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
            System.out.println("  Parent: " + doc.getParentId());
            System.out.println("  D-Name: " + doc.getDisplayNameMap());
            System.out.println("  P-Name: " + doc.getNames());
            System.out.println("  P-Rang: " + doc.getOwnerStartYear() + " - " + doc.getOwnerEndYear());
            System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
            System.out.println("  Dates:  " + doc.getCreateDate() + " . " + doc.getLastUpdateDate());
            for (String altJuris : doc.getAltJurisdictions()) {
                System.out.println("  AJ: " + altJuris);
            }
            for (String appData : doc.getAppData()) {
                System.out.println("  " + appData);
            }
        }

        System.exit(0);
    }
}
