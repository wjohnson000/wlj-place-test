package std.wlj.dbnew;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.data.ExternalReferenceBridge;
import org.familysearch.standards.place.service.DbReadableService;
import org.familysearch.standards.place.service.DbWritableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class ExtXrefDbServiceTest {

    public static void main(String... args) {

        ApplicationContext appContext = null;
        try {
            appContext = new ClassPathXmlApplicationContext("postgres-context-localhost-wlj.xml");
            BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
            DbReadableService dbRService = new DbReadableService(ds);
            DbWritableService dbWService = new DbWritableService(ds);

            Set<ExternalReferenceBridge> extXrefBs = dbRService.getExternalReferences("RANDMC", "ABC-DEF");
            System.out.println("\nALL..............................................\n");
            for (ExternalReferenceBridge extXrefB : extXrefBs) {
                System.out.println("EXT-XREF: " + extXrefB.getRefId() + "." + extXrefB.getReference() + " :: " + extXrefB.getType().getCode() + " :: " + extXrefB.getPlaceRep().getRepId());
            }

            System.out.println("\nNEW/UPD..........................................\n");
            Set<Integer> repIds = new HashSet<>(Arrays.asList(2, 3, 4, 5));
            extXrefBs = dbWService.createOrUpdateExternalReference("RANDMC", "ABC-DEF", repIds, "wjohnson000", null);
            for (ExternalReferenceBridge extXrefB : extXrefBs) {
                System.out.println("EXT-XREF: " + extXrefB.getRefId() + "." + extXrefB.getReference() + " :: " + extXrefB.getType().getCode() + " :: " + extXrefB.getPlaceRep().getRepId());
            }

            System.out.println("\nNEW/UPD..........................................\n");
            repIds = new HashSet<>(Arrays.asList(4, 5, 6));
            extXrefBs = dbWService.createOrUpdateExternalReference("RANDMC", "ABC-DEF", repIds, "wjohnson000", null);
            for (ExternalReferenceBridge extXrefB : extXrefBs) {
                System.out.println("EXT-XREF: " + extXrefB.getRefId() + "." + extXrefB.getReference() + " :: " + extXrefB.getType().getCode() + " :: " + extXrefB.getPlaceRep().getRepId());
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
