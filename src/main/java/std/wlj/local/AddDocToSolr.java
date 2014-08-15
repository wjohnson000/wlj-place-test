package std.wlj.local;

import java.util.Date;

import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;


public class AddDocToSolr {
	public static void main(String... args) throws PlaceDataException {
//      SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://place-solr.dev.fsglobal.org/solr/places");
      SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://localhost:8080/solr/places");
//      SolrConnection solrConn = SolrConnection.connectToEmbeddedInstance("C:/Tools/solr/data/solr-places/solr");
      

      PlaceRepDoc prDoc = new PlaceRepDoc();
      prDoc.addAttribute(11, 21, 1900, "attr-fidgeting", "en");
      prDoc.addAttribute(12, 22, 1901, "attr-fighting", "en");
      prDoc.setCentroid("44.44, -55.55");
      prDoc.addCitation(111, 51, 101, new Date(System.currentTimeMillis()), "Gazeteer", "page 55");
      prDoc.addCitation(112, 52, 102, new Date(System.currentTimeMillis()), "Google", "http://maps.google.com/112");
      prDoc.addDisplayName("Wayne", "en");
      prDoc.addDisplayName("Enyaw", "ne");
      prDoc.setEndYear(null);
      prDoc.setId("11111111-3");
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

      solrConn.addAndCommit(prDoc);

      solrConn.shutdown();
	}
}
