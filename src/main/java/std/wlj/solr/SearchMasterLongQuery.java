package std.wlj.solr;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;

public class SearchMasterLongQuery {

    static final int MAX_ROWS = 5000;

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsDevConnection(false);
        System.out.println("Conn: " + solrConn);

        int id01 = 8_000_000;
        int id02 = 8_000_875;
        String query01 = "repId:[" + id01 + " TO " + id02 + "]";

        String query02 = "repId:(";
        for (int i=id01;  i<id02;  i++) {
            query02 += " " + i;
        }
        query02 += ")";

        SolrQuery queryAA = new SolrQuery(query01);
        queryAA.setRows(MAX_ROWS);

        SolrQuery queryBB = new SolrQuery(query02);
        queryBB.setRows(MAX_ROWS);

        long time00 = System.nanoTime();
        List<PlaceRepDoc> docs01 = solrConn.search(queryAA);
        long time01 = System.nanoTime();
        System.out.println("CNT: " + docs01.size() + " --> " + (time01-time00)/1_000_000.0);

        time00 = System.nanoTime();
        List<PlaceRepDoc> docs02 = solrConn.search(queryBB);
        time01 = System.nanoTime();
        System.out.println("CNT: " + docs02.size() + " --> " + (time01-time00)/1_000_000.0);

        System.exit(0);
    }
}
