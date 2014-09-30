package std.wlj.db.refactor;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.service.DbDataService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TestVersion {

    public static void main(String... args) {

        ApplicationContext appContext = null;
        DbDataService dbService = null;

        try {
            System.out.println("Setting up services ...");
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
            BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
            dbService = new DbDataService(ds);

            System.out.println(">>> Latest revision/version ...");
            int revision = dbService.getLatestRevision();
            String version = dbService.getLatestVersion();
            System.out.println("  revision: " + revision);
            System.out.println("   version: " + version);

            System.out.println(">>> All version");
            for (String vv: dbService.getVersions()) {
                System.out.println("   version: " + vv);
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
