package std.wlj.dbnew;

import java.util.HashMap;
import java.util.Map;

import org.familysearch.standards.place.data.TypeBridge;

import std.wlj.datasource.DbConnectionManager;
import std.wlj.datasource.DbConnectionManager.DbServices;

public class TypeDbUpdateTermServiceTest {

    public static void main(String... args) {
        Map<String,String> names = new HashMap<>();
        names.put("en", "en-name");
        names.put("fr", "fr-name");
        Map<String,String> descr = new HashMap<>();
        descr.put("en", "en-description");
        descr.put("fr", "fr-description");

        DbServices dbServices = null;
        try {
            dbServices = DbConnectionManager.getDbServicesWLJ();

            System.out.println("\nNEW..............................................\n");
            TypeBridge typeB01 = dbServices.writeService.createType(TypeBridge.TYPE.NAME, "N-WLJ-BB", names, descr, true, "wjohnson000", null);
            System.out.println("TYPE: " + typeB01.getTypeId() + " :: " + typeB01.getCode() + " :: " + typeB01.getNames());

            System.out.println("\nUPD..............................................\n");
            names.put("ru", "ru-name");
            descr.put("ru", "ru-description");
            names.put("fr", "fr-name-new");
            descr.put("fr", "fr-description");
            TypeBridge typeB02 = dbServices.writeService.updateType(typeB01.getTypeId(), TypeBridge.TYPE.NAME, "N-WLJ-BB", names, descr, true, "wjohnson000", null);
            System.out.println("TYPE: " + typeB02.getTypeId() + " :: " + typeB02.getCode() + " :: " + typeB02.getNames());
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            System.out.println("Shutting down ...");
            if (dbServices != null) dbServices.shutdown();
        }

        System.exit(0);
    }
}
