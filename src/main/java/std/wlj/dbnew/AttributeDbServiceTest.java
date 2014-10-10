package std.wlj.dbnew;

import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.data.AttributeBridge;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.service.DbReadableService;
import org.familysearch.standards.place.service.DbWritableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class AttributeDbServiceTest {

    public static void main(String... args) {

        ApplicationContext appContext = null;
        try {
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
            BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
            DbReadableService dbRService = new DbReadableService(ds);
            DbWritableService dbWService = new DbWritableService(ds);

            PlaceRepBridge placeRepB = dbRService.getRep(2, null);
            List<AttributeBridge> attrBs = placeRepB.getAllAttributes();
            System.out.println("\nALL..............................................\n");
            for (AttributeBridge attrB : attrBs) {
                System.out.println("ATTR: " + attrB.getAttributeId() + "." + attrB.getPlaceRep().getRepId() + " :: " + attrB.getLocale() + " :: " + attrB.getValue());
            }

            System.out.println("\nNEW..............................................\n");
            AttributeBridge attrB01 = dbWService.createAttribute(2, 433, 2020, "fr", "attr-value-fr", "wjohnson000");
            System.out.println("ATTR: " + attrB01.getAttributeId() + "." + attrB01.getPlaceRep().getRepId() + " :: " + attrB01.getLocale() + " :: " + attrB01.getValue());

            System.out.println("\nUPD..............................................\n");
            AttributeBridge attrB02 = dbWService.updateAttribute(attrB01.getAttributeId(), 2, 433, 2030, "fr", "attr-value-fr-new", "wjohnson000");
            System.out.println("ATTR: " + attrB02.getAttributeId() + "." + attrB02.getPlaceRep().getRepId() + " :: " + attrB02.getLocale() + " :: " + attrB02.getValue());

            System.out.println("\nALL..............................................\n");
            placeRepB = dbRService.getRep(2, null);
            attrBs = placeRepB.getAllAttributes();
            for (AttributeBridge attrB : attrBs) {
                System.out.println("ATTR: " + attrB.getAttributeId() + "." + attrB.getPlaceRep().getRepId() + " :: " + attrB.getLocale() + " :: " + attrB.getValue());
            }

            System.out.println("\nALL (after delete)...............................\n");
            dbWService.deleteAttribute(attrB01.getAttributeId(), 2, "wjohnson000");

            placeRepB = dbRService.getRep(2, null);
            attrBs = placeRepB.getAllAttributes();
            for (AttributeBridge attrB : attrBs) {
                System.out.println("ATTR: " + attrB.getAttributeId() + "." + attrB.getPlaceRep().getRepId() + " :: " + attrB.getLocale() + " :: " + attrB.getValue());
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
