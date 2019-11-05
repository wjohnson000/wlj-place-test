package std.wlj.dbnew;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.familysearch.standards.place.data.ExternalReferenceBridge;

import std.wlj.util.DbConnectionManager;
import std.wlj.util.DbConnectionManager.DbServices;

public class ExtXrefDbServiceTest {

    public static void main(String... args) {

        DbServices dbServices = null;
        try {
            dbServices = DbConnectionManager.getDbServicesWLJ();

            Set<ExternalReferenceBridge> extXrefBs = dbServices.readService.getExternalReferences("RANDMC", "ABC-DEF");
            System.out.println("\nALL..............................................\n");
            for (ExternalReferenceBridge extXrefB : extXrefBs) {
                System.out.println("EXT-XREF: " + extXrefB.getRefId() + "." + extXrefB.getReference() + " :: " + extXrefB.getType().getCode() + " :: " + extXrefB.getPlaceRep().getRepId());
            }

            System.out.println("\nNEW/UPD..........................................\n");
            Set<Integer> repIds = new HashSet<>(Arrays.asList(2, 3, 4, 5));
            extXrefBs = dbServices.writeService.createOrUpdateExternalReference("RANDMC", "ABC-DEF", repIds, "wjohnson000", null);
            for (ExternalReferenceBridge extXrefB : extXrefBs) {
                System.out.println("EXT-XREF: " + extXrefB.getRefId() + "." + extXrefB.getReference() + " :: " + extXrefB.getType().getCode() + " :: " + extXrefB.getPlaceRep().getRepId());
            }

            System.out.println("\nNEW/UPD..........................................\n");
            repIds = new HashSet<>(Arrays.asList(4, 5, 6));
            extXrefBs = dbServices.writeService.createOrUpdateExternalReference("RANDMC", "ABC-DEF", repIds, "wjohnson000", null);
            for (ExternalReferenceBridge extXrefB : extXrefBs) {
                System.out.println("EXT-XREF: " + extXrefB.getRefId() + "." + extXrefB.getReference() + " :: " + extXrefB.getType().getCode() + " :: " + extXrefB.getPlaceRep().getRepId());
            }
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            System.out.println("Shutting down ...");
            if (dbServices != null) dbServices.shutdown();
        }

        System.exit(0);
    }
}
