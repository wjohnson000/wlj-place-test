package std.wlj.kml;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.BoundaryBridge;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.service.DbReadableService;
import org.familysearch.standards.place.service.DbWritableService;

import std.wlj.datasource.DbConnectionManager;
import std.wlj.util.SolrManager;

public class SVC_BoundaryContainsBoundary {

    private static PlaceDataServiceImpl dataService;

    public static void main(String... args) throws IOException {
        DataSource ds = DbConnectionManager.getDataSourceDev55();
        SolrService solrService = SolrManager.localEmbeddedService();
        DbReadableService readService = new DbReadableService(ds);
        DbWritableService writeService = new DbWritableService(ds);
        dataService = new PlaceDataServiceImpl(solrService, readService, writeService);

        List<BoundaryBridge> boundaries = dataService.getBoundaryContained(1, Arrays.asList(11, 111, 1111, 11111));
        for (BoundaryBridge boundaryB : boundaries) {
            System.out.println("B: " + boundaryB.getBoundary() + " --> " + boundaryB.getPlaceRepId() + " [" + boundaryB.getPointCount() + "]");
            System.out.println("   B-detail: " + boundaryB.getBoundary());
        }

        solrService.shutdown();
        readService.shutdown();
        writeService.shutdown();
        System.exit(0);
    }
}
