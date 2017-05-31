package std.wlj.solr;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.appdata.AppDataManager;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.data.solr.SolrSearchResults;

import std.wlj.util.SolrManager;


public class SearchLocalFullResults {

    public static void main(String... args) throws PlaceDataException {
//      SolrConnection solrConn = SolrManager.localHttpConnection();
      SolrConnection solrConn = SolrManager.localEmbeddedConnection("D:/solr/stand-alone-6.1.0");

      // Do a look-up by documents ...
//      SolrQuery query = new SolrQuery("repId:111");
//      SolrQuery query = new SolrQuery("id:*World*");
//      query.addField("lastUpdateDate:[NOW-1YEAR/DAY TO NOW/DAY+1DAY]");  // &NOW=" + System.currentTimeMillis());
      SolrQuery query = new SolrQuery("lastUpdateDate:[NOW-21DAY TO NOW]");
      query.setSort("repId", SolrQuery.ORDER.asc);
      query.setRows(16);
//      query.addFilterQuery("-deleteId:[* TO *]");
      System.out.println("QQ: " + query);
      SolrSearchResults results = solrConn.search(query, null);

      System.out.println("TOT.cnt: " + results.getFoundCount());
      System.out.println("RET.cnt: " + results.getReturnedCount());
      for (PlaceRepDoc doc : results.getResults()) {
          if (AppDataManager.isAppDataDoc(doc)) {
              Set<String> foundIds = new HashSet<>();
              System.out.println("ID: " + doc.getId() + " --> " + doc.getType() + " --> " + doc.getRevision());
              for (String appData : doc.getAppData()) {
                  int ndx = appData.indexOf('|');
                  if (ndx > 0) {
                      String sId = appData.substring(0, ndx);
                      if (! foundIds.contains(sId)) {
                          foundIds.add(sId);
                          System.out.println("  " + appData);
                      }
                  }
              }
              continue;
          }

          System.out.println("ID: " + doc.getId() + " --> " + doc.getType() + " --> " + Arrays.toString(doc.getJurisdictionIdentifiers()) + " --> " + doc.getRevision());
          System.out.println("  Place:  " + doc.getPlaceId());
          System.out.println("  D-Name: " + doc.getDisplayNameMap());
          System.out.println("  P-Name: " + doc.getNames());
          System.out.println("  Del-Id: " + doc.getDeleteId() + " . " + doc.getPlaceDeleteId());
          System.out.println("  Dates:  " + doc.getCreateDate() + " . " + doc.getLastUpdateDate());
          System.out.println("  Chain:  " + Arrays.toString(doc.getRepIdChainAsInt()));
          
          for (String extXref : doc.getExtXrefs()) {
              System.out.println("  Ext-Xref: " + extXref);
          }
          
          for (String altJuris : doc.getAltJurisdictions()) {
              System.out.println("  Alt-Juris: " + altJuris);
          }
          
          for (String citn : doc.getCitations()) {
              System.out.println("  Citn: " + citn);
          }
      }

      System.exit(0);
  }
}
