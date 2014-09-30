package std.wlj.db.refactor;

import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.service.DbDataService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TestType {

    public static void main(String... args) {

        ApplicationContext appContext = null;
        DbDataService dbService = null;

        try {
            System.out.println("Setting up services ...");
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
            BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
            dbService = new DbDataService(ds);

            System.out.println(">>> Valid stuff ...");
            TypeBridge typeB = dbService.getTypeByCode(TypeBridge.TYPE.PLACE, "BLDG");
            PrintUtil.printIt(typeB);

            typeB = dbService.getTypeById(TypeBridge.TYPE.PLACE, 26);
            PrintUtil.printIt(typeB);

            System.out.println(">>> Invalid stuff ...");
            typeB = dbService.getTypeByCode(TypeBridge.TYPE.PLACE, "PLC_DATE");
            PrintUtil.printIt(typeB);

            typeB = dbService.getTypeById(TypeBridge.TYPE.PLACE, 443);
            PrintUtil.printIt(typeB);

            System.out.println(">>> List of types ...");
            Set<TypeBridge> typeBs = dbService.getTypes(TypeBridge.TYPE.CITATION);
            for (TypeBridge typeBB : typeBs) {
                PrintUtil.printIt(typeBB);
            }
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            System.out.println("Shutting down ...");
            dbService.shutdown();
            ((ClassPathXmlApplicationContext)appContext).close();
        }

        System.exit(0);
    }
}
