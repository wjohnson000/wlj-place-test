package std.wlj.jira;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.core.CoreContainer;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;

/**
 * Update the "deleteId" field for all non-deleted documents in Solr ...
 * 
 * @author wjohnson000
 *
 */
public class STD_106434_Embedded {

    private static final int    SOLR_ROWS = 2500;
    private static final String SOLR_PATH = "C:/D-drive/solr/standalone-dbload-7.1.0";

    public static void main(String... args) throws Exception {
        CoreContainer container = new CoreContainer(SOLR_PATH);
        EmbeddedSolrServer solrServer = new EmbeddedSolrServer(container, "places");

        Map<String,Object> deleteIdModifier = new HashMap<>(1);
        deleteIdModifier.put("set", 0);

        // Do a look-up by documents ...
        int total = 0;
        List<PlaceRepDoc> docs = getNonDeletedDocs(solrServer);
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

            solrServer.add(solrDocs);
            solrServer.commit();
            docs = getNonDeletedDocs(solrServer);
        }

        solrServer.close();
        System.exit(0);
    }

    static List<PlaceRepDoc> getNonDeletedDocs(EmbeddedSolrServer client) throws SolrServerException, IOException {
        SolrQuery query = new SolrQuery("-deleteId:[* TO *]");
        query.setRows(SOLR_ROWS);
        QueryResponse response = client.query(query);
        return response.getBeans(PlaceRepDoc.class);
    }
}
