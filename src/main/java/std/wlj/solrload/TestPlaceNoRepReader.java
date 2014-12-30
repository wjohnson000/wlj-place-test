package std.wlj.solrload;

import java.io.File;
import java.util.Iterator;

import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.solr.load.PlaceNoRepReader;

public class TestPlaceNoRepReader {
	public static void main(String... args) {
		File parentDir = new File("C:/temp/place-extract/wlj-one");
		PlaceNoRepReader prReader = new PlaceNoRepReader(parentDir);

		int docCount = 0;
		Iterator<PlaceRepDoc> iter = prReader.iterator();

		long then = System.nanoTime();
        while (iter.hasNext()) {
            docCount++;
            PlaceRepDoc prDoc = iter.next();
            if (prDoc.getVariantNames().size() == 0) {
                System.out.println("No Variants!!  " + prDoc.getId() + " . " + prDoc.getOwnerId() + " . " + prDoc.getPlaceDeleteId() + " . " + prDoc.getVariantNames().size());
            } else if (prDoc.getPlaceDeleteId() == null  ||  prDoc.getPlaceDeleteId().intValue() == 0) {
                System.out.println("No Delete-Id!!  " + prDoc.getId() + " . " + prDoc.getOwnerId() + " . " + prDoc.getPlaceDeleteId() + " . " + prDoc.getVariantNames().size());
            } else if (prDoc.getDeleteId() == null  ||  prDoc.getDeleteId().intValue() <= 0) {
                System.out.println("No Rep-Delete-Id!!  " + prDoc.getId() + " . " + prDoc.getOwnerId() + " . " + prDoc.getPlaceDeleteId() + " . " + prDoc.getVariantNames().size());
            }
        }

        long nnow = System.nanoTime();
        System.out.println("DocCount: " + docCount);
        System.out.println("Time: " + ((nnow - then) / 1000000.0));
	}
}
