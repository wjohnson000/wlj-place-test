package std.wlj.local;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;


public class AddDocsToSolr {
    private static SolrConnection solrConn = null;

	public static void main(String... args) throws PlaceDataException {
        solrConn = SolrConnection.connectToRemoteInstance("http://place-solr.dev.fsglobal.org/solr/places");
//        solrConn = SolrConnection.connectToRemoteInstance("http://localhost:8080/solr/places");
//        solrConn = SolrConnection.connectToEmbeddedInstance("C:/Tools/solr/data/solr-places/solr");
//        System.setProperty("solr.master.url", "");
//        System.setProperty("solr.master", "false");
//        System.setProperty("solr.slave", "false");
//        solrConn = SolrConnection.connectToEmbeddedInstance("C:/Tools/solr/data/tokoro");

        int count = 100000;
        int groupCnt = 500;
        if (args.length > 0) {
            count = Integer.parseInt(args[0]);
        }
        if (args.length > 1) {
            groupCnt = Integer.parseInt(args[1]);
        }
        addDocs(count, groupCnt);

        solrConn.shutdown();
    }

	/**
	 * Add a specific number of documents to SOLR, grouped "groupCount" at a time.
	 *  
	 * @param count total number of documents to add
	 * @param groupCount number to send to SOLR [at a time]
	 */
	private static void addDocs(int count, int groupCount) throws PlaceDataException {
	    solrConn.delete( "*:*" );
	    List<PlaceRepDoc> docList = new ArrayList<>();

	    long tTime = 0L;
	    for (int i=1;  i<=count;  i++) {
	        docList.add(createDoc(i + "-xx"));
	        if (docList.size() % groupCount == 0) {
	            long then = System.nanoTime();
	            solrConn.add(docList);
	            long nnow = System.nanoTime();
	            docList.clear();
	            tTime += (nnow - then);
	            System.out.println(groupCount + " added -- time: " + ((nnow - then) / 1000000.0));
	        }

	        // Periodically commit, just for fun
	        if (i % 1000 == 0) {
	            solrConn.commit();
	        }
	    }

	    // Add final documents and commit ...
	    if (docList.size() > 0) {
            long then = System.nanoTime();
            solrConn.add(docList);
            long nnow = System.nanoTime();
            System.out.println(groupCount + " added -- time: " + ((nnow - then) / 1000000.0));
        }
        solrConn.commit();

	    System.out.println("\n" + count + " added -- time: " + ((tTime / 1000000.0)));
	}

	/**
	 * Create a silly place-rep-doc instance with the given identifier
	 * 
	 * @param id place-rep-doc identifer
	 * @return new place-rep-doc
	 */
	private static PlaceRepDoc createDoc(String id) {
        PlaceRepDoc prDoc = new PlaceRepDoc();

        prDoc.setId(id);
        prDoc.addAttribute(11, 21, 1900, "attr-fidgeting", "en");
        prDoc.addAttribute(12, 22, 1901, "attr-fighting", "en");
        prDoc.setCentroid("44.44, -55.55");
        prDoc.addCitation(111, 51, 101, new Date(System.currentTimeMillis()), "Gazeteer", "page 55");
        prDoc.addCitation(112, 52, 102, new Date(System.currentTimeMillis()), "Google", "http://maps.google.com/112");
        prDoc.addDisplayName("Wayne", "en");
        prDoc.addDisplayName("Enyaw", "ne");
        prDoc.setEndYear(null);
        prDoc.setOwnerEndYear(2012);
        prDoc.setOwnerId(55);
        prDoc.setOwnerStartYear(1901);
        prDoc.setParentId(-1);
        prDoc.setPrefLocale("en");
        prDoc.setPublished(1);
        prDoc.setRepId(11111111);
        prDoc.setRepIdChainAsInt(new int[] { 1111, 111, 11, 1});
        prDoc.setRevision(111);
        prDoc.setStartYear(1900);
        prDoc.setType(42);
        prDoc.setUUID("abcd-1234-efgh-5678");
        prDoc.setValidated(1);
        prDoc.addVariantName(1111, 11, "en", "wayne");
        prDoc.addVariantName(1112, 12, "en", "waynex");
        prDoc.addVariantName(1113, 13, "en", "waynexx");

        return prDoc;
	}
}
