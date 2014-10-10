package std.wlj.dbnew;

import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.data.AttributeBridge;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.service.DbReadableService;
import org.familysearch.standards.place.service.DbWritableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class CreateAttribute02DbService {

    public static void main(String... args) {

        ApplicationContext appContext = null;
        try {
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
            BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
            DbReadableService dbRService = new DbReadableService(ds);
            DbWritableService dbWService = new DbWritableService(ds);

            PlaceRepBridge placeRepB = dbRService.getRep(1, null);
            List<AttributeBridge> attrBs = placeRepB.getAllAttributes();
            for (AttributeBridge attrB : attrBs) {
                System.out.println("ATTR: " + attrB.getAttributeId() + "." + attrB.getPlaceRep().getRepId() + " :: " + attrB.getLocale() + " :: " + attrB.getValue());
            }

            System.out.println("\n..............................................\n");
            AttributeBridge attrB = dbWService.createAttribute(1, 433, 2020, "fr", "attr-value-fr", "wjohnson000");
            System.out.println("ATTR: " + attrB.getAttributeId() + "." + attrB.getPlaceRep().getRepId() + " :: " + attrB.getLocale() + " :: " + attrB.getValue());
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            System.out.println("Shutting down ...");
            ((ClassPathXmlApplicationContext)appContext).close();
        }

        System.exit(0);
    }
}
