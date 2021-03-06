package std.wlj.solr;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;


public class DumpAppDataDocs {

    private static final String[] docIds = {
        "ATTRIBUTE-TYPE",
        "CITATION-TYPE",
        "EXT-XREF-TYPE",
        "NAME-TYPE",
        "PLACE-TYPE",
        "RESOLUTION-TYPE",
        "FEEDBACK-RESOLUTION-TYPE",
        "FEEDBACK-STATE-TYPE",
        "REP-RELATION",

        "SOURCE",
        "GROUP-HIERARCHY",
        "NAME-PRIORITY",
    };

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsDevConnection(true);

        // Do a look-up by doc-id
        SolrQuery query;
        for (String docId : docIds) {
            query = new SolrQuery("id:" + docId);
            query.setRows(2);
            query.setSort("revision", SolrQuery.ORDER.asc);
            List<PlaceRepDoc> docs = solrConn.search(query);
            if (docs.isEmpty()) {
                continue;
            }

            PlaceRepDoc doc = docs.get(0);
            System.out.println("ID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()) + " --> " + doc.getRevision());
            System.out.println("  Place:  " + doc.getPlaceId());
            System.out.println("  D-Name: " + doc.getDisplayNameMap());
            System.out.println("  P-Name: " + doc.getNames());
            doc.getAppData().forEach(appd -> System.out.println("  AppD: " + appd));
        }

        solrConn.shutdown();
        System.exit(0);
    }
}
