package std.wlj.access;

import java.io.IOException;
import java.util.List;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.*;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.datasource.DbConnectionManager;
import std.wlj.datasource.DbConnectionManager.DbServices;
import std.wlj.util.SolrManager;

public class SVC_GetVersionsTest {

    private static PlaceDataServiceImpl dataService;

    public static void main(String... args) throws IOException {
        DbServices dbServices = DbConnectionManager.getDbServicesSams();
        SolrService solrService = SolrManager.localEmbeddedService();
        dataService = new PlaceDataServiceImpl(solrService, dbServices.readService, dbServices.writeService);

        int repId = 6052140;

        List<PlaceRepBridge> placeReps = dataService.getRepVersions(repId);
        List<AltJurisdictionBridge> altJuris = dataService.getAltJurisdictionVersions(repId);
        List<AttributeBridge> attributes = dataService.getAttributeVersions(repId);
        List<BoundaryBridge> boundaries = dataService.getBoundaryVersions(repId);
        List<CitationBridge> citations = dataService.getCitationVersions(repId);

        placeReps.forEach(what -> System.out.println("Rep: " + what + " --> del? " + what.isDeleted()));
        altJuris.forEach(what -> System.out.println("Alt: " + what + " --> del? " + what.isDeleted()));
        attributes.forEach(what -> System.out.println("Atr: " + what + " --> del? " + what.isDeleted()));
        boundaries.forEach(what -> System.out.println("Bdy: " + what + " --> del? " + what.isDeleted()));
        citations.forEach(what -> System.out.println("Cit: " + what + " --> del? " + what.isDeleted()));

        solrService.shutdown();
        dbServices.shutdown();

        System.exit(0);
    }
}
