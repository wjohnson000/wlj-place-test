package std.wlj.access;

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
            int repId = 3698531;

            dbServices = DbConnectionManager.getDbServicesSams();
            solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-dbload-7.1.0");
            dataService = new PlaceDataServiceImpl(solrService, dbServices.readService, dbServices.writeService);

            PlaceRepBridge placeRepB01 = dbServices.readService.getRep(repId);
            if (placeRepB01 == null) {
                System.out.println("Not found --- repId=" + repId);
                return;
            }

            System.out.println("\nALL ..........................................................................");
            System.out.println("REP: " + placeRepB01.getRepId() + "." + placeRepB01.getRevision());
            placeRepB01.getAllAttributes().forEach(attrB -> print(attrB));

//            System.out.println("\nNEW ........................................................................\n");
//            AttributeBridge attrB01 = dataService.createAttribute(repId, 433, 1900, 2000, "fr", "attr-value-fr", "copyright", null, "wjohnson000", null);
//            print(attrB01);
//
//            System.out.println("\nUPD ........................................................................\n");
//            AttributeBridge attrB02 = dataService.updateAttribute(attrB01.getAttributeId(), repId, 433, 1900, 2020, "fr", "attr-value-fr-new", "copyright", "http://copyright.com", "wjohnson000", null);
//            print(attrB02);
//
//            System.out.println("\nALL ..........................................................................");
//            PlaceRepBridge placeRepB02 = dbServices.readService.getRep(repId);
//            System.out.println("REP: " + placeRepB02.getRepId() + "." + placeRepB02.getRevision());
//            placeRepB02.getAllAttributes().forEach(attrB -> print(attrB));

        } catch(Exception ex) {
            System.out.println("\nEx: " + ex.getMessage());
        } finally {
            System.out.println("\nShutting down ...");
            if (dataService != null) dataService.shutdown();
            if (dbServices != null) dbServices.shutdown();
            if (solrService != null) solrService.shutdown();
        }

        System.exit(0);
    }

    static void print(AttributeBridge attrB) {
        StringBuilder buff = new StringBuilder();
        buff.append("  ATTR");
        buff.append("|").append(attrB.getAttributeId());
        buff.append("|rev=").append(attrB.getRevision());
        buff.append("|rep=").append(attrB.getPlaceRep().getRepId());
        buff.append("|typ=").append(attrB.getType().getTypeId());
        buff.append("|loc=").append(attrB.getLocale());
        buff.append("|val=").append(attrB.getValue());
        buff.append("|rng=").append(attrB.getFromYear()).append("-").append(attrB.getToYear());
        buff.append("|cpy=").append(attrB.getCopyrightNotice());
        buff.append("|url=").append(attrB.getCopyrightUrl());
        System.out.println(buff.toString());
    }
}
