package std.wlj.solr;

import java.util.Arrays;

import org.familysearch.standards.core.Localized;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.PlaceRepBridge;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.util.SolrManager;


public class SearchMasterByIdSolrService {

    public static void main(String... args) throws PlaceDataException {
        int repId = 279;
        repId = 3252009;
        repId = 2373218;
//        repId = 6720850;
        SolrService solrService = SolrManager.awsProdService(true);

        PlaceRepDoc doc = solrService.findPlaceRepNoCache(repId);
        if (doc == null) {
            System.out.println("Doc not found -- repId: " + repId);
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

            doc.setDataService(solrService);
            PlaceRepresentation rep = new PlaceRepresentation(doc);
            Localized<String> name = rep.getFullDisplayName(StdLocale.KOREAN);
            System.out.println("  D-Name: " + name.get());
        }

        System.exit(0);
    }
}
