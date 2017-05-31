package std.wlj.access;

import java.util.List;

import org.familysearch.standards.place.access.PlaceDataServiceImpl;
import org.familysearch.standards.place.data.AttributeBridge;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.datasource.DbConnectionManager;
import std.wlj.datasource.DbConnectionManager.DbServices;
import std.wlj.util.SolrManager;


public class AttributeTest {

    public static void main(String... args) {
        PlaceDataServiceImpl dataService = null;
        DbServices dbServices = null;
        SolrService solrService = null;

        try {
            int repId = 145;

            dbServices = DbConnectionManager.getDbServicesWLJ();
            solrService = SolrManager.awsIntService(true);
            dataService = new PlaceDataServiceImpl(solrService, dbServices.readService, dbServices.writeService);

            PlaceRepBridge placeRepB01 = dbServices.readService.getRep(repId);
            if (placeRepB01 == null) {
                System.out.println("Not found --- repId=" + repId);
                return;
            }

            List<AttributeBridge> attrBs = placeRepB01.getAllAttributes();
            System.out.println("\nALL..............................................\n");
            System.out.println("PLACE-REP: " + placeRepB01.getRepId() + "." + placeRepB01.getRevision());
            for (AttributeBridge attrB : attrBs) {
                System.out.println("ATTR: " + attrB.getAttributeId() + "." + attrB.getPlaceRep().getRepId() + " :: " + attrB.getLocale() + " :: " + attrB.getValue());
            }

            System.out.println("\nNEW..............................................\n");
            AttributeBridge attrB01 = dataService.createAttribute(repId, 433, 2020, "fr", "attr-value-fr", "wjohnson000", null);
            System.out.println("ATTR: " + attrB01.getAttributeId() + "." + attrB01.getPlaceRep().getRepId() + " :: " + attrB01.getLocale() + " :: " + attrB01.getValue());

            System.out.println("\nUPD..............................................\n");
            AttributeBridge attrB02 = dataService.updateAttribute(attrB01.getAttributeId(), repId, 433, 2030, "fr", "attr-value-fr-new", "wjohnson000", null);
            System.out.println("ATTR: " + attrB02.getAttributeId() + "." + attrB02.getPlaceRep().getRepId() + " :: " + attrB02.getLocale() + " :: " + attrB02.getValue());

            System.out.println("\nALL..............................................\n");
            PlaceRepBridge placeRepB02 = dbServices.readService.getRep(repId);
            System.out.println("PLACE-REP: " + placeRepB02.getRepId() + "." + placeRepB02.getRevision());
            attrBs = placeRepB02.getAllAttributes();
            for (AttributeBridge attrB : attrBs) {
                System.out.println("ATTR: " + attrB.getAttributeId() + "." + attrB.getPlaceRep().getRepId() + " :: " + attrB.getLocale() + " :: " + attrB.getValue());
            }

            System.out.println("\nALL (after delete)...............................\n");
            dataService.deleteAttribute(attrB01.getAttributeId(), repId, "wjohnson000", null);

            PlaceRepBridge placeRepB03 = dbServices.readService.getRep(repId);
            attrBs = placeRepB03.getAllAttributes();
            System.out.println("PLACE-REP: " + placeRepB03.getRepId() + "." + placeRepB03.getRevision());
            for (AttributeBridge attrB : attrBs) {
                System.out.println("ATTR: " + attrB.getAttributeId() + "." + attrB.getPlaceRep().getRepId() + " :: " + attrB.getLocale() + " :: " + attrB.getValue());
            }
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
