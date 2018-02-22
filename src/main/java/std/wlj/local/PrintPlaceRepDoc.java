package std.wlj.local;

import java.util.Date;

import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;


public class PrintPlaceRepDoc {
	public static void main(String... args) throws PlaceDataException {
	    PlaceRepDoc prDoc = createDoc("123-456");
	    System.out.println("PR: " + prDoc);
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
        prDoc.addAttribute(11, 21, 1900, null, "attr-fidgeting", "en", null, null);
        prDoc.addAttribute(12, 22, 1901, null, "attr-fighting", "en", null, null);
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
