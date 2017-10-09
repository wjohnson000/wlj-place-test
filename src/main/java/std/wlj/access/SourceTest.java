package std.wlj.access;

import java.util.Set;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.SourceBridge;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.datasource.DbConnectionManager;
import std.wlj.datasource.DbConnectionManager.DbServices;
import std.wlj.util.SolrManager;


public class SourceTest {

    public static void main(String... args) {
        PlaceDataServiceImpl dataService = null;
        DbServices dbServices = null;
        SolrService solrService = null;

        try {
            dbServices = DbConnectionManager.getDbServicesSams();
            solrService = SolrManager.awsIntService(true);
            dataService = new PlaceDataServiceImpl(solrService, dbServices.readService, dbServices.writeService);

            System.out.println("\nALL..............................................\n");
            Set<SourceBridge> sourceBs = dataService.getSources();
            for (SourceBridge sourceB : sourceBs) {
                System.out.println("SRC: " + sourceB.getSourceId() + " :: " + sourceB.getTitle() + " :: " + sourceB.getDescription());
            }

            System.out.println("\nONE..............................................\n");
            SourceBridge sourceB = dataService.getSourceById(11);
            System.out.println("SRC: " + sourceB.getSourceId() + " :: " + sourceB.getTitle() + " :: " + sourceB.getDescription());

            System.out.println("\nNEW..............................................\n");
            SourceBridge sourceB01 = dataService.createSource("wlj-title", "wlj-desc", true, "wjohnson000", null);
            System.out.println("SRC-B01: " + sourceB01.getSourceId() + " :: " + sourceB01.getTitle() + " :: " + sourceB01.getDescription());

            System.out.println("\nUPD..............................................\n");
            SourceBridge sourceB02 = dataService.updateSource(sourceB01.getSourceId(), "wlj-title-new", "wlj-desc-new", true, "wjohnson000", null);
            System.out.println("SRC-B01: " + sourceB02.getSourceId() + " :: " + sourceB02.getTitle() + " :: " + sourceB02.getDescription());
        } catch(Exception ex) {
            System.out.println("Ex: " + ex.getMessage());
        } finally {
            System.out.println("Shutting down ...");
            if (dataService != null) dataService.shutdown();
            if (dbServices != null) dbServices.shutdown();
            if (solrService != null) solrService.shutdown();
        }

        System.exit(0);
    }
}
