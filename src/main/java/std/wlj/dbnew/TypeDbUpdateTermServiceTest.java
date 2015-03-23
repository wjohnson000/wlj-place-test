package std.wlj.dbnew;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.service.DbWritableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TypeDbUpdateTermServiceTest {

    public static void main(String... args) {
        Map<String,String> names = new HashMap<>();
        names.put("en", "en-name");
        names.put("fr", "fr-name");
        Map<String,String> descr = new HashMap<>();
        descr.put("en", "en-description");
        descr.put("fr", "fr-description");

        ApplicationContext appContext = null;
        try {
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost-wlj.xml");
            BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
            DbWritableService dbWService = new DbWritableService(ds);

            System.out.println("\nNEW..............................................\n");
            TypeBridge typeB01 = dbWService.createType(TypeBridge.TYPE.NAME, "N-WLJ-BB", names, descr, true, "wjohnson000", null);
            System.out.println("TYPE: " + typeB01.getTypeId() + " :: " + typeB01.getCode() + " :: " + typeB01.getNames());

            System.out.println("\nUPD..............................................\n");
            names.put("ru", "ru-name");
            descr.put("ru", "ru-description");
            names.put("fr", "fr-name-new");
            descr.put("fr", "fr-description");
            TypeBridge typeB02 = dbWService.updateType(typeB01.getTypeId(), TypeBridge.TYPE.NAME, "N-WLJ-BB", names, descr, true, "wjohnson000", null);
            System.out.println("TYPE: " + typeB02.getTypeId() + " :: " + typeB02.getCode() + " :: " + typeB02.getNames());
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            System.out.println("Shutting down ...");
            ((ClassPathXmlApplicationContext)appContext).close();
        }

        System.exit(0);
    }
}
