package std.wlj.solr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;


public class CopyPlaceRepDoc {

    private static final String[] repIds = { "1442484", "10335852" };

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConnFROM = SolrManager.awsIntConnection(true);
        SolrConnection solrConnTO   = SolrManager.localEmbeddedConnection("D:/solr/stand-alone-6.5.0");

        // Do a look-up by rep-id and parent ...
        SolrQuery query;
        List<PlaceRepDoc> docs = new ArrayList<>();
        for (String repId : repIds) {
            query = new SolrQuery("id:" + repId + "-*");
            query.setRows(32);
            query.setSort("revision", SolrQuery.ORDER.asc);
            docs.addAll(solrConnFROM.search(query));

            query = new SolrQuery("parentId:" + repId);
            query.setRows(32);
            query.setSort("revision", SolrQuery.ORDER.asc);
            docs.addAll(solrConnFROM.search(query));
        }

        System.out.println("CNT: " + docs.size());
        for (PlaceRepDoc doc : docs) {
            System.out.println("ID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()) + " --> " + doc.getRevision());
            System.out.println("  Place:  " + doc.getPlaceId());
            System.out.println("  D-Name: " + doc.getDisplayNameMap());
            System.out.println("  P-Name: " + doc.getNames());
            System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
        }

        solrConnTO.add(docs);
        solrConnTO.commit();

        solrConnFROM.shutdown();
        solrConnTO.shutdown();
        System.exit(0);
    }
}
