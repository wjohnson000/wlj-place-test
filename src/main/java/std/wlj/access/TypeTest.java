package std.wlj.access;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.dbcp.BasicDataSource;
import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.service.DbReadableService;
import org.familysearch.standards.place.service.DbWritableService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class TypeTest {

    public static void main(String... args) {
        Map<String,String> names = new HashMap<>();
        names.put("en", "en-name");
        names.put("fr", "fr-name");
        Map<String,String> descr = new HashMap<>();
        descr.put("en", "en-description");
        descr.put("fr", "fr-description");

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

            System.out.println("\nALL [Name]........................................\n");
            Set<TypeBridge> typeBs = dataService.getTypes(TypeBridge.TYPE.NAME, false);
            for (TypeBridge typeB : typeBs) {
                System.out.println("TYPE: " + typeB.getTypeId() + " :: " + typeB.getCode() + " :: " + typeB.getNames());
            }

            System.out.println("\nONE..............................................\n");
            TypeBridge typeB00 = dataService.getTypeById(TypeBridge.TYPE.NAME, 444, false);
            System.out.println("TYPE: " + typeB00.getTypeId() + " :: " + typeB00.getCode() + " :: " + typeB00.getNames());

            System.out.println("\nTWO..............................................\n");
            typeB00 = dataService.getTypeByCode(TypeBridge.TYPE.NAME, "ISONAME", false);
            System.out.println("TYPE: " + typeB00.getTypeId() + " :: " + typeB00.getCode() + " :: " + typeB00.getNames());

            System.out.println("\nNEW..............................................\n");
            TypeBridge typeB01 = dataService.createType(TypeBridge.TYPE.NAME, "N-WLJ-02", names, descr, true, "wjohnson000", null);
            System.out.println("TYPE: " + typeB01.getTypeId() + " :: " + typeB01.getCode() + " :: " + typeB01.getNames());

            System.out.println("\nUPD..............................................\n");
            names.put("ru", "ru-name");
            descr.put("ru", "ru-description");
            TypeBridge typeB02 = dataService.updateType(typeB01.getTypeId(), TypeBridge.TYPE.NAME, "N-WLJ-02", names, descr, true, "wjohnson000", null);
            System.out.println("TYPE: " + typeB02.getTypeId() + " :: " + typeB02.getCode() + " :: " + typeB02.getNames());

            System.out.println("\nALL [Name]........................................\n");
            for (TypeBridge typeB : dataService.getTypes(TypeBridge.TYPE.NAME, false)) {
                System.out.println("TYPE: " + typeB.getTypeId() + " :: " + typeB.getCode() + " :: " + typeB.getNames());
            }
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            System.out.println("Shutting down ...");
            ((ClassPathXmlApplicationContext)appContext).close();
        }

        System.exit(0);
    }
}
