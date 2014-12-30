package std.wlj.access;

import java.util.Date;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.CitationBridge;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.service.DbReadableService;
import org.familysearch.standards.place.service.DbWritableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class CitationTest {

    public static void main(String... args) {

//      System.setProperty("solr.master.url", "C:/tools/solr/data/tokoro");
//      System.setProperty("solr.solr.home", "C:/tools/solr/data/tokoro");
      System.setProperty("solr.master.url", "http://localhost:8983/solr/places");
      System.setProperty("solr.solr.home", "http://localhost:8983/solr/places");
      System.setProperty("solr.master", "false");
      System.setProperty("solr.slave", "false");

      ApplicationContext appContext = null;
      PlaceDataServiceImpl dataService = null;

        try {
            int repId = 123457;
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
            BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
            SolrService       solrService = new SolrService();
            DbReadableService dbRService  = new DbReadableService(ds);
            DbWritableService dbWService  = new DbWritableService(ds);
            dataService = new PlaceDataServiceImpl(solrService, dbRService, dbWService);

            PlaceRepBridge placeRepB01 = dbRService.getRep(repId, null);
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
            CitationBridge citnB01 = dataService.createCitation(repId, 463, 22, new Date(), "citn-desc", "citn-ref", "wjohnson000");
            System.out.println("CITN: " + citnB01.getCitationId() + "." + citnB01.getPlaceRep().getRepId() + " :: " + citnB01.getSourceRef() + " :: " + citnB01.getDescription());

            System.out.println("\nNEW..............................................\n");
            CitationBridge citnB01X = dataService.createCitation(repId, 462, 22, new Date(), "citn-desc-x", "citn-ref-x", "wjohnson000");
            System.out.println("CITNX: " + citnB01X.getCitationId() + "." + citnB01X.getPlaceRep().getRepId() + " :: " + citnB01X.getSourceRef() + " :: " + citnB01X.getDescription());

            System.out.println("\nUPD..............................................\n");
            CitationBridge citnB02 = dataService.updateCitation(citnB01.getCitationId(), repId, 463, 22, new Date(), "citn-desc-new", "citn-ref-new", "wjohnson000");
            System.out.println("CITN: " + citnB02.getCitationId() + "." + citnB02.getPlaceRep().getRepId() + " :: " + citnB02.getSourceRef() + " :: " + citnB02.getDescription());

            System.out.println("\nALL..............................................\n");
            PlaceRepBridge placeRepB02 = dbRService.getRep(repId, null);
            System.out.println("PLACE-REP: " + placeRepB02.getRepId() + "." + placeRepB02.getVersion() + "." + placeRepB02.getRevision());
            citnBs = placeRepB02.getAllCitations();
            for (CitationBridge citnB : citnBs) {
                System.out.println("CITN: " + citnB.getCitationId() + "." + citnB.getPlaceRep().getRepId() + " :: " + citnB.getSourceRef() + " :: " + citnB.getDescription());
            }

            System.out.println("\nALL (after delete)...............................\n");
            dataService.deleteCitation(citnB01.getCitationId(), repId, "wjohnson000");

            System.out.println("\nNEW..............................................\n");
            CitationBridge citnB01Y = dataService.createCitation(repId, 462, 22, new Date(), "citn-desc-y", "citn-ref-y", "wjohnson000");
            System.out.println("CITNY: " + citnB01Y.getCitationId() + "." + citnB01Y.getPlaceRep().getRepId() + " :: " + citnB01Y.getSourceRef() + " :: " + citnB01Y.getDescription());

            PlaceRepBridge placeRepB03 = dbRService.getRep(repId, null);
            System.out.println("PLACE-REP: " + placeRepB03.getRepId() + "." + placeRepB03.getVersion() + "." + placeRepB03.getRevision());
            citnBs = placeRepB03.getAllCitations();
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
