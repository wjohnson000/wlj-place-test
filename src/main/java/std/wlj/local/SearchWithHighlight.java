package std.wlj.local;

import java.util.Set;
import java.util.TreeSet;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.params.HighlightParams;
import org.familysearch.standards.place.data.PlaceNameBridge;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.data.solr.SolrSearchResults;


public class SearchWithHighlight {
    public static void main(String... args) throws Exception {
//        SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://localhost:8983/solr/places");
//        SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://107.21.173.161:8983/solr/places");
        SolrConnection solrConn = SolrConnection.connectToEmbeddedInstance("C:/tools/Solr/data/");
        System.out.println("Doc count: " + solrConn.getDocCount());

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

        String[] fuzzies = {
            "",
            "~1",
            "~2"
        };

        for (String text : textes) {
            System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            System.out.println("TEXT: " + text);
            for (String fuzzy : fuzzies) {
                SolrQuery query = new SolrQuery("names:" + text + fuzzy);
                query.setRows(500);
                query.setHighlight(true);
                query.setHighlightSimplePre("|");
                query.setHighlightSimplePost("|");
                query.addHighlightField("names");
                query.setParam(HighlightParams.MAX_MULTIVALUED_TO_MATCH, "1");
                query.setParam(HighlightParams.PRESERVE_MULTI, false);

                SolrSearchResults results = solrConn.search(query, null);
                long found = results.getFoundCount();
                Set<String> matches = new TreeSet<>();
                for (PlaceRepDoc prDoc : results.getResults()) {
                    PlaceNameBridge nameB = prDoc.getMatchedVariant();
                    if (nameB != null) {
                        matches.add(nameB.getNormalizedName());
                    }
                }

                StringBuilder buff = new StringBuilder();
                buff.append("found=").append(found);
                for (String match : matches) {
                    buff.append("|").append(match);
                }
                System.out.println(fuzzy + " --> " + buff.toString());
            }

        }

        solrConn.commit();
        solrConn.shutdown();
    }
}
