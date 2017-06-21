package std.wlj.solrload;

import java.io.File;
import java.util.Iterator;

import org.familysearch.standards.loader.helper.PlaceRepDocReader;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;

public class TestPlaceRepReader {
	public static void main(String... args) {
		File parentDir = new File("D:/tmp/flat-files/one-ten-thousand");
		PlaceRepDocReader prReader = new PlaceRepDocReader(parentDir);

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
