package org.familysearch.std.wlj.local;

//import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;


public class SearchRepoXxx {
    public static void main(String... args) throws Exception {
//        SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://localhost:8983/solr/places");
//        SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://107.21.173.161:8983/solr/places");
        SolrConnection solrConn = SolrConnection.connectToEmbeddedInstance("C:/tools/Solr/data/");

        // Do a first look-up
        
//        solrConn.optimize();
        SolrQuery query = new SolrQuery("id:1-*");
        List<PlaceRepDoc> docs = solrConn.search(query);

        int count01 = 0;
        int count02 = 0;
        long time01 = 0;
        long time02 = 0;
        for (int i=102;  i<5000;  i+= 123) {
            query = new SolrQuery("id:" + i + "-1");

            long nnowx = System.nanoTime();
            docs = solrConn.search(query);
            time02 += System.nanoTime() - nnowx;
            count02 += (docs == null) ? 0 : docs.size();
//            System.out.println("02: " + docs.get(0));

            nnowx = System.nanoTime();
            PlaceRepDoc doc01 = solrConn.getById((i+1) + "-1");
            time01 += System.nanoTime() - nnowx;
            count01 += (doc01 == null) ? 0 : 1;
//            System.out.println("01: " + doc01);
        }

        System.out.println("Cnt: " + count01 + ";  Time01: " + (time01 / 1000000.0));
        System.out.println("Cnt: " + count02 + ";  Time02: " + (time02 / 1000000.0));

//        long total = 0;
//        for (int loop=0;  loop<10;  loop++) {
//            for (int i=0;  i<3;  i++) {
//                query = new SolrQuery("id:1-*");
//                long nnow = System.nanoTime();
//                docs = solrConn.search(query);
//                nnow = System.nanoTime() - nnow;
//                total += nnow;
//                System.out.println("X | 1-* | " + (nnow / 1000000.0) + " | " + docs.size() + " | " + (docs.size() == 0 ? 0 : docs.get(0).getCitations().size()));
//            }
//
//            for (int i=0;  i<3;  i++) {
//                query = new SolrQuery("id:1-1");
//                long nnow = System.nanoTime();
//                docs = solrConn.search(query);
//                nnow = System.nanoTime() - nnow;
//                total += nnow;
//                System.out.println("X | 1-1 | " + (nnow / 1000000.0) + " | " + docs.size() + " | " + (docs.size() == 0 ? 0 : docs.get(0).getCitations().size()));
//            }
//        }
//
//        System.out.println("Total: " + (total / 1000000.0));

        solrConn.commit();
        solrConn.shutdown();
    }
}
