package std.wlj.interpretation;

import java.io.IOException;
import java.util.Arrays;

import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.util.SolrManager;

/**
 * Run an interpretation through the Place 2.0 engine.
 * 
 * @author wjohnson000
 *
 */
public class CheckForMissingReps {

    private static SolrConnection solrConn;

    public static void main(String... args) throws PlaceDataException, IOException {
        SolrService  solrService = SolrManager.localEmbeddedService("C:/D-drive/solr/standalone-7.7.1");
        solrConn   = solrService.getReadConnection();

        int count = 0;
        for (int repId=1;  repId<11_444_555; repId+=23) {
            if (++count % 1000 == 0) System.out.println("... " + count);
            PlaceRepDoc repDoc = solrConn.getById(String.valueOf(repId));
            if (repDoc != null) {
                int missingId = checkDoc(repDoc);
                if (missingId > 0) {
                    printRep(repDoc);
                    System.out.println("  " + missingId);
                }
            }
        }

        solrService.shutdown();
        System.exit(0);
    }

    static void printRep(PlaceRepDoc repDoc) {
        System.out.println("\nREP:" + repDoc.getId() + " --> " + repDoc.getRepId() + " . " + repDoc.getDeleteId() + " . " + Arrays.toString(repDoc.getJurisdictionIdentifiers()));
    }

    static int checkDoc(PlaceRepDoc repDoc) {
        int missingId = 0;
        for (int repId : repDoc.getJurisdictionIdentifiers()) {
            PlaceRepDoc jDoc = solrConn.getById(String.valueOf(repId));
            if (jDoc == null) {
                missingId = repId;
                break;
            }
        }

        return missingId;
    }
}
