package std.wlj.db.refactor;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.data.SourceBridge;
import org.familysearch.standards.place.service.DbDataService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TestSource {

    public static void main(String... args) {

        ApplicationContext appContext = null;
        DbDataService dbService = null;

        try {
            System.out.println("Setting up services ...");
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
            BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
            dbService = new DbDataService(ds);

            System.out.println(">>> A Source ...");
            SourceBridge sourceB = dbService.getSourceById(25);
            PrintUtil.printIt(sourceB);

            System.out.println(">>> Non-existent source ...");
            sourceB = dbService.getSourceById(11111);
            PrintUtil.printIt(sourceB);

            System.out.println(">>> All sources ...");
            for (SourceBridge sourceBB : dbService.getSources()) {
                PrintUtil.printIt(sourceBB);
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
