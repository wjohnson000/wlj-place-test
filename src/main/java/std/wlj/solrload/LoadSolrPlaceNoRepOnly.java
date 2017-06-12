package std.wlj.solrload;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.familysearch.standards.core.logging.Logger;
import org.familysearch.standards.loader.helper.PlaceNoRepDocGenerator;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;

public class LoadSolrPlaceNoRepOnly {
    public static void main(String...args) throws PlaceDataException {
        String solrLocal = "D:/solr/newbie-6.1.0";
        SolrConnection solrConn = SolrManager.localEmbeddedConnection(solrLocal);

        File parentDir = new File("D:/tmp/flat-files/one-infinity");
        PlaceNoRepDocGenerator prReader = new PlaceNoRepDocGenerator(parentDir);

        Logger logger = new Logger(PlaceNoRepDocGenerator.class);
        System.out.println("Debug? " + logger.isDebugEnabled());

        int docCount = 0;
        long then = System.nanoTime();
        List<PlaceRepDoc> docList = new ArrayList<>();
        Iterator<PlaceRepDoc> iter = prReader.iterator();
        while (iter.hasNext()) {
            docCount++;
            PlaceRepDoc prDoc = iter.next();
            docList.add(prDoc);
            if (docCount % 1000 == 0) {
                solrConn.add(docList);
                docList.clear();
            }
        }
        solrConn.add(docList);
        solrConn.commit();

        long nnow = System.nanoTime();
        System.out.println("DocCount: " + docCount);
        System.out.println("Time: " + ((nnow - then) / 1000000.0));
    }
}
