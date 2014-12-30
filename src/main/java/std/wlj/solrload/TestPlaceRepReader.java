package std.wlj.solrload;

import java.io.File;
import java.util.Iterator;

import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.solr.load.PlaceRepReader;

public class TestPlaceRepReader {
	public static void main(String... args) {
		File parentDir = new File("C:/temp/place-extract/wlj-one");
		PlaceRepReader prReader = new PlaceRepReader(parentDir);

		int docCount = 0;
		Iterator<PlaceRepDoc> iter = prReader.iterator();

		long then = System.nanoTime();
        while (iter.hasNext()) {
            docCount++;
            PlaceRepDoc prDoc = iter.next();
            System.out.println(prDoc.getId() + " . " + prDoc.getRepId() + " . " + prDoc.getOwnerId());
        }
        long nnow = System.nanoTime();
        System.out.println("DocCount: " + docCount);
        System.out.println("Time: " + ((nnow - then) / 1000000.0));
	}
}
