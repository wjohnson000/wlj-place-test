package std.wlj.access;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.ExternalReferenceBridge;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.service.DbReadableService;
import org.familysearch.standards.place.service.DbWritableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class ExtXrefTest {

    public static void main(String... args) {
//      System.setProperty("solr.master.url", "C:/tools/solr/data/tokoro");
//      System.setProperty("solr.solr.home", "C:/tools/solr/data/tokoro");
      System.setProperty("solr.master.url", "http://localhost:8983/solr/places");
      System.setProperty("solr.solr.home", "http://localhost:8983/solr/places");
      System.setProperty("solr.master", "false");
      System.setProperty("solr.slave", "false");

      ApplicationContext appContext = null;
      PlaceDataServiceImpl dataService = null;
      try {
          appContext = new ClassPathXmlApplicationContext("postgres-context-aws-int.xml");
          BasicDataSource ds = (BasicDataSource)appContext.getBean("dataSource");
          SolrService       solrService = new SolrService();
          DbReadableService dbRService  = new DbReadableService(ds);
          DbWritableService dbWService  = new DbWritableService(ds);
          dataService = new PlaceDataServiceImpl(solrService, dbRService, dbWService);

          Set<ExternalReferenceBridge> extXrefBs = dataService.getExternalReferences("RANDMC", "ABC-DEF");
          System.out.println("\nALL..............................................\n");
          for (ExternalReferenceBridge extXrefB : extXrefBs) {
              System.out.println("EXT-XREF: " + extXrefB.getRefId() + "." + extXrefB.getReference() + " :: " + extXrefB.getType().getCode() + " :: " + extXrefB.getPlaceRep().getRepId());
          }

          System.out.println("\nNEW/UPD..........................................\n");
          Set<Integer> repIds = new HashSet<>(Arrays.asList(2, 3, 4, 5));
          extXrefBs = dataService.createOrUpdateExternalReference("RANDMC", "ABC-DEF", repIds, "wjohnson000");
          for (ExternalReferenceBridge extXrefB : extXrefBs) {
              System.out.println("EXT-XREF: " + extXrefB.getRefId() + "." + extXrefB.getReference() + " :: " + extXrefB.getType().getCode() + " :: " + extXrefB.getPlaceRep().getRepId());
          }

          System.out.println("\nNEW/UPD..........................................\n");
          repIds = new HashSet<>(Arrays.asList(4, 5, 6));
          extXrefBs = dataService.createOrUpdateExternalReference("RANDMC", "ABC-DEF", repIds, "wjohnson000");
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
