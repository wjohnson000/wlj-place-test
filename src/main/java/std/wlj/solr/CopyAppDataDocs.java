package std.wlj.solr;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;


public class CopyAppDataDocs {

    private static final String[] docIds = {
        "ATTRIBUTE-TYPE",
        "CITATION-TYPE",
        "EXT-XREF-TYPE",
        "NAME-TYPE",
        "PLACE-TYPE",
        "RESOLUTION-TYPE",
        "FEEDBACK-RESOLUTION-TYPE",
        "FEEDBACK-STATE-TYPE",
        "REP-RELATION",

        "SOURCE",
        "GROUP-HIERARCHY",
        "NAME-PRIORITY",
    };

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConnFROM = SolrManager.awsProdConnection(false);
        SolrConnection solrConnTO   = SolrManager.awsIntConnection(true);

        // Do a look-up by doc-id
        SolrQuery query;
        List<PlaceRepDoc> docs = new ArrayList<>();
        for (String docId : docIds) {
            query = new SolrQuery("id:" + docId);
            query.setRows(32);
            query.setSort("revision", SolrQuery.ORDER.asc);
            docs.addAll(solrConnFROM.search(query));
        }

        System.out.println("CNT: " + docs.size());
        docs.forEach(doc -> System.out.println("ID: " + doc.getId() + " --> " + doc.getType()));

        solrConnTO.add(docs);
        solrConnTO.commit();

        solrConnFROM.shutdown();
        solrConnTO.shutdown();
        System.exit(0);
    }
}
