package std.wlj.solr;

import java.util.Arrays;

import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrService;


public class SearchMasterByIdSolrService {

    public static void main(String... args) throws PlaceDataException {
//        String solrHome = "http://localhost:8983/solr/places";
        String solrHome = "http://familysearch.org/int-solr-repeater/places";

        System.setProperty("solr.solr.home", solrHome);
        System.setProperty("solr.master.url", solrHome);
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        System.setProperty("solr.skip.warmup", "true");

        SolrService solrService = new SolrService();

        PlaceRepDoc doc = solrService.findPlaceRep(1442484);
        if (doc == null) {
            System.out.println("Doc not found -- repId: " + 1442484);
        } else { 
            System.out.println("ID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()) + " --> " + doc.getRevision());
            System.out.println("  Place:  " + doc.getPlaceId());
            System.out.println("  F-Rev:  " + doc.getForwardRevision());
            System.out.println("  Par-Id: " + doc.getParentId());
            System.out.println("  D-Name: " + doc.getDisplayNameMap());
            System.out.println("  P-Rang: " + doc.getOwnerStartYear() + " - " + doc.getOwnerEndYear());
            System.out.println("  FromTo: " + doc.getFromYear() + " - " + doc.getToYear());
            System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
            for (String appData : doc.getVariantNames()) {
                System.out.println("  " + appData);
            }
            if (doc.getChildren() != null) {
                for (PlaceRepBridge prBridge : doc.getChildren()) {
                    System.out.println("Child:  " + prBridge.getRepId() + "." + prBridge.getRevision() + " --> " + Arrays.toString(prBridge.getJurisdictionIdentifiers()));
                }
            }
        }

        System.exit(0);
    }
}
