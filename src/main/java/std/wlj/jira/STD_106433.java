package std.wlj.jira;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;

/**
 * Update the "deleteId" field for all non-deleted documents in Solr ...
 * 
 * @author wjohnson000
 *
 */
public class STD_106433 {

    private static final String AWS_URL_DEV55 = "http://ws-55.solr.std.cmn.dev.us-east-1.dev.fslocal.org/places";

    public static void main(String... args) throws Exception {
        HttpSolrClient client = new HttpSolrClient.Builder(AWS_URL_DEV55).build();

        Map<String,Object> deleteIdModifier = new HashMap<>(1);
        deleteIdModifier.put("set", 0);

        // Do a look-up by documents ...
        int total = 0;
        List<PlaceRepDoc> docs = getNonDeletedDocs(client);
        while (! docs.isEmpty()) {
            total += docs.size();
            System.out.println("Another set of documents ... " + docs.size() + " --> " + total);

            List<SolrInputDocument> solrDocs = new ArrayList<>(docs.size());
            for (PlaceRepDoc prDoc : docs) {
                SolrInputDocument solrDoc = new SolrInputDocument();
                solrDoc.addField("id", prDoc.getId());
                solrDoc.addField("deleteId", deleteIdModifier);
                solrDocs.add(solrDoc);
            }

            client.add(solrDocs);
            client.commit();
            docs = getNonDeletedDocs(client);
        }

        client.close();
        System.exit(0);
    }

    static List<PlaceRepDoc> getNonDeletedDocs(HttpSolrClient client) throws SolrServerException, IOException {
        SolrQuery query = new SolrQuery("-deleteId:[* TO *]");
        query.setRows(2500);
        QueryResponse response = client.query(query);
        return response.getBeans(PlaceRepDoc.class);
    }
}
