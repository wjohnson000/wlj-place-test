package std.wlj.dbnew;

import java.util.List;

import org.familysearch.standards.place.data.AttributeBridge;
import org.familysearch.standards.place.data.PlaceRepBridge;

import std.wlj.datasource.DbConnectionManager;
import std.wlj.datasource.DbConnectionManager.DbServices;

public class AttributeDbServiceTest {

    public static void main(String... args) {

        DbServices dbServices = null;
        try {
            dbServices = DbConnectionManager.getDbServicesWLJ();

            PlaceRepBridge placeRepB = dbServices.readService.getRep(2);
            List<AttributeBridge> attrBs = placeRepB.getAllAttributes();
            System.out.println("\nALL..............................................\n");
            for (AttributeBridge attrB : attrBs) {
                System.out.println("ATTR: " + attrB.getAttributeId() + "." + attrB.getPlaceRep().getRepId() + " :: " + attrB.getLocale() + " :: " + attrB.getValue());
            }

            System.out.println("\nNEW..............................................\n");
            AttributeBridge attrB01 = dbServices.writeService.createAttribute(2, 433, 2020, "fr", "attr-value-fr", "wjohnson000", null);
            System.out.println("ATTR: " + attrB01.getAttributeId() + "." + attrB01.getPlaceRep().getRepId() + " :: " + attrB01.getLocale() + " :: " + attrB01.getValue());

            System.out.println("\nUPD..............................................\n");
            AttributeBridge attrB02 = dbServices.writeService.updateAttribute(attrB01.getAttributeId(), 2, 433, 2030, "fr", "attr-value-fr-new", "wjohnson000", null);
            System.out.println("ATTR: " + attrB02.getAttributeId() + "." + attrB02.getPlaceRep().getRepId() + " :: " + attrB02.getLocale() + " :: " + attrB02.getValue());

            System.out.println("\nALL..............................................\n");
            placeRepB = dbServices.readService.getRep(2);
            attrBs = placeRepB.getAllAttributes();
            for (AttributeBridge attrB : attrBs) {
                System.out.println("ATTR: " + attrB.getAttributeId() + "." + attrB.getPlaceRep().getRepId() + " :: " + attrB.getLocale() + " :: " + attrB.getValue());
            }

            System.out.println("\nALL (after delete)...............................\n");
            dbServices.writeService.deleteAttribute(attrB01.getAttributeId(), 2, "wjohnson000", null);

            placeRepB = dbServices.readService.getRep(2);
            attrBs = placeRepB.getAllAttributes();
            for (AttributeBridge attrB : attrBs) {
                System.out.println("ATTR: " + attrB.getAttributeId() + "." + attrB.getPlaceRep().getRepId() + " :: " + attrB.getLocale() + " :: " + attrB.getValue());
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
