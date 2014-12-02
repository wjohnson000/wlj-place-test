package std.wlj.local;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;


public class SearchWithoutHighlight {
    public static void main(String... args) throws Exception {
//        SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://localhost:8983/solr/places");
//        SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://107.21.173.161:8983/solr/places");
        SolrConnection solrConn = SolrConnection.connectToEmbeddedInstance("C:/tools/Solr/data/");

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
