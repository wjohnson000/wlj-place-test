package std.wlj.solr;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.appdata.AppDataManager;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;

public class SearchLocal {

    private static final int MAX_ROWS = 20;

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.localEmbeddedConnection("C:/D-drive/solr/standalone-7.1.0");

        // Do a look-up by documents ...
        SolrQuery query = new SolrQuery("repId:3779606");
//        SolrQuery query = new SolrQuery("xref:[* TO *]");
//        SolrQuery query = new SolrQuery("appData: *");
//        query.addField("lastUpdateDate:[NOW-1YEAR/DAY TO NOW/DAY+1DAY]");  // &NOW=" + System.currentTimeMillis());
//        SolrQuery query = new SolrQuery("lastUpdateDate:[NOW-7DAY TO NOW]");
        query.setSort("repId", SolrQuery.ORDER.asc);
        query.setRows(32);
        //      query.addFilterQuery("-deleteId:[* TO *]");
        System.out.println("QQ: " + query);
        List<PlaceRepDoc> docs = solrConn.search(query);

        System.out.println("CNT: " + docs.size());
        for (PlaceRepDoc doc : docs) {
            if (AppDataManager.isAppDataDoc(doc)) {
                Set<String> foundIds = new HashSet<>();
                System.out.println("ID: " + doc.getId() + " --> " + doc.getType() + " --> " + doc.getRevision());
                System.out.println("  Dates:  " + doc.getCreateDate() + " . " + doc.getLastUpdateDate());
                for (String appData : doc.getAppData()) {
                    int ndx = appData.indexOf('|');
                    if (ndx > 0) {
                        String sId = appData.substring(0, ndx);
                        if (! foundIds.contains(sId)) {
                            foundIds.add(sId);
                            System.out.println("  " + appData);
                        }
                    }
                }
                continue;
            }

            System.out.println("\nID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()) + " --> " + doc.getRevision());
            System.out.println("  Place:  " + doc.getPlaceId());
            System.out.println("  Par-Id: " + doc.getParentId());
            System.out.println("  Typ-Id: " + doc.getType());
            System.out.println("  P-Rang: " + doc.getOwnerStartYear() + " - " + doc.getOwnerEndYear());
            System.out.println("  FromTo: " + doc.getStartYear() + " - " + doc.getEndYear());
            System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
            System.out.println("  Locatn: " + doc.getCentroid() + " . " + doc.getLatitude() + "," + doc.getLongitude());
            System.out.println("  Publsh: " + doc.isPublished());
            System.out.println("  Validd: " + doc.isValidated());
            System.out.println("  Creatd: " + doc.getCreateDate() + " . " + doc.getLastUpdateDate());

            doc.getDisplayNames().stream().limit(MAX_ROWS).forEach(dispName -> System.out.println("  D-Name: " + dispName));
            doc.getVariantNames().stream().limit(MAX_ROWS).forEach(varName -> System.out.println("  V-Name: " + varName));
            doc.getNames().stream().limit(MAX_ROWS).forEach(nName -> System.out.println("  N-Name: " + nName));
            doc.getAttributes().stream().limit(MAX_ROWS).forEach(attr -> System.out.println("    Attr: " + attr));
            doc.getCitations().stream().limit(MAX_ROWS).forEach(citn -> System.out.println("    Citn: " + citn));
            doc.getAltJurisdictions().stream().limit(MAX_ROWS).forEach(altJuris -> System.out.println("    AltJ: " + altJuris));
            doc.getExtXrefs().stream().limit(MAX_ROWS).forEach(xref -> System.out.println("    Xref: " + xref));
            doc.getAppData().stream().limit(MAX_ROWS).forEach(appData -> System.out.println("    AppD: " + appData));
        }

        System.exit(0);
    }
}
