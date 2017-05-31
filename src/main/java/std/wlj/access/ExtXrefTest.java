package std.wlj.access;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.ExternalReferenceBridge;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.datasource.DbConnectionManager;
import std.wlj.datasource.DbConnectionManager.DbServices;
import std.wlj.util.SolrManager;


public class ExtXrefTest {

    public static void main(String... args) {
        PlaceDataServiceImpl dataService = null;
        DbServices dbServices = null;
        SolrService solrService = null;

        try {
            dbServices = DbConnectionManager.getDbServicesWLJ();
            solrService = SolrManager.awsIntService(true);
            dataService = new PlaceDataServiceImpl(solrService, dbServices.readService, dbServices.writeService);

            Set<ExternalReferenceBridge> extXrefBs = dataService.getExternalReferences("NGA_US_UFI", "ABC-DEF");
            System.out.println("\nALL..............................................\n");
            for (ExternalReferenceBridge extXrefB : extXrefBs) {
                System.out.println("EXT-XREF: " + extXrefB.getRefId() + "." + extXrefB.getReference() + " :: " + extXrefB.getType().getCode() + " :: " + extXrefB.getPlaceRep().getRepId());
            }

            System.out.println("\nNEW/UPD..........................................\n");
            Set<Integer> repIds = new HashSet<>(Arrays.asList(1000002, 1000003, 1000004, 1000005));
            extXrefBs = dataService.createOrUpdateExternalReference("NGA_US_UFI", "ABC-DEF", repIds, "wjohnson000", null);
            for (ExternalReferenceBridge extXrefB : extXrefBs) {
                System.out.println("EXT-XREF: " + extXrefB.getRefId() + "." + extXrefB.getReference() + " :: " + extXrefB.getType().getCode() + " :: " + extXrefB.getPlaceRep().getRepId());
            }

            System.out.println("\nNEW/UPD..........................................\n");
            repIds = new HashSet<>(Arrays.asList(1000004, 1000005, 1000006));
            extXrefBs = dataService.createOrUpdateExternalReference("NGA_US_UFI", "ABC-DEF", repIds, "wjohnson000", null);
            for (ExternalReferenceBridge extXrefB : extXrefBs) {
                System.out.println("EXT-XREF: " + extXrefB.getRefId() + "." + extXrefB.getReference() + " :: " + extXrefB.getType().getCode() + " :: " + extXrefB.getPlaceRep().getRepId());
            }
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            System.out.println("Shutting down ...");
            if (dataService != null) dataService.shutdown();
            if (dbServices != null) dbServices.shutdown();
            if (solrService != null) solrService.shutdown();
        }

        System.exit(0);
    }
}
