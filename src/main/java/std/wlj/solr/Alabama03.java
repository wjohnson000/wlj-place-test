package std.wlj.solr;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;


public class Alabama03 {

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsProdConnection(false);

        // Do a look-up by documents ...
        Map<Integer,PlaceRepDoc> uniqueDocs = new TreeMap<>();
        SolrQuery query = new SolrQuery("repIdChain:351 AND (type:186 OR type:376) AND NOT deleteId:[1 TO 2147483647] AND published:1");
        query.setRows(1000);
        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("CNT: " + docs.size());
        for (PlaceRepDoc doc : docs) {
            PlaceRepDoc currDoc = uniqueDocs.get(doc.getRepId());
            if (currDoc == null) {
                uniqueDocs.put(doc.getRepId(), doc);
            } else if (doc.getRevision() > currDoc.getRevision()) {
                uniqueDocs.put(doc.getRepId(), doc);
            }
        }

        for (PlaceRepDoc doc : uniqueDocs.values()) {
            System.out.println("ID: " + doc.getId() + " --> " + doc.getDeleteId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()));
        }

        System.exit(0);
    }
}
