package std.wlj.dbnew;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.service.DbReadableService;
import org.familysearch.standards.place.service.DbWritableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TypeDbTest {

    public static void main(String... args) {
        Map<String,String> names = new HashMap<>();
        names.put("en", "en-name");
        names.put("fr", "fr-name");
        Map<String,String> descr = new HashMap<>();
        descr.put("en", "en-description");
        descr.put("fr", "fr-description");

        ApplicationContext appContext = null;
        try {
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
            BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
            DbReadableService dbRService = new DbReadableService(ds);
            DbWritableService dbWService = new DbWritableService(ds);

            System.out.println("\nALL [Name]........................................\n");
            Set<TypeBridge> typeBs = dbRService.getTypes(TypeBridge.TYPE.NAME);
            for (TypeBridge typeB : typeBs) {
                System.out.println("TYPE: " + typeB.getTypeId() + " :: " + typeB.getCode() + " :: " + typeB.getNames());
            }

            System.out.println("\nONE..............................................\n");
            TypeBridge typeB00 = dbRService.getTypeById(TypeBridge.TYPE.NAME, 444);
            System.out.println("TYPE: " + typeB00.getTypeId() + " :: " + typeB00.getCode() + " :: " + typeB00.getNames());

            System.out.println("\nTWO..............................................\n");
            typeB00 = dbRService.getTypeByCode(TypeBridge.TYPE.NAME, "ISONAME");
            System.out.println("TYPE: " + typeB00.getTypeId() + " :: " + typeB00.getCode() + " :: " + typeB00.getNames());

            System.out.println("\nNEW..............................................\n");
            TypeBridge typeB01 = dbWService.createType(TypeBridge.TYPE.NAME, "N-WLJ", names, descr, true, "wjohnson000");
            System.out.println("TYPE: " + typeB01.getTypeId() + " :: " + typeB01.getCode() + " :: " + typeB01.getNames());

            System.out.println("\nUPD..............................................\n");
            names.put("ru", "ru-name");
            descr.put("ru", "ru-description");
            TypeBridge typeB02 = dbWService.updateType(typeB01.getTypeId(), TypeBridge.TYPE.NAME, "N-WLJ", names, descr, true, "wjohnson000");
            System.out.println("TYPE: " + typeB02.getTypeId() + " :: " + typeB02.getCode() + " :: " + typeB02.getNames());

            System.out.println("\nALL [Name]........................................\n");
            for (TypeBridge typeB : dbRService.getTypes(TypeBridge.TYPE.NAME)) {
                System.out.println("TYPE: " + typeB.getTypeId() + " :: " + typeB.getCode() + " :: " + typeB.getNames());
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
