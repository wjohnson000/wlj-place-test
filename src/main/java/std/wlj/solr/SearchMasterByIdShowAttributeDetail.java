package std.wlj.solr;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.AttributeBridge;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;


public class SearchMasterByIdShowAttributeDetail {

    private static final int MAX_ROWS = 20;

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsProdConnection(true);
        System.out.println("Write-Ready: " + solrConn.isWriteReady());

        SolrQuery query = new SolrQuery("repId:1");
        query.setRows(12);
        System.out.println("QRY: " + query);

        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("CNT: " + docs.size());

        for (PlaceRepDoc doc : docs) {
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

            System.out.println();
            List<AttributeBridge> attrBs = doc.getAllAttributes();
            for (AttributeBridge attrB : attrBs) {
                System.out.println("AttrId: " + attrB.getAttributeId());
                System.out.println("FromYr: " + attrB.getFromYear());
                System.out.println("  ToYr: " + attrB.getToYear());
                System.out.println("Locale: " + attrB.getLocale());
                System.out.println(" Value: " + attrB.getValue());
                System.out.println("CpRght: " + attrB.getCopyrightNotice());
                System.out.println("CprUrl: " + attrB.getCopyrightUrl());
            }
        }

        System.exit(0);
    }
}
