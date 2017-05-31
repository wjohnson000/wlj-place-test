package std.wlj.solr;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.appdata.AppDataManager;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.data.solr.SolrSearchResults;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.util.SolrManager;

public class SearchLocal {

    public static void main(String... args) throws PlaceDataException {
      SolrConnection solrConn = SolrManager.localEmbeddedConnection("D:/solr/stand-alone-6.5.0");
//      SolrConnection solrConn = SolrManager.localEmbeddedConnection("D:/solr/stand-alone-6.1.0");
//      SolrConnection solrConn = SolrManager.awsBetaConnection(true);

      // Do a look-up by documents ...
      SolrQuery query = new SolrQuery("repId:337");
//      SolrQuery query = new SolrQuery("id:*World*");
//      query.addField("lastUpdateDate:[NOW-1YEAR/DAY TO NOW/DAY+1DAY]");  // &NOW=" + System.currentTimeMillis());
//      SolrQuery query = new SolrQuery("lastUpdateDate:[NOW-7DAY TO NOW]");
      query.setSort("repId", SolrQuery.ORDER.asc);
      query.setRows(32);
//      query.addFilterQuery("-deleteId:[* TO *]");
      System.out.println("QQ: " + query);
      List<PlaceRepDoc> docs = solrConn.search(query);

      System.out.println("CNT: " + docs.size());
      for (PlaceRepDoc doc : docs) {
          if (AppDataManager.isAppDataDoc(doc)) {
              Set<String> foundIds = new HashSet<>();
              System.out.println("ID: " + doc.getId() + " --> " + doc.getType() + " --> " + doc.getRevision());
              System.out.println("  Dates:  " + doc.getCreateDate() + " . " + doc.getLastUpdateDate());
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
          System.out.println("  Bdy-Id:  " + doc.getPreferredBoundaryId());
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
