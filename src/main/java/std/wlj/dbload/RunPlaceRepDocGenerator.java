package std.wlj.dbload;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;

import org.familysearch.standards.loader.helper.PlaceRepDocReader;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;

public class RunPlaceRepDocGenerator {

    public static void main(String...args) {
        File baseDir = new File("C:/temp/flat-file/yyy");
        PlaceRepDocReader docGen = new PlaceRepDocReader(baseDir);
        Iterator<PlaceRepDoc> prIter = docGen.iterator();
        while (prIter.hasNext()) {
            PlaceRepDoc prDoc = prIter.next();
            System.out.println("PRD: " + prDoc);
            if (prDoc.getRepId() > 0) {
                System.out.println("=========================================================================");
                System.out.println("ID: " + prDoc.getId() + " --> " + prDoc.getType() + " --> " + Arrays.toString(prDoc.getJurisdictionIdentifiers()) + " --> " + prDoc.getRevision());
                System.out.println("  Place:  " + prDoc.getPlaceId());
                System.out.println("  Parent: " + prDoc.getParentId());
                System.out.println("  D-Name: " + prDoc.getDisplayNameMap());
                System.out.println("  P-Name: " + prDoc.getNames());
                System.out.println("  P-Rang: " + prDoc.getOwnerStartYear() + " - " + prDoc.getOwnerEndYear());
                System.out.println("  Del-Id: " + prDoc.getDeleteId() + " . " + prDoc.getPlaceDeleteId());
                System.out.println("  Creatd: " + prDoc.getCreateUser() + " . " + prDoc.getCreateDate());
                System.out.println("  Updatd: " + prDoc.getLastUpdateUser() + " . " + prDoc.getLastUpdateDate());
                for (String attrs : prDoc.getAttributes()) {
                    System.out.println("  AT: " + attrs);
                }
                for (String citns : prDoc.getCitations()) {
                    System.out.println("  CT: " + citns);
                }
                for (String extXrefs : prDoc.getExtXrefs()) {
                    System.out.println("  EX: " + extXrefs);
                }
                for (String altJuris : prDoc.getAltJurisdictions()) {
                    System.out.println("  AJ: " + altJuris);
                }
                System.out.println("=========================================================================");
            }
        }
    }
}
