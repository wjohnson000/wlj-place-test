package std.wlj.dbload;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.db.loader.helper.PlaceRepDocGenerator;

public class RunPlaceRepDocGenerator {

    public static void main(String...args) {
        File baseDir = new File("D:/tmp/flat-files/eight-million");
        PlaceRepDocGenerator docGen = new PlaceRepDocGenerator(baseDir);
        Iterator<PlaceRepDoc> prIter = docGen.iterator();
        while (prIter.hasNext()) {
            PlaceRepDoc prDoc = prIter.next();
            System.out.println("PRD: " + prDoc);
            if (prDoc.getRepId() == 374148) {
                System.out.println("=========================================================================");
                System.out.println("ID: " + prDoc.getId() + " --> " + prDoc.getType() + " --> " + Arrays.toString(prDoc.getJurisdictionIdentifiers()) + " --> " + prDoc.getRevision());
                System.out.println("  Place:  " + prDoc.getPlaceId());
                System.out.println("  Parent: " + prDoc.getParentId());
                System.out.println("  D-Name: " + prDoc.getDisplayNameMap());
                System.out.println("  P-Name: " + prDoc.getNames());
                System.out.println("  P-Rang: " + prDoc.getOwnerStartYear() + " - " + prDoc.getOwnerEndYear());
                System.out.println("  Del-Id: " + prDoc.getDeleteId() + " . " + prDoc.getPlaceDeleteId());
                System.out.println("  Dates:  " + prDoc.getCreateDate() + " . " + prDoc.getLastUpdateDate());
                for (String altJuris : prDoc.getAltJurisdictions()) {
                    System.out.println("  AJ: " + altJuris);
                }
                for (String appData : prDoc.getAppData()) {
                    System.out.println("  " + appData);
                }
                System.out.println("=========================================================================");
            }
        }
    }
}
