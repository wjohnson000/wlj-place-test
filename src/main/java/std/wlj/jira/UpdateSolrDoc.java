package std.wlj.jira;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;


public class UpdateSolrDoc {

    private static final int MAX_ROWS = 20;

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsDevConnection(true);

        // Do a look-up by documents ...
        SolrQuery query = new SolrQuery("id:4221776*");
        query.setRows(2);
        query.setSort("revision", ORDER.asc);
        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("CNT: " + docs.size());

        PlaceRepDoc xDoc = null;
        for (PlaceRepDoc doc : docs) {
            if (xDoc == null) xDoc = doc;
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

        // Modify the document ...
        if (xDoc != null) {
//            xDoc.setId("9266829-591351");
//            xDoc.setRevision(591351);
//            xDoc.setForwardRevision(null);
//            xDoc.setCentroid("17.930266,100.61622");
//            xDoc.getCitations().add("64490401|398|460|2014-11-12|ห้วยมะยม|13255813");
            xDoc.setDisplayNames(new ArrayList<>());
//            xDoc.setVariantNames(new ArrayList<>());
//            xDoc.setNames(new ArrayList<>());
            solrConn.add(xDoc);
            solrConn.commit();
        }

        System.exit(0);
    }
}
