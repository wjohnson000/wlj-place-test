package std.wlj.access;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.util.DbConnectionManager;
import std.wlj.util.SolrManager;
import std.wlj.util.DbConnectionManager.DbServices;


public class TypeTest {

    public static void main(String... args) {
        Map<String,String> names = new HashMap<>();
        names.put("en", "en-name");
        names.put("fr", "fr-name");
        Map<String,String> descr = new HashMap<>();
        descr.put("en", "en-description");
        descr.put("fr", "fr-description");

        PlaceDataServiceImpl dataService = null;
        DbServices dbServices = null;
        SolrService solrService = null;

        try {
            dbServices = DbConnectionManager.getDbServicesWLJ();
            solrService = SolrManager.awsIntService(true);
            dataService = new PlaceDataServiceImpl(solrService, dbServices.readService, dbServices.writeService);

            System.out.println("\nALL [Name]........................................\n");
            Set<TypeBridge> typeBs = dataService.getTypes(TypeBridge.TYPE.NAME);
            for (TypeBridge typeB : typeBs) {
                System.out.println("TYPE: " + typeB.getTypeId() + " :: " + typeB.getCode() + " :: " + typeB.getNames());
            }

            System.out.println("\nONE..............................................\n");
            TypeBridge typeB00 = dataService.getTypeById(TypeBridge.TYPE.NAME, 444);
            System.out.println("TYPE: " + typeB00.getTypeId() + " :: " + typeB00.getCode() + " :: " + typeB00.getNames());

            System.out.println("\nTWO..............................................\n");
            typeB00 = dataService.getTypeByCode(TypeBridge.TYPE.NAME, "ISONAME");
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
            for (TypeBridge typeB : dataService.getTypes(TypeBridge.TYPE.NAME)) {
                System.out.println("TYPE: " + typeB.getTypeId() + " :: " + typeB.getCode() + " :: " + typeB.getNames());
            }
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            System.out.println("Shutting down ...");
            if (dataService != null) dataService.shutdown();
            if (dbServices != null) dbServices.shutdown();
            if (solrService != null) solrService.shutdown();
        }

        System.exit(0);
    }
}
