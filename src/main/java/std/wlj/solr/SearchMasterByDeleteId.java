package std.wlj.solr;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;


public class SearchMasterByDeleteId {

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsProdConnection(true);

        // Do a look-up for deleted documents
        int skip = 0;
        Map<Integer,Integer> deleteIdMap = new TreeMap<>();
        while (true) {
            SolrQuery query = new SolrQuery("deleteId:* AND NOT id:PLACE*");
            query.setStart(skip);
            query.setRows(256);
            query.setSort("revision", SolrQuery.ORDER.asc);
            List<PlaceRepDoc> docs = solrConn.search(query);
            if (docs.size() == 0) {
                break;
            }

            System.out.println("Skip: " + skip + " --> Count: " + docs.size());
            skip += 250;
            for (PlaceRepDoc prDoc : docs) {
                if (prDoc.getDeleteId() != null) {
                    deleteIdMap.put(prDoc.getRepId(), prDoc.getDeleteId());
                }
            }
        }

        for (Map.Entry<Integer,Integer> entry : deleteIdMap.entrySet()) {
            System.out.println("" + entry.getKey() + "|" + entry.getValue());
        }
        solrConn.shutdown();
        System.exit(0);
    }
}
