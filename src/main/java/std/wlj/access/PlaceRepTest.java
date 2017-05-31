package std.wlj.access;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.datasource.DbConnectionManager;
import std.wlj.datasource.DbConnectionManager.DbServices;
import std.wlj.util.SolrManager;

public class PlaceRepTest {

    private static PlaceDataServiceImpl dataService;

    public static void main(String... args) throws IOException {
        DbServices dbServices = DbConnectionManager.getDbServicesWLJ();
        SolrService solrService = SolrManager.localEmbeddedService("D:/solr/stand-alone-6.5.0");
        dataService = new PlaceDataServiceImpl(solrService, dbServices.readService, dbServices.writeService);

        List<PlaceRepBridge> repBridges = dataService.getRepVersions(337);
        repBridges.forEach(rep -> dumpRep(rep));

        solrService.shutdown();
        dbServices.shutdown();

        System.exit(0);
    }

    static void dumpRep(PlaceRepBridge prBridge) {
        System.out.println("=======================================================================");
        System.out.println("ID: " + prBridge.getRepId() + ";  Rev: " + prBridge.getRevision());
        System.out.println("  place: " + prBridge.getPlaceId());
        System.out.println("  place: " + Arrays.toString(prBridge.getJurisdictionIdentifiers()));
        System.out.println("   type: " + prBridge.getPlaceType());
        System.out.println("  range: " + prBridge.getJurisdictionFromYear() + " to " + prBridge.getJurisdictionToYear());
        System.out.println("  where: " + prBridge.getLatitude() + " :: " + prBridge.getLongitude());
        System.out.println("  bdyId: " + prBridge.getPreferredBoundaryId());
        System.out.println("  " + prBridge.getAllDisplayNames());
        System.out.println();
    }
}
