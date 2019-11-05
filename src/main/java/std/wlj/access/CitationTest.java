package std.wlj.access;

import java.util.Date;
import java.util.List;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.CitationBridge;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.util.DbConnectionManager;
import std.wlj.util.SolrManager;
import std.wlj.util.DbConnectionManager.DbServices;

public class CitationTest {

    public static void main(String... args) {
        PlaceDataServiceImpl dataService = null;
        DbServices dbServices = null;
        SolrService solrService = null;

        try {
            int repId = 1234;

            dbServices = DbConnectionManager.getDbServicesWLJ();
            solrService = SolrManager.awsIntService(true);
            dataService = new PlaceDataServiceImpl(solrService, dbServices.readService, dbServices.writeService);

            PlaceRepBridge placeRepB01 = dbServices.readService.getRep(repId);
            if (placeRepB01 == null) {
                System.out.println("Not found --- repId=" + repId);
                return;
            }

            List<CitationBridge> citnBs = placeRepB01.getAllCitations();
            System.out.println("\nALL..............................................\n");
            for (CitationBridge citnB : citnBs) {
                System.out.println("CITN: " + citnB.getCitationId() + "." + citnB.getPlaceRep().getRepId() + " :: " + citnB.getSourceRef() + " :: " + citnB.getDescription());
            }

            System.out.println("\nNEW..............................................\n");
            CitationBridge citnB01 = dataService.createCitation(repId, 463, 22, new Date(), "citn-desc", "citn-ref", "wjohnson000", null);
            System.out.println("CITN: " + citnB01.getCitationId() + "." + citnB01.getPlaceRep().getRepId() + " :: " + citnB01.getSourceRef() + " :: " + citnB01.getDescription());

            System.out.println("\nNEW..............................................\n");
            CitationBridge citnB01X = dataService.createCitation(repId, 462, 22, new Date(), "citn-desc-x", "citn-ref-x", "wjohnson000", null);
            System.out.println("CITNX: " + citnB01X.getCitationId() + "." + citnB01X.getPlaceRep().getRepId() + " :: " + citnB01X.getSourceRef() + " :: " + citnB01X.getDescription());

            System.out.println("\nUPD..............................................\n");
            CitationBridge citnB02 = dataService.updateCitation(citnB01.getCitationId(), repId, 463, 22, new Date(), "citn-desc-new", "citn-ref-new", "wjohnson000", null);
            System.out.println("CITN: " + citnB02.getCitationId() + "." + citnB02.getPlaceRep().getRepId() + " :: " + citnB02.getSourceRef() + " :: " + citnB02.getDescription());

            System.out.println("\nALL..............................................\n");
            PlaceRepBridge placeRepB02 = dbServices.readService.getRep(repId);
            System.out.println("PLACE-REP: " + placeRepB02.getRepId() + "." + placeRepB02.getRevision());
            citnBs = placeRepB02.getAllCitations();
            for (CitationBridge citnB : citnBs) {
                System.out.println("CITN: " + citnB.getCitationId() + "." + citnB.getPlaceRep().getRepId() + " :: " + citnB.getSourceRef() + " :: " + citnB.getDescription());
            }

            System.out.println("\nALL (after delete)...............................\n");
            dataService.deleteCitation(citnB01.getCitationId(), repId, "wjohnson000", null);

            System.out.println("\nNEW..............................................\n");
            CitationBridge citnB01Y = dataService.createCitation(repId, 462, 22, new Date(), "citn-desc-y", "citn-ref-y", "wjohnson000", null);
            System.out.println("CITNY: " + citnB01Y.getCitationId() + "." + citnB01Y.getPlaceRep().getRepId() + " :: " + citnB01Y.getSourceRef() + " :: " + citnB01Y.getDescription());

            PlaceRepBridge placeRepB03 = dbServices.readService.getRep(repId);
            System.out.println("PLACE-REP: " + placeRepB03.getRepId() + "." + placeRepB03.getRevision());
            citnBs = placeRepB03.getAllCitations();
            for (CitationBridge citnB : citnBs) {
                System.out.println("CITN: " + citnB.getCitationId() + "." + citnB.getPlaceRep().getRepId() + " :: " + citnB.getSourceRef() + " :: " + citnB.getDescription());
            }
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } catch(Throwable th) {
            System.out.println("Ex: " + th.getMessage());
        } finally {
            System.out.println("Shutting down ...");
            if (dataService != null) dataService.shutdown();
            if (dbServices != null) dbServices.shutdown();
            if (solrService != null) solrService.shutdown();
        }

        System.exit(0);
    }
}
