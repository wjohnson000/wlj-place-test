package std.wlj.local;

import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.appdata.AppDataManager;
import org.familysearch.standards.place.appdata.AppDataTypeMapper;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.SolrManager;

public class UpdateXrefDoc {
    public static void main(String... args) throws Exception {
        SolrConnection solrConn = SolrManager.localHttpConnection();

        String[] appDocIds = { AppDataTypeMapper.EXT_XREF_TYPE_ID };
        for (String appDocId : appDocIds) {
            System.out.println("SEARCH --> id:" + appDocId);
            SolrQuery query = new SolrQuery("id:" + appDocId);
            List<PlaceRepDoc> docs = solrConn.search(query);
            for (PlaceRepDoc doc : docs) {
                List<String> appData = doc.getAppData();
                appData.remove(4);
                PlaceRepDoc newDoc = AppDataManager.makeAppDataDoc(appDocId, appData);
                solrConn.add(newDoc);
            }
        }

        solrConn.commit();
        solrConn.shutdown();
        System.exit(0);
    }

}
