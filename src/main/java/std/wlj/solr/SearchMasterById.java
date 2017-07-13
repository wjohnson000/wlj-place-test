package std.wlj.solr;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;


public class SearchMasterById {

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsDevConnection(true);
        System.out.println("Write-Ready: " + solrConn.isWriteReady());

        // Do a look-up by documents ...
        Map<Integer,PlaceRepDoc> uniqueDocs = new TreeMap<>();
//        SolrQuery query = new SolrQuery("names:q");
        SolrQuery query = new SolrQuery("repId: 111");
//        SolrQuery query = new SolrQuery("id:NAME-PRIORITY");
//        SolrQuery query = new SolrQuery("parentId:1442484");
//      SolrQuery query = new SolrQuery("repIdChain:7099871");
        query.setRows(10);
        query.setSort("repId", SolrQuery.ORDER.desc);

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
            System.out.println("  Typ-Id: " + doc.getType());
            System.out.println("  D-Name: " + doc.getDisplayNameMap());
            System.out.println("  P-Rang: " + doc.getOwnerStartYear() + " - " + doc.getOwnerEndYear());
            System.out.println("  FromTo: " + doc.getFromYear() + " - " + doc.getToYear());
            System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
            System.out.println("  Locatn: " + doc.getCentroid() + " . " + doc.getLatitude() + " . " + doc.getLongitude());
            System.out.println("  Publsh: " + doc.isPublished());
            System.out.println("  Valitd: " + doc.isValidated());
            System.out.println("  Creatd: " + doc.getCreateDate() + " . " + doc.getLastUpdateDate());
            for (String varName : doc.getVariantNames()) {
                System.out.println("  V-Name: " + varName);
            }
            for (String attr : doc.getAttributes()) {
                System.out.println("    Attr: " + attr);
            }
            for (String citn : doc.getCitations()) {
                System.out.println("    Citn: " + citn);
            }
            for (String altJuris : doc.getAltJurisdictions()) {
                System.out.println("    AltJ: " + altJuris);
            }
            for (String extXrefs : doc.getExtXrefs()) {
                System.out.println("    Xref: " + extXrefs);
            }
            for (String appD : doc.getAppData()) {
                System.out.println("    Appd: " + appD);
            }
        }

        System.exit(0);
    }
}
