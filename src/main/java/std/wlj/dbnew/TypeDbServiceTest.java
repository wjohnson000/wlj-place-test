package std.wlj.dbnew;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.familysearch.standards.place.data.TypeBridge;

import std.wlj.util.DbConnectionManager;
import std.wlj.util.DbConnectionManager.DbServices;

public class TypeDbServiceTest {

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

            System.out.println("\nALL [Name]........................................\n");
            Set<TypeBridge> typeBs = dbServices.readService.getTypes(TypeBridge.TYPE.NAME);
            for (TypeBridge typeB : typeBs) {
                System.out.println("TYPE: " + typeB.getTypeId() + " :: " + typeB.getCode() + " :: " + typeB.getNames());
            }

            System.out.println("\nONE..............................................\n");
            TypeBridge typeB00 = dbServices.readService.getTypeById(TypeBridge.TYPE.NAME, 444);
            System.out.println("TYPE: " + typeB00.getTypeId() + " :: " + typeB00.getCode() + " :: " + typeB00.getNames());

            System.out.println("\nTWO..............................................\n");
            typeB00 = dbServices.readService.getTypeByCode(TypeBridge.TYPE.NAME, "ISONAME");
            System.out.println("TYPE: " + typeB00.getTypeId() + " :: " + typeB00.getCode() + " :: " + typeB00.getNames());

            System.out.println("\nNEW..............................................\n");
            TypeBridge typeB01 = dbServices.writeService.createType(TypeBridge.TYPE.NAME, "N-WLJ", names, descr, true, "wjohnson000", null);
            System.out.println("TYPE: " + typeB01.getTypeId() + " :: " + typeB01.getCode() + " :: " + typeB01.getNames());

            System.out.println("\nUPD..............................................\n");
            names.put("ru", "ru-name");
            descr.put("ru", "ru-description");
            TypeBridge typeB02 = dbServices.writeService.updateType(typeB01.getTypeId(), TypeBridge.TYPE.NAME, "N-WLJ", names, descr, true, "wjohnson000", null);
            System.out.println("TYPE: " + typeB02.getTypeId() + " :: " + typeB02.getCode() + " :: " + typeB02.getNames());

            System.out.println("\nALL [Name]........................................\n");
            for (TypeBridge typeB : dbServices.readService.getTypes(TypeBridge.TYPE.NAME)) {
                System.out.println("TYPE: " + typeB.getTypeId() + " :: " + typeB.getCode() + " :: " + typeB.getNames());
            }
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            System.out.println("Shutting down ...");
            if (dbServices != null) dbServices.shutdown();
        }

        System.exit(0);
    }
}
