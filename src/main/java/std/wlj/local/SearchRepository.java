package std.wlj.local;

//import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;


public class SearchRepository {
    public static void main(String... args) throws Exception {
//        SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://localhost:8983/solr/places");
//        SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://107.21.173.161:8983/solr/places");
        SolrConnection solrConn = SolrConnection.connectToEmbeddedInstance("C:/tools/Solr/data/");

        // Do a first look-up
        
//        solrConn.optimize();
        SolrQuery query = new SolrQuery("id:1-*");
        List<PlaceRepDoc> docs = solrConn.search(query);

        Runtime.getRuntime().traceInstructions(true);
        Runtime.getRuntime().traceMethodCalls(true);

        long total = 0;
        for (int loop=0;  loop<5;  loop++) {
            for (int i=0;  i<3;  i++) {
                String[] names = { "1", "1337578", "1234", "555555" };
                for (String name : names) {
                    query = new SolrQuery("id:" + name + "-*");
                    long nnow = System.nanoTime();
                    docs = solrConn.search(query);
                    nnow = System.nanoTime() - nnow;
                    total += nnow;
                    System.out.println("X | " + name + " | " + (nnow / 1000000.0) + " | " + docs.size() + " | " + (docs.size() == 0 ? 0 : docs.get(0).getCitations().size()));
                }
            }

            for (int i=0;  i<3;  i++) {
                String[] names = { "provo", "darlington", "southcarolina", "usa", "horsens" };
                for (String name : names) {
                    query = new SolrQuery("names:" + name);
                    long nnow = System.nanoTime();
                    docs = solrConn.search(query);
                    nnow = System.nanoTime() - nnow;
                    total += nnow;
                    System.out.println("Y | " + name + " | " + (nnow / 1000000.0) + " | " + docs.size() + " | " + (docs.size() == 0 ? 0 : docs.get(0).getCitations().size()));
                }
            }
        }

        System.out.println("Total: " + (total / 1000000.0));

        solrConn.commit();
        solrConn.shutdown();
    }
}
