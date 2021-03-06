package std.wlj.kml;

import java.io.IOException;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.DbConnectionManager;
import std.wlj.util.SolrManager;
import std.wlj.util.DbConnectionManager.DbServices;

public class SVC_DeleteBoundary {

    private static PlaceDataServiceImpl dataService;
    private static String wlj = "wjohnson000";

    public static void main(String... args) throws IOException, PlaceDataException {
        DbServices dbServices = DbConnectionManager.getDbServicesSams();
        
        SolrService solrService = SolrManager.localEmbeddedService();
        dataService = new PlaceDataServiceImpl(solrService, dbServices.readService, dbServices.writeService);

        int transxId = dataService.deleteBoundary(220, wlj, null);
        System.out.println("TRX-ID: " + transxId);

        solrService.shutdown();
        dbServices.shutdown();

        System.exit(0);
    }
}
