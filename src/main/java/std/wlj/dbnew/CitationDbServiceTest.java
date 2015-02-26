package std.wlj.dbnew;

import java.util.Date;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.data.CitationBridge;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.service.DbReadableService;
import org.familysearch.standards.place.service.DbWritableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class CitationDbServiceTest {

    public static void main(String... args) {

        ApplicationContext appContext = null;
        try {
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost-wlj.xml");
            BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
            DbReadableService dbRService = new DbReadableService(ds);
            DbWritableService dbWService = new DbWritableService(ds);

            PlaceRepBridge placeRepB = dbRService.getRep(2, null);
            List<CitationBridge> citnBs = placeRepB.getAllCitations();
            System.out.println("\nALL..............................................\n");
            for (CitationBridge citnB : citnBs) {
                System.out.println("CITN: " + citnB.getCitationId() + "." + citnB.getPlaceRep().getRepId() + " :: " + citnB.getSourceRef() + " :: " + citnB.getDescription());
            }

            System.out.println("\nNEW..............................................\n");
            CitationBridge citnB01 = dbWService.createCitation(2, 463, 22, new Date(), "citn-desc", "citn-ref", "wjohnson000");
            System.out.println("CITN: " + citnB01.getCitationId() + "." + citnB01.getPlaceRep().getRepId() + " :: " + citnB01.getSourceRef() + " :: " + citnB01.getDescription());

            System.out.println("\nUPD..............................................\n");
            CitationBridge citnB02 = dbWService.updateCitation(citnB01.getCitationId(), 2, 463, 22, new Date(), "citn-desc-new", "citn-ref-new", "wjohnson000");
            System.out.println("CITN: " + citnB02.getCitationId() + "." + citnB02.getPlaceRep().getRepId() + " :: " + citnB02.getSourceRef() + " :: " + citnB02.getDescription());

            System.out.println("\nALL..............................................\n");
            placeRepB = dbRService.getRep(2, null);
            citnBs = placeRepB.getAllCitations();
            for (CitationBridge citnB : citnBs) {
                System.out.println("CITN: " + citnB.getCitationId() + "." + citnB.getPlaceRep().getRepId() + " :: " + citnB.getSourceRef() + " :: " + citnB.getDescription());
            }

            System.out.println("\nALL (after delete)...............................\n");
            dbWService.deleteCitation(citnB01.getCitationId(), 2, "wjohnson000");

            placeRepB = dbRService.getRep(2, null);
            citnBs = placeRepB.getAllCitations();
            for (CitationBridge citnB : citnBs) {
                System.out.println("CITN: " + citnB.getCitationId() + "." + citnB.getPlaceRep().getRepId() + " :: " + citnB.getSourceRef() + " :: " + citnB.getDescription());
            }
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            System.out.println("Shutting down ...");
            ((ClassPathXmlApplicationContext)appContext).close();
        }

        System.exit(0);
    }
}
