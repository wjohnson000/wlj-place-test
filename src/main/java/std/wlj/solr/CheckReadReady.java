package std.wlj.solr;

import org.familysearch.standards.place.data.solr.SolrConnection;
import std.wlj.util.SolrManager;
import org.familysearch.standards.place.exceptions.PlaceDataException;

public class CheckReadReady {

    public static void main(String... args) throws PlaceDataException {
        SolrConnection solrConn = SolrManager.awsDevConnection(true);
        System.out.println("Dev-Write-Ready: " + solrConn.isWriteReady());
        solrConn.shutdown();

        solrConn = SolrManager.awsIntConnection(true);
        System.out.println("Int-Write-Ready: " + solrConn.isWriteReady());
        solrConn.shutdown();

        solrConn = SolrManager.awsBetaConnection(true);
        System.out.println("Beta-Write-Ready: " + solrConn.isWriteReady());
        solrConn.shutdown();

        solrConn = SolrManager.awsProdConnection(true);
        System.out.println("Prod-Write-Ready: " + solrConn.isWriteReady());
        solrConn.shutdown();
    }
}
