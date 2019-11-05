package std.wlj.dbnew;

import java.util.Date;
import java.util.List;

import org.familysearch.standards.place.data.CitationBridge;
import org.familysearch.standards.place.data.PlaceRepBridge;

import std.wlj.util.DbConnectionManager;
import std.wlj.util.DbConnectionManager.DbServices;


public class CitationDbServiceTest {

    public static void main(String... args) {

        DbServices dbServices = null;
        try {
            dbServices = DbConnectionManager.getDbServicesWLJ();

            PlaceRepBridge placeRepB = dbServices.readService.getRep(2);
            List<CitationBridge> citnBs = placeRepB.getAllCitations();
            System.out.println("\nALL..............................................\n");
            for (CitationBridge citnB : citnBs) {
                System.out.println("CITN: " + citnB.getCitationId() + "." + citnB.getPlaceRep().getRepId() + " :: " + citnB.getSourceRef() + " :: " + citnB.getDescription());
            }

            System.out.println("\nNEW..............................................\n");
            CitationBridge citnB01 = dbServices.writeService.createCitation(2, 463, 22, new Date(), "citn-desc", "citn-ref", "wjohnson000", null);
            System.out.println("CITN: " + citnB01.getCitationId() + "." + citnB01.getPlaceRep().getRepId() + " :: " + citnB01.getSourceRef() + " :: " + citnB01.getDescription());

            System.out.println("\nUPD..............................................\n");
            CitationBridge citnB02 = dbServices.writeService.updateCitation(citnB01.getCitationId(), 2, 463, 22, new Date(), "citn-desc-new", "citn-ref-new", "wjohnson000", null);
            System.out.println("CITN: " + citnB02.getCitationId() + "." + citnB02.getPlaceRep().getRepId() + " :: " + citnB02.getSourceRef() + " :: " + citnB02.getDescription());

            System.out.println("\nALL..............................................\n");
            placeRepB = dbServices.readService.getRep(2);
            citnBs = placeRepB.getAllCitations();
            for (CitationBridge citnB : citnBs) {
                System.out.println("CITN: " + citnB.getCitationId() + "." + citnB.getPlaceRep().getRepId() + " :: " + citnB.getSourceRef() + " :: " + citnB.getDescription());
            }

            System.out.println("\nALL (after delete)...............................\n");
            dbServices.writeService.deleteCitation(citnB01.getCitationId(), 2, "wjohnson000", null);

            placeRepB = dbServices.readService.getRep(2);
            citnBs = placeRepB.getAllCitations();
            for (CitationBridge citnB : citnBs) {
                System.out.println("CITN: " + citnB.getCitationId() + "." + citnB.getPlaceRep().getRepId() + " :: " + citnB.getSourceRef() + " :: " + citnB.getDescription());
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
