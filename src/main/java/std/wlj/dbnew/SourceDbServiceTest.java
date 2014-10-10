package std.wlj.dbnew;

import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.data.SourceBridge;
import org.familysearch.standards.place.service.DbReadableService;
import org.familysearch.standards.place.service.DbWritableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class SourceDbServiceTest {

    public static void main(String... args) {

        ApplicationContext appContext = null;
        try {
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
            BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
            DbReadableService dbRService = new DbReadableService(ds);
            DbWritableService dbWService = new DbWritableService(ds);

            System.out.println("\nALL..............................................\n");
            Set<SourceBridge> sourceBs = dbRService.getSources();
            for (SourceBridge sourceB : sourceBs) {
                System.out.println("SRC: " + sourceB.getSourceId() + " :: " + sourceB.getTitle() + " :: " + sourceB.getDescription());
            }

            System.out.println("\nONE..............................................\n");
            SourceBridge sourceB = dbRService.getSourceById(11);
            System.out.println("SRC: " + sourceB.getSourceId() + " :: " + sourceB.getTitle() + " :: " + sourceB.getDescription());

            System.out.println("\nNEW..............................................\n");
            SourceBridge sourceB01 = dbWService.createSource("wlj-title", "wlj-desc", true, "wjohnson000");
            System.out.println("SRC-B01: " + sourceB01.getSourceId() + " :: " + sourceB01.getTitle() + " :: " + sourceB01.getDescription());

            System.out.println("\nUPD..............................................\n");
            SourceBridge sourceB02 = dbWService.updateSource(sourceB01.getSourceId(), "wlj-title-new", "wlj-desc-new", true, "wjohnson000");
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
