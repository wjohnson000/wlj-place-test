package std.wlj.dbnew;

import java.util.Set;

import org.familysearch.standards.place.data.SourceBridge;

import std.wlj.datasource.DbConnectionManager;
import std.wlj.datasource.DbConnectionManager.DbServices;

public class SourceDbServiceTest {

    public static void main(String... args) {

        DbServices dbServices = null;
        try {
            dbServices = DbConnectionManager.getDbServicesWLJ();

            System.out.println("\nALL..............................................\n");
            Set<SourceBridge> sourceBs = dbServices.readService.getSources(false);
            for (SourceBridge sourceB : sourceBs) {
                System.out.println("SRC: " + sourceB.getSourceId() + " :: " + sourceB.getTitle() + " :: " + sourceB.getDescription());
            }

            System.out.println("\nONE..............................................\n");
            SourceBridge sourceB = dbServices.readService.getSourceById(11, false);
            System.out.println("SRC: " + sourceB.getSourceId() + " :: " + sourceB.getTitle() + " :: " + sourceB.getDescription());

            System.out.println("\nNEW..............................................\n");
            SourceBridge sourceB01 = dbServices.writeService.createSource("wlj-title", "wlj-desc", true, "wjohnson000", null);
            System.out.println("SRC-B01: " + sourceB01.getSourceId() + " :: " + sourceB01.getTitle() + " :: " + sourceB01.getDescription());

            System.out.println("\nUPD..............................................\n");
            SourceBridge sourceB02 = dbServices.writeService.updateSource(sourceB01.getSourceId(), "wlj-title-new", "wlj-desc-new", true, "wjohnson000", null);
            System.out.println("SRC-B01: " + sourceB02.getSourceId() + " :: " + sourceB02.getTitle() + " :: " + sourceB02.getDescription());
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            System.out.println("Shutting down ...");
            if (dbServices != null) dbServices.shutdown();
        }

        System.exit(0);
    }
}
