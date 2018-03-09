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
public class STD_106434_Trial {

    private static final String AWS_URL_DEV55 = "http://ws-55.solr.std.cmn.dev.us-east-1.dev.fslocal.org/places";

    public static void main(String... args) throws Exception {
        HttpSolrClient client = new HttpSolrClient.Builder(AWS_URL_DEV55).build();

        Map<String,Object> deleteIdModifier = new HashMap<>(1);
        deleteIdModifier.put("set", 0);

        // Do a look-up by documents ...
        List<PlaceRepDoc> docs = getNonDeletedDocs(client);
        docs.forEach(doc -> System.out.println(doc + " --> " + doc.getDeleteId()));

        List<SolrInputDocument> solrDocs = new ArrayList<>(docs.size());
        for (PlaceRepDoc prDoc : docs) {
            SolrInputDocument solrDoc = new SolrInputDocument();
            solrDoc.addField("id", prDoc.getId());
            solrDoc.addField("deleteId", deleteIdModifier);
            solrDocs.add(solrDoc);
        }

        client.add(solrDocs);
        client.commit();

        client.close();
        System.exit(0);
    }

    static List<PlaceRepDoc> getNonDeletedDocs(HttpSolrClient client) throws SolrServerException, IOException {
        SolrQuery query = new SolrQuery("id:(330 331 332 333 334 335 336 337 338 339)");
        query.setRows(10);
        QueryResponse response = client.query(query);
        return response.getBeans(PlaceRepDoc.class);
    }
}
