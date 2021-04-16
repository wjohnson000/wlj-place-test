package std.wlj.solr;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.appdata.AppDataManager;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;


public class VerifySolrData {

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsProdConnection(false);

        // First query should return only the "APP-DATA" documents.  Second should return nothing ...
        executeQuery(solrConn, "id:*-* -id:PLACE*");
        executeQuery(solrConn, "forwardRevision:[* TO *]");

        System.exit(0);
    }

    private static void executeQuery(SolrConnection solrConn, String queryStr) throws PlaceDataException {
        SolrQuery query = new SolrQuery(queryStr);
        query.setSort("revision", SolrQuery.ORDER.asc);
        query.setRows(132);
        List<PlaceRepDoc> docs = solrConn.search(query);

        System.out.println();
        System.out.println("=================================================================================================");
        System.out.println("Query: " + queryStr);
        System.out.println("=================================================================================================");
        System.out.println("CNT: " + docs.size());
        for (PlaceRepDoc doc : docs) {
            if (AppDataManager.isAppDataDoc(doc)) {
                Set<String> foundIds = new HashSet<>();
                System.out.println("ID: " + doc.getId() + " --> " + doc.getType() + " --> " + doc.getRevision());
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
            } else {
                System.out.println("ID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()) + " --> " + doc.getRevision());
                System.out.println("  Place:  " + doc.getPlaceId());
                System.out.println("  D-Name: " + doc.getDisplayNameMap());
                System.out.println("  P-Name: " + doc.getNames());
                System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
                System.out.println("  Chain:  " + Arrays.toString(doc.getRepIdChainAsInt()));
            }

        }
    }
}
