package std.wlj.solr;

import java.util.Arrays;

import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.util.SolrManager;


public class SearchMasterByIdSolrService {

    public static void main(String... args) throws PlaceDataException {
        SolrService solrService = SolrManager.awsProdService(true);

        PlaceRepDoc doc = solrService.findPlaceRepNoCache(6720850);
        if (doc == null) {
            System.out.println("Doc not found -- repId: " + 6720850);
        } else { 
            System.out.println("ID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()) + " --> " + doc.getRevision());
            System.out.println("  Place:  " + doc.getPlaceId());
            System.out.println("  Par-Id: " + doc.getParentId());
            System.out.println("  D-Name: " + doc.getDisplayNameMap());
            System.out.println("  P-Rang: " + doc.getOwnerStartYear() + " - " + doc.getOwnerEndYear());
            System.out.println("  FromTo: " + doc.getFromYear() + " - " + doc.getToYear());
            System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
            for (String appData : doc.getVariantNames()) {
                System.out.println("  " + appData);
            }
            for (PlaceRepBridge prB : doc.getResolvedJurisdictions()) {
                System.out.println("  Juris: " + prB.getRepId());
            }

//            if (doc.getChildren() != null) {
//                for (PlaceRepBridge prBridge : doc.getChildren()) {
//                    System.out.println("Child:  " + prBridge.getRepId() + "." + prBridge.getRevision() + " --> " + Arrays.toString(prBridge.getJurisdictionIdentifiers()));
//                }
//            }
        }

        System.exit(0);
    }
}
