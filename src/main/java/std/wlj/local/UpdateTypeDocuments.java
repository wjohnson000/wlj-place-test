package std.wlj.local;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.AppDataManager;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.util.PlaceHelper;


public class UpdateTypeDocuments {
    public static void main(String... args) throws Exception {
        SolrConnection solrConn = SolrConnection.connectToRemoteInstance("http://localhost:8983/solr/places");
//        SolrConnection solrConn = SolrConnection.connectToEmbeddedInstance("C:/tools/Solr/data");

        String[] appDocIds = { AppDataManager.ATTR_TYPE_ID, AppDataManager.NAME_TYPE_ID };
        for (String appDocId : appDocIds) {
            SolrQuery query = new SolrQuery("id:" + appDocId);
            List<PlaceRepDoc> docs = solrConn.search(query);
            for (PlaceRepDoc doc : docs) {
                List<String> appData = updateAppData(doc, -8);
                PlaceRepDoc newDoc = AppDataManager.makeAppDataDoc(appDocId, appData);
                solrConn.add(newDoc);
            }
        }

        solrConn.commit();
        solrConn.shutdown();
    }

    /**
     * The ID is the first field of each "appData" entry.  Increment or decrement the id
     * accordingly ...
     * 
     * @param doc PlaceRepDoc to update
     * @param idDelta value by which the ID is to be changed
     * @return new List of data
     */
    private static List<String> updateAppData(PlaceRepDoc doc, int idDelta) {
        List<String> appData = new ArrayList<String>();

        for (String aData : doc.getAppData()) {
            String[] tokens = PlaceHelper.split(aData, '|');
            int newId = Integer.parseInt(tokens[0]) + idDelta;
            tokens[0] = String.valueOf(newId);
            StringBuilder buff = new StringBuilder(64);
            for (String token : tokens) {
                if (buff.length() > 0) buff.append('|');
                buff.append(token);
            }
            appData.add(buff.toString());
        }

        return appData;
    }
}
