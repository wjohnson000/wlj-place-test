package std.wlj.kml;

import java.io.IOException;
import java.util.List;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.BoundaryBridge;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.util.DbConnectionManager;
import std.wlj.util.SolrManager;
import std.wlj.util.DbConnectionManager.DbServices;

public class SVC_BoundaryBorders {

    private static PlaceDataServiceImpl dataService;

    public static void main(String... args) throws IOException {
        DbServices dbServices = DbConnectionManager.getDbServicesSams();
        SolrService solrService = SolrManager.localEmbeddedService();
        dataService = new PlaceDataServiceImpl(solrService, dbServices.readService, dbServices.writeService);

        List<BoundaryBridge> boundaries = dataService.getBoundariesBorderingOn(12, null, null);
        for (BoundaryBridge boundaryB : boundaries) {
            System.out.println("B: " + boundaryB.getBoundary() + " --> " + boundaryB.getPlaceRepId() + " [" + boundaryB.getPointCount() + "]");
            System.out.println("   B-detail: " + boundaryB.getBoundary());
        }

        solrService.shutdown();
        dbServices.shutdown();

        System.exit(0);
    }
}
