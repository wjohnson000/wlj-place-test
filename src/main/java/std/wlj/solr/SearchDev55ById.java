package std.wlj.solr;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.util.SolrManager;


public class SearchDev55ById {

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsDevConnection(true);
        SolrService    solrSvc  = SolrManager.awsDevService(true);

        // Do a look-up by documents ...
        //        SolrQuery query = new SolrQuery("id:REP-RELATION");
        //        SolrQuery query = new SolrQuery("ownerId:-1");
        SolrQuery query = new SolrQuery("parentId:244895");
        query.setRows(10);
        query.setSort("revision", SolrQuery.ORDER.asc);
        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("CNT: " + docs.size());

        for (PlaceRepDoc doc : docs) {
            doc.setDataService(solrSvc);
            for (int i=0;  i<12;  i++) {
                new Thread(() -> printDoc(doc)).start();
            }
        }

        try { Thread.sleep(1111L); } catch(Exception ex) { }
        System.exit(0);
    }

    static void printDoc(PlaceRepDoc doc) {
        System.out.println("ID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()) + " --> " + doc.getRevision());
        System.out.println("  Juris:  " + Arrays.toString(doc.getResolvedJurisdictions()));
        System.out.println("  Place:  " + doc.getPlaceId());
        System.out.println("  Par-Id: " + doc.getParentId());
        System.out.println("  D-Name: " + doc.getDisplayNameMap());
        System.out.println("  P-Rang: " + doc.getOwnerStartYear() + " - " + doc.getOwnerEndYear());
        System.out.println("  FromTo: " + doc.getFromYear() + " - " + doc.getToYear());
        System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
        System.out.println("  Creatd: " + doc.getCreateDate() + " . " + doc.getLastUpdateDate());
        for (String varName : doc.getVariantNames()) {
            System.out.println("  V-Name: " + varName);
        }
    }
}
