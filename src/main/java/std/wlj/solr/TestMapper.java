package std.wlj.solr;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.data.AttributeBridge;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.ws.model.AttributeModel;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.mapper.AttributeMapper;
import std.wlj.util.SolrManager;

public class TestMapper {

    static final int MAX_ROWS = 50;
    static final DateFormat SOLR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T00:00:00Z'"); 

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsBetaConnection(true);
        SolrService    solrSvc  = SolrManager.awsBetaService(true);
        System.out.println("Write-Ready: " + solrConn.isWriteReady());

        AttributeMapper mapper = new AttributeMapper(solrSvc);
        SolrQuery query = new SolrQuery("repId:5337344");

        query.setRows(MAX_ROWS);
        System.out.println("QRY: " + query);

        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("CNT: " + docs.size());

        for (PlaceRepDoc doc : docs) {
            doc.setDataService(solrSvc);
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

            doc.getDisplayNames().stream().limit(MAX_ROWS).forEach(dispName -> System.out.println("  D-Name: " + dispName));
            doc.getVariantNames().stream().limit(MAX_ROWS).forEach(varName -> System.out.println("  V-Name: " + varName));
            doc.getNames().stream().limit(MAX_ROWS).forEach(nName -> System.out.println("  N-Name: " + nName));
            doc.getAttributes().stream().limit(MAX_ROWS).forEach(attr -> System.out.println("    Attr: " + attr));
            doc.getCitations().stream().limit(MAX_ROWS).forEach(citn -> System.out.println("    Citn: " + citn));
            doc.getAltJurisdictions().stream().limit(MAX_ROWS).forEach(altJuris -> System.out.println("    AltJ: " + altJuris));
            doc.getExtXrefs().stream().limit(MAX_ROWS).forEach(xref -> System.out.println("    Xref: " + xref));
            doc.getAppData().stream().limit(MAX_ROWS).forEach(appData -> System.out.println("    AppD: " + appData));

            for (AttributeBridge ab : doc.getAllAttributes()) {
                AttributeModel attrModel = mapper.createModelFromBridge(ab, StdLocale.ENGLISH, "http://localhost:8080/meef");
                System.out.println("AB: " + ab.getAttributeId() + " -> " + ab.getUrl() + " -> " + ab.getUrlTitle());
                System.out.println("    " + attrModel.toString());
            }
        }
        
        System.exit(0);
    }
}
