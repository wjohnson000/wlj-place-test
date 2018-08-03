package std.wlj.solr;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.AttributeBridge;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;

public class SearchMasterById {

    static final int MAX_ROWS = 50;
    static final DateFormat SOLR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T00:00:00Z'"); 

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsDev55Connection(true);
        System.out.println("Write-Ready: " + solrConn.isWriteReady());

//        SolrQuery query = new SolrQuery("*:*");
        SolrQuery query = new SolrQuery("repId:1626022");
//        SolrQuery query = new SolrQuery("ownerId:2546");
//        SolrQuery query = new SolrQuery("repId:(10301415 10729281)");
//        SolrQuery query = new SolrQuery("repId:[6893967 TO 6894017]");
//        SolrQuery query = new SolrQuery("ownerId:1");
//        SolrQuery query = new SolrQuery("usa");
//        SolrQuery query = new SolrQuery("id:GROUP-HIERARCHY");
//        SolrQuery query = new SolrQuery("parentId:6085");
//        SolrQuery query = new SolrQuery("repIdChain:7099871");
//        SolrQuery query = new SolrQuery("forwardRevision:[* TO *]");
//        SolrQuery query = new SolrQuery("_root_:[* TO *]");
//        SolrQuery query = new SolrQuery("type:81");
//        SolrQuery query = new SolrQuery("type:81 AND -deleteId:*");
//        SolrQuery query = new SolrQuery("typeGroup:[* TO *]");
//        SolrQuery query = new SolrQuery("published:1 AND !centroid:[-90,-180 TO 90,180] AND !deleteId:[* TO *]");
//        SolrQuery query = new SolrQuery("prefLocale:grk-Latn-x-nga");
//        SolrQuery query = new SolrQuery("!deleteId:[* TO *] AND placeDeleteId:[* TO *]");
//        SolrQuery query = new SolrQuery("citSourceId:[11 TO 1473]");
//        SolrQuery query = new SolrQuery("attributes:1328427*");
//        SolrQuery query = new SolrQuery("attrValue:Specifically*");
//        Calendar cnow = Calendar.getInstance();
//        cnow.add(Calendar.HOUR_OF_DAY, -1);
//        Date dnow = new Date(cnow.getTimeInMillis());
//        SolrQuery query = new SolrQuery("lastUpdateDate: [" + SOLR_DATE_FORMAT.format(dnow) + " TO *]");

//        SolrQuery query = new SolrQuery("type:81");
//        query.addFilterQuery("-deleteId:[0 TO *]");
//        query.addFilterQuery("deleteId:0");

        query.setRows(MAX_ROWS);
//        query.setSort("repId", SolrQuery.ORDER.desc);
//        query.setSort("lastUpdateDate", SolrQuery.ORDER.desc);
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

            for (AttributeBridge ab : doc.getAllAttributes()) {
                System.out.println("AB: " + ab.getAttributeId() + " -> " + ab.getUrl() + " -> " + ab.getUrlTitle());
            }
        }
        
//        for (PlaceRepDoc doc : docs) {
//            String dispName = doc.getDisplayName("en");
//            if (dispName == null) dispName = doc.getDisplayName(doc.getPrefLocale());
//
//            StringBuilder buff = new StringBuilder();
//            buff.append(doc.getRepId());
//            buff.append("|").append(doc.getPlaceId());
//            buff.append("|").append(doc.getPlaceDeleteId());
//            buff.append("|").append(Arrays.toString(doc.getJurisdictionIdentifiers()));
//            buff.append("|").append(doc.getRevision());
//            buff.append("|").append(doc.getType());
//            buff.append("|").append(doc.getPrefLocale());
//            buff.append("|").append(doc.getCentroid());
//            buff.append("|").append(doc.isPublished());
//            buff.append("|").append(doc.isValidated());
//            buff.append("|").append(doc.getLastUpdateDate());
//            buff.append("|").append(dispName);
//
//            System.out.println(buff.toString());
//        }

        System.exit(0);
    }
}
