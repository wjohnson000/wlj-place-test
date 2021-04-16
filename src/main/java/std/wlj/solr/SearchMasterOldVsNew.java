package std.wlj.solr;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.data.solr.SolrDoc;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;

public class SearchMasterOldVsNew {

    static final int MAX_ROWS = 15;
    static final Random random = new Random();

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsBetaConnection(true);
        System.out.println("Write-Ready: " + solrConn.isWriteReady());

        long timeOld = 0L;
        long timeNew = 0L;

        searchOld(solrConn);
//        searchNew(solrConn);

        for (int times=0;  times<1_000;  times++) {
            timeOld += searchOld(solrConn);
//            timeNew += searchNew(solrConn);
        }
        System.out.println("\n\n\n");
        System.out.println("OLD: " + timeOld / 1_000_000.0);
        System.out.println("NEW: " + timeNew / 1_000_000.0);

        solrConn.shutdown();
        System.exit(0);
    }

    static long searchOld(SolrConnection conn) throws PlaceDataException {
        String query = IntStream.of(1, 2, 3)
            .mapToObj(val -> String.valueOf(random.nextInt(11_000_000)))
            .collect(Collectors.joining(" ", "repId:(", ")"));

        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.setRows(MAX_ROWS);
        solrQuery.setSort("repId", SolrQuery.ORDER.desc);
        System.out.println("QRY: " + query);

        long time0 = System.nanoTime();
        List<PlaceRepDoc> docs = conn.search(solrQuery);
        long time1 = System.nanoTime();

        for (PlaceRepDoc doc : docs) {
            System.out.println("\nOLD.ID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getRepIdChainAsInt()) + " --> " + doc.getRevision());
            System.out.println("  Place:  " + doc.getPlaceId());
            System.out.println("  Par-Id: " + doc.getParentId());
            System.out.println("  Typ-Id: " + doc.getType());
            System.out.println("  Locale: " + doc.getPrefLocale());
            System.out.println("  P-Rang: " + doc.getOwnerStartYear() + " - " + doc.getOwnerEndYear());
            System.out.println("  FromTo: " + doc.getStartYear() + " - " + doc.getEndYear());
            System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
            System.out.println("  Locatn: " + doc.getCentroid());
            System.out.println("  Publsh: " + doc.getPublished());
            System.out.println("  Validd: " + doc.getValidated());
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
        }

        return time1 - time0;
    }

//    static long searchNew(SolrConnection conn) throws PlaceDataException {
//        String query = IntStream.of(1, 2, 3)
//                .mapToObj(val -> String.valueOf(random.nextInt(11_000_000)))
//                .collect(Collectors.joining(" ", "repId:(", ")"));
//
//        SolrQuery solrQuery = new SolrQuery(query);
//        solrQuery.setRows(MAX_ROWS);
//        solrQuery.setSort("repId", SolrQuery.ORDER.desc);
//        System.out.println("QRY: " + query);
//
//        long time0 = System.nanoTime();
//        List<SolrDoc> docs = conn.searchNew(solrQuery);
//        long time1 = System.nanoTime();
//
//        for (SolrDoc doc : docs) {
//            System.out.println("\nNEW.ID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getRepIdChainAsInt()) + " --> " + doc.getRevision());
//            System.out.println("  Place:  " + doc.getOwnerId());
//            System.out.println("  Par-Id: " + doc.getParentId());
//            System.out.println("  Typ-Id: " + doc.getType());
//            System.out.println("  Locale: " + doc.getPrefLocale());
//            System.out.println("  P-Rang: " + doc.getOwnerStartYear() + " - " + doc.getOwnerEndYear());
//            System.out.println("  FromTo: " + doc.getStartYear() + " - " + doc.getEndYear());
//            System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
//            System.out.println("  Locatn: " + doc.getCentroid());
//            System.out.println("  Publsh: " + doc.getPublished());
//            System.out.println("  Validd: " + doc.getValidated());
//            System.out.println("  PrefBd: " + doc.getPreferredBoundaryId());
//            System.out.println("  Creatd: " + doc.getCreateDate() + " . " + doc.getLastUpdateDate());
//            System.out.println("  TGroup: " + doc.getTypeGroup());
//
//            doc.getDisplayNames().stream().limit(MAX_ROWS).forEach(dispName -> System.out.println("  D-Name: " + dispName));
//            doc.getVariantNames().stream().limit(MAX_ROWS).forEach(varName -> System.out.println("  V-Name: " + varName));
//            doc.getNames().stream().limit(MAX_ROWS).forEach(nName -> System.out.println("  N-Name: " + nName));
//            doc.getAttributes().stream().limit(MAX_ROWS).forEach(attr -> System.out.println("    Attr: " + attr));
//            doc.getCitations().stream().limit(MAX_ROWS).forEach(citn -> System.out.println("    Citn: " + citn));
//            doc.getAltJurisdictions().stream().limit(MAX_ROWS).forEach(altJuris -> System.out.println("    AltJ: " + altJuris));
//            doc.getExtXrefs().stream().limit(MAX_ROWS).forEach(xref -> System.out.println("    Xref: " + xref));
//            doc.getAppData().stream().limit(MAX_ROWS).forEach(appData -> System.out.println("    AppD: " + appData));
//        }
//
//        return time1 - time0;
//    }
}
