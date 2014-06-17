package std.wlj.dbnew;

import java.io.PrintWriter;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.AppDataManager;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.util.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


public class Step04DumpSolr {
    public static void main(String... args) throws Exception {
        PrintWriter writer = FileUtils.getWriter("C:/temp/load-place-db/solr-content-04-after.txt");
        SolrConnection solrConn = SolrConnection.connectToEmbeddedInstance("C:/tools/Solr/data/");
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        int skipCnt = 0;
        while (true) {
            SolrQuery query = new SolrQuery("id:*");
            query.setStart(skipCnt);
            query.setRows(100);
            query.addSort("repId", SolrQuery.ORDER.asc);
            query.addSort("revision", SolrQuery.ORDER.asc);
            query.addSort("id", SolrQuery.ORDER.asc);

            List<PlaceRepDoc> docs = solrConn.search(query);
            if (docs.size() == 0) {
                break;
            }

            for (PlaceRepDoc doc : docs) {
                System.out.println(doc.getId() + " --> " + doc.getRepId() + "." + doc.getRevision());
                if (! AppDataManager.isAppDataDoc(doc)) {
                    String json = gson.toJson(doc);
                    writer.println(json);
                }
            }
            skipCnt += 100;
        }

        solrConn.shutdown();
        writer.close();
        System.exit(0);
    }
}
