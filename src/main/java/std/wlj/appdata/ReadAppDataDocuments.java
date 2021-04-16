package std.wlj.appdata;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;


public class ReadAppDataDocuments {

    public static String[] DOC_IDS = {
         "ATTRIBUTE-TYPE",
         "CITATION-TYPE",
         "EXT-XREF-TYPE",
         "NAME-TYPE",
         "PLACE-TYPE",
         "RESOLUTION-TYPE",
         "FEEDBACK-RESOLUTION-TYPE",
         "FEEDBACK-STATE-TYPE",
         "REP-RELATION",
         "GROUP-HIERARCHY",
         "NAME-PRIORITY",
         "SOURCE",
    };

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsBetaConnection(true);

        for (String docId : DOC_IDS) {
            SolrQuery query = new SolrQuery("id:" + docId);
            query.setRows(2);
            query.setSort("revision", SolrQuery.ORDER.desc);

            List<PlaceRepDoc> docs = solrConn.search(query);
            System.out.println("====================================================================================================================");

            for (PlaceRepDoc doc : docs) {
                System.out.println("DOC-ID|" + doc.getId());
                doc.getAppData().forEach(System.out::println);
            }
        }

        System.exit(0);
    }
}
