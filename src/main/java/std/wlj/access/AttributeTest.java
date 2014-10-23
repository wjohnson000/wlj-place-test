package std.wlj.access;

import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.AttributeBridge;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.service.DbReadableService;
import org.familysearch.standards.place.service.DbWritableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class AttributeTest {

    public static void main(String... args) {
//        System.setProperty("solr.master.url", "C:/tools/solr/data/tokoro");
//        System.setProperty("solr.solr.home", "C:/tools/solr/data/tokoro");
        System.setProperty("solr.master.url", "http://localhost:8983/solr/places");
        System.setProperty("solr.solr.home", "http://localhost:8983/solr/places");
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");

        ApplicationContext appContext = null;
        PlaceDataServiceImpl dataService = null;
        try {
            int repId = 145;
            appContext = new ClassPathXmlApplicationContext("postgres-context-aws-int.xml");
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

            List<AttributeBridge> attrBs = placeRepB01.getAllAttributes();
            System.out.println("\nALL..............................................\n");
            System.out.println("PLACE-REP: " + placeRepB01.getRepId() + "." + placeRepB01.getVersion() + "." + placeRepB01.getRevision());
            for (AttributeBridge attrB : attrBs) {
                System.out.println("ATTR: " + attrB.getAttributeId() + "." + attrB.getPlaceRep().getRepId() + " :: " + attrB.getLocale() + " :: " + attrB.getValue());
            }

            System.out.println("\nNEW..............................................\n");
            AttributeBridge attrB01 = dataService.createAttribute(repId, 433, 2020, "fr", "attr-value-fr", "wjohnson000");
            System.out.println("ATTR: " + attrB01.getAttributeId() + "." + attrB01.getPlaceRep().getRepId() + " :: " + attrB01.getLocale() + " :: " + attrB01.getValue());

            System.out.println("\nUPD..............................................\n");
            AttributeBridge attrB02 = dataService.updateAttribute(attrB01.getAttributeId(), repId, 433, 2030, "fr", "attr-value-fr-new", "wjohnson000");
            System.out.println("ATTR: " + attrB02.getAttributeId() + "." + attrB02.getPlaceRep().getRepId() + " :: " + attrB02.getLocale() + " :: " + attrB02.getValue());

            System.out.println("\nALL..............................................\n");
            PlaceRepBridge placeRepB02 = dbRService.getRep(repId, null);
            System.out.println("PLACE-REP: " + placeRepB02.getRepId() + "." + placeRepB02.getVersion() + "." + placeRepB02.getRevision());
            attrBs = placeRepB02.getAllAttributes();
            for (AttributeBridge attrB : attrBs) {
                System.out.println("ATTR: " + attrB.getAttributeId() + "." + attrB.getPlaceRep().getRepId() + " :: " + attrB.getLocale() + " :: " + attrB.getValue());
            }

            System.out.println("\nALL (after delete)...............................\n");
            dataService.deleteAttribute(attrB01.getAttributeId(), repId, "wjohnson000");

            PlaceRepBridge placeRepB03 = dbRService.getRep(repId, null);
            attrBs = placeRepB03.getAllAttributes();
            System.out.println("PLACE-REP: " + placeRepB03.getRepId() + "." + placeRepB03.getVersion() + "." + placeRepB03.getRevision());
            for (AttributeBridge attrB : attrBs) {
                System.out.println("ATTR: " + attrB.getAttributeId() + "." + attrB.getPlaceRep().getRepId() + " :: " + attrB.getLocale() + " :: " + attrB.getValue());
            }
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            System.out.println("Shutting down ...");
            ((ClassPathXmlApplicationContext)appContext).close();
            if (dataService != null) dataService.shutdown();
        }

        System.exit(0);
    }
}
