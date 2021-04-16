package std.wlj.solr.helper;

import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.exceptions.PlaceDataException;


public class SearchSolrTimings {

    public static void main(String... args) throws PlaceDataException {
        SolrConnectionX solrConn = SolrConnectionX.connectToEmbeddedInstance("D:/solr/newbie-6.1.0");
        System.out.println("SOLR-conn: " + solrConn);

        // Do a look-up by documents ...
        SolrQuery query = new SolrQuery("names:indiana");
        query.setSort("repId", SolrQuery.ORDER.desc);
        query.setRows(1111);

        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println(">>>>> CNT-0: " + docs.size());

        for (PlaceRepDoc doc : docs) {
            System.out.println("ID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()) + " --> " + doc.getRevision());
            System.out.println("  Place:  " + doc.getPlaceId());
            System.out.println("  Par-Id: " + doc.getParentId());
            System.out.println("  D-Name: " + doc.getDisplayNameMap());
            System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
            for (String varName : doc.getVariantNames()) {
                System.out.println("  V-Name: " + varName);
            }
        }

        List<int[]> ids = solrConn.searchLite(query);
        System.out.println(">>>>> CNT-1: " + ids.size());
        ids.forEach(id -> System.out.println("  Rep-Id: " + id[0] + ";  Revision: " + id[1]));
        
        System.exit(0);
    }
}
