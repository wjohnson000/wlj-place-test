package std.wlj.local;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.HighlightParams;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.data.solr.SolrSearchResults;


public class SearchWithHightlight {
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
            query.setHighlight(true);
            query.setHighlightSimplePre("|");
            query.setHighlightSimplePost("|");
            query.addHighlightField("names");
            query.setParam(HighlightParams.MAX_MULTIVALUED_TO_MATCH, "1");
            query.setParam(HighlightParams.PRESERVE_MULTI, false);
//            query.setParam(HighlightParams.USE_FVH, true);
//            query.setParam(HighlightParams.PHRASE_LIMIT, "1");
//            query.setParam(HighlightParams.BOUNDARY_SCANNER, "breakIterator");
//            query.setParam(HighlightParams.USE_PHRASE_HIGHLIGHTER, "false");

            nnow = System.nanoTime();
            SolrSearchResults results = solrConn.search(query, null);
            nnow = System.nanoTime() - nnow;
            time += nnow;

            System.out.println("Token: " + text + " --> " + results.getReturnedCount());
        }

        System.out.println("Search w/ highlight: " + (time / 1000000.0));

        solrConn.commit();
        solrConn.shutdown();
    }
}
