package std.wlj.local;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;


public class SearchWithoutHighlight {
    public static void main(String... args) throws Exception {
        SolrConnection solrConn = SolrManager.awsDevConnection(false);

        // Regular search, highlight search
        String[] textes = {
            "provo",
            "bloomington",
            "copenhagen",
            "trierweiler",
            "bishopwearmouth",
            "アルゼンチン",
            "chihuahua",
            "kornelimuenster",
            "mikkeli",
            "район",
            "затон"
        };

        for (String text : textes) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println("TEXT: " + textes);
            SolrQuery query = new SolrQuery("names:" + text);
            List<PlaceRepDoc> docs = solrConn.search(query);
            System.out.println("Token: " + text + " --> " + docs.size());

            query = new SolrQuery("names:" + text + "~");
            docs = solrConn.search(query);
            System.out.println("Token: " + text + " --> " + docs.size());

            query = new SolrQuery("names:" + text + "~1");
            docs = solrConn.search(query);
            System.out.println("Token: " + text + " --> " + docs.size());

            query = new SolrQuery("names:" + text + "~2");
            docs = solrConn.search(query);
            System.out.println("Token: " + text + " --> " + docs.size());

            query = new SolrQuery("names:" + text + "~3");
            docs = solrConn.search(query);
            System.out.println("Token: " + text + " --> " + docs.size());

            query = new SolrQuery("names:" + text + "~4");
            docs = solrConn.search(query);
            System.out.println("Token: " + text + " --> " + docs.size());

            query = new SolrQuery("names:" + text + "~5");
            docs = solrConn.search(query);
            System.out.println("Token: " + text + " --> " + docs.size());
        }

        solrConn.commit();
        solrConn.shutdown();
    }
}
