/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.redis;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.AttributeBridge;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.util.SolrManager;

/**
 * @author wjohnson000
 *
 */
public class TestRepSerialization {

    static final int MAX_ROWS = 15;
    static final DateFormat SOLR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T00:00:00Z'"); 

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsBetaConnection(true);
        SolrService    solrSvc  = SolrManager.awsBetaService(true);

        SolrQuery query = new SolrQuery("repId:(120 240 360)");
        query.setRows(MAX_ROWS);
        query.setSort("repId", SolrQuery.ORDER.desc);
        System.out.println("QRY: " + query);

        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("CNT: " + docs.size());

        for (PlaceRepDoc doc : docs) {
            System.out.println("\n\n");
            dumpRepDoc(doc);
            try {
                long time0 = System.nanoTime();
                byte[] json = RedisSerializer.placeRepBridgeToBytes(doc);
                long time1 = System.nanoTime();
                System.out.println("JSON: " + json.length + " --> " + (time1 - time0) / 1_000_000.0);

                time0 = System.nanoTime();
                PlaceRepBridge repB = RedisSerializer.bytesToPlaceRepBridge(json);
                time1 = System.nanoTime();
                System.out.println("\nRepB.class=" + repB.getClass().getName() + " --> " + (time1 - time0) / 1_000_000.0);

                if (repB instanceof PlaceRepDoc) {
                    PlaceRepDoc repDoc = (PlaceRepDoc)repB;
                    repDoc.setDataService(solrSvc);
                    dumpRepDoc((PlaceRepDoc)repB);
                }
            } catch(Exception ex) {
                System.out.println("REDIS.OOPS: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        System.exit(0);
    }

    static void dumpRepDoc(PlaceRepDoc doc) {
        System.out.println("\nID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()) + " --> " + doc.getRevision());
        System.out.println("  Place:  " + doc.getPlaceId());
        System.out.println("  Par-Id: " + doc.getParentId());
        System.out.println("  Typ-Id: " + doc.getType());
        System.out.println("  Locale: " + doc.getPrefLocale());
        System.out.println("  P-Rang: " + doc.getOwnerStartYear() + " - " + doc.getOwnerEndYear());
        System.out.println("  FromTo: " + doc.getStartYear() + " - " + doc.getEndYear());
        System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
        System.out.println("  Locatn: " + doc.getCentroid() + " . " + doc.getLatitude() + "," + doc.getLongitude());
        System.out.println("  Publsh: " + doc.isPublished());
        System.out.println("  Validd: " + doc.isValidated());
        System.out.println("  PrefBd: " + doc.getPreferredBoundaryId());
        System.out.println("  Creatd: " + doc.getCreateDate() + " . " + doc.getLastUpdateDate());
        System.out.println("  TGroup: " + doc.getTypeGroup());
        System.out.println("  TGroup: " + doc.getTypeGroup());

        doc.getDisplayNames().stream().limit(MAX_ROWS).forEach(dispName -> System.out.println("  D-Name: " + dispName));
        doc.getVariantNames().stream().limit(MAX_ROWS).forEach(varName -> System.out.println("  V-Name: " + varName));
        doc.getNames().stream().limit(MAX_ROWS).forEach(nName -> System.out.println("  N-Name: " + nName));
        doc.getAttributes().stream().limit(MAX_ROWS).forEach(attr -> System.out.println("    Attr: " + attr));
        doc.getCitations().stream().limit(MAX_ROWS).forEach(citn -> System.out.println("    Citn: " + citn));
        doc.getAltJurisdictions().stream().limit(MAX_ROWS).forEach(altJuris -> System.out.println("    AltJ: " + altJuris));
        doc.getExtXrefs().stream().limit(MAX_ROWS).forEach(xref -> System.out.println("    Xref: " + xref));
        doc.getAppData().stream().limit(MAX_ROWS).forEach(appData -> System.out.println("    AppD: " + appData));

        for (AttributeBridge ab : doc.getAllAttributes()) {
            System.out.println("AB: " + ab.getAttributeId() + " -> " + ab.getUrl() + " -> " + ab.getUrlTitle() + " --> " + ab.getTitle());
        }
    }
}
