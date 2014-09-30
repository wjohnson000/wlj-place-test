package std.wlj.db.refactor;

import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.data.ExternalReferenceBridge;
import org.familysearch.standards.place.service.DbDataService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TestExtXref {

    public static void main(String... args) {

        ApplicationContext appContext = null;
        DbDataService dbService = null;

        try {
            System.out.println("Setting up services ...");
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost.xml");
            BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
            dbService = new DbDataService(ds);

            System.out.println(">>> Valid stuff ...");
            List<ExternalReferenceBridge> extXrefBs = dbService.getExternalReference("NGEO", "Issue 5 Page 55");
            for (ExternalReferenceBridge extXrefB : extXrefBs) {
                PrintUtil.printIt(extXrefB);
            }

            System.out.println(">>> Valid stuff ... Two");
            extXrefBs = dbService.getExternalReference("RANDMC", "ABC-DEF");
            for (ExternalReferenceBridge extXrefB : extXrefBs) {
                PrintUtil.printIt(extXrefB);
            }

            System.out.println(">>> Invalid stuff ...");
            extXrefBs = dbService.getExternalReference("NGEO", "ABC-DEF");
            for (ExternalReferenceBridge extXrefB : extXrefBs) {
                PrintUtil.printIt(extXrefB);
            }

            System.out.println(">>> Invalid stuff ... Two");
            extXrefBs = dbService.getExternalReference("NOGO", "ABC-DEF");
            for (ExternalReferenceBridge extXrefB : extXrefBs) {
                PrintUtil.printIt(extXrefB);
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
