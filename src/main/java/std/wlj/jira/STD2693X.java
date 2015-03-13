package std.wlj.jira;

import java.util.Arrays;

import org.familysearch.standards.place.data.AppDataManager;
import org.familysearch.standards.place.data.PlaceNameBridge;
import org.familysearch.standards.place.data.VariantNameSorter;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrService;


public class STD2693X {
    public static void main(String... args) throws Exception {
        String solrHome = "http://familysearch.org/int-solr/places";

        System.setProperty("solr.solr.home", solrHome);
        System.setProperty("solr.master.url", solrHome);
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");

        SolrService  solrService = new SolrService();
        AppDataManager appDataManager = new AppDataManager(solrService);
        VariantNameSorter.initMap(appDataManager);

        PlaceRepDoc doc = solrService.findPlaceRep(266, null);

        System.out.println("ID: " + doc.getId() + " --> " + doc.getRevision() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()));
        System.out.println("  FWD: " + doc.getForwardRevision());
        System.out.println("  DSP: " + doc.getAllDisplayNames());
        System.out.println("  DEL: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
//            for (String varn : doc.getVariantNames()) {
//                System.out.println("  VAR: " + varn);
//            }
//
//            for (String varn : VariantNameSorter.sortNames(doc.getVariantNames())) {
//                System.out.println("  VXR: " + varn);
//            }

        for (PlaceNameBridge nBridge : doc.getAllVariantNames()) {
            System.out.println("  VAR: " + nBridge.getNameId() + "|" + nBridge.getType().getTypeId() + "|" + nBridge.getName().getLocale() + "|" + nBridge.getName().get());
        }
        for (PlaceNameBridge nBridge : VariantNameSorter.sortNameBridges(doc.getAllVariantNames())) {
            System.out.println("  VXR: " + nBridge.getNameId() + "|" + nBridge.getType().getTypeId() + "|" + nBridge.getName().getLocale() + "|" + nBridge.getName().get());
        }
    }
}
