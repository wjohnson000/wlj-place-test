package std.wlj.local;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.data.solr.SolrSearchResults;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;


public class SearchByPubNonPub {
    public static void main(String... args) throws Exception {
//        SolrConnection solrConn = SolrManager.awsProdConnection(false);
        SolrConnection solrConn = SolrManager.localEmbeddedConnection("C:/D-drive/solr/standalone-dbload-7.1.0");

        // Do a first look-up
        SolrQuery query = new SolrQuery("*:*");
        query.addFilterQuery("deleteId:[\\-10000 TO 0]");
        query.setRows(1000);
        System.out.println("QRY: " + query);

        SolrSearchResults ssr = solrConn.search(query, null);
        System.out.println("RET: " + ssr.getReturnedCount());
        System.out.println("FND: " + ssr.getFoundCount());
        ssr.getResults().forEach(System.out::println);

        long time0 = System.nanoTime();
        for (int repId=1;  repId<=11_000_000;  repId+=1357) {
            getDocById(solrConn, repId, false, true);
        }
        long time1 = System.nanoTime();

        solrConn.shutdown();
        System.out.println("TIME: " + (time1-time0)/1_000_000.0);
    }

    static void getDocById(SolrConnection solrConn, int repId, boolean usePub, boolean useDel) throws PlaceDataException {
      SolrQuery query = new SolrQuery("repId:" + repId);
      query.setRows(1000);
      if (usePub  &&  useDel) {
          query.addFilterQuery("published:1 -deleteId:[1 TO * ]");
      } else if (usePub) {
          query.addFilterQuery("published:1");
      } else if (useDel) {
          query.addFilterQuery("-deleteId:[* TO * ]");
      }

      solrConn.search(query, null);
    }
}
