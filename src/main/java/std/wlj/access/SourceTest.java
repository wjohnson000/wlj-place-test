package std.wlj.access;

import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.SourceBridge;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.service.DbReadableService;
import org.familysearch.standards.place.service.DbWritableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class SourceTest {

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
            appContext = new ClassPathXmlApplicationContext("postgres-context-aws-int.xml");
            BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
            SolrService       solrService = new SolrService();
            DbReadableService dbRService  = new DbReadableService(ds);
            DbWritableService dbWService  = new DbWritableService(ds);
            dataService = new PlaceDataServiceImpl(solrService, dbRService, dbWService);

            System.out.println("\nALL..............................................\n");
            Set<SourceBridge> sourceBs = dataService.getSources(false);
            for (SourceBridge sourceB : sourceBs) {
                System.out.println("SRC: " + sourceB.getSourceId() + " :: " + sourceB.getTitle() + " :: " + sourceB.getDescription());
            }

            System.out.println("\nONE..............................................\n");
            SourceBridge sourceB = dataService.getSourceById(11, false);
            System.out.println("SRC: " + sourceB.getSourceId() + " :: " + sourceB.getTitle() + " :: " + sourceB.getDescription());

            System.out.println("\nNEW..............................................\n");
            SourceBridge sourceB01 = dataService.createSource("wlj-title", "wlj-desc", true, "wjohnson000", null);
            System.out.println("SRC-B01: " + sourceB01.getSourceId() + " :: " + sourceB01.getTitle() + " :: " + sourceB01.getDescription());

            System.out.println("\nUPD..............................................\n");
            SourceBridge sourceB02 = dataService.updateSource(sourceB01.getSourceId(), "wlj-title-new", "wlj-desc-new", true, "wjohnson000", null);
            System.out.println("SRC-B01: " + sourceB02.getSourceId() + " :: " + sourceB02.getTitle() + " :: " + sourceB02.getDescription());
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            System.out.println("Shutting down ...");
            ((ClassPathXmlApplicationContext)appContext).close();
        }

        System.exit(0);
    }
}
