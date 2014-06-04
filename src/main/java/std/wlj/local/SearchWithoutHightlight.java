package std.wlj.local;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;


public class SearchWithoutHightlight {
    public static void main(String... args) throws Exception {
//        SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://localhost:8983/solr/places");
//        SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://107.21.173.161:8983/solr/places");
        SolrConnection solrConn = SolrConnection.connectToEmbeddedInstance("C:/tools/Solr/data/");

        // Regular search, highlight search
        long nnow = 0;
        long time = 0;
        String[] textes = { "provo", "bloomington", "copenhagen", "trierweiler", "bishopwearmouth", "アルゼンチン", "chihuahua", "kornelimuenster", "mikkeli" };

        for (String text : textes) {
            SolrQuery query = new SolrQuery("names:" + text);
            nnow = System.nanoTime();
            List<PlaceRepDoc> docs = solrConn.search(query);
            nnow = System.nanoTime() - nnow;
            time += nnow;

            System.out.println("Token: " + text + " --> " + docs.size());
        }

        System.out.println("Search w/out highlight: " + (time / 1000000.0));

        solrConn.commit();
        solrConn.shutdown();
    }
}
