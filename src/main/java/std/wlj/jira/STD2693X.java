package std.wlj.jira;

import java.util.Arrays;

import org.familysearch.standards.place.appdata.AppDataManager;
import org.familysearch.standards.place.data.PlaceNameBridge;
import org.familysearch.standards.place.data.VariantNameSorter;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.util.SolrManager;


public class STD2693X {
    public static void main(String... args) throws Exception {
        SolrService  solrService = SolrManager.awsProdService(true);
        AppDataManager appDataManager = new AppDataManager(solrService);
        VariantNameSorter.initMap(appDataManager);

        PlaceRepDoc doc = solrService.findPlaceRep(266);

        System.out.println("ID: " + doc.getId() + " --> " + doc.getRevision() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()));
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
