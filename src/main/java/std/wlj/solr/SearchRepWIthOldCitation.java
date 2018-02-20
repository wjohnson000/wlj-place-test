package std.wlj.solr;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.dbdump.DumpTypes;
import std.wlj.util.SolrManager;


public class SearchRepWIthOldCitation {

    public static void main(String... args) throws Exception {
        Map<String, String> types = DumpTypes.loadPlaceTypes();
        SolrConnection solrConn = SolrManager.awsProdConnection(true);

        SolrQuery query = new SolrQuery("citSourceId:[1 TO 1473]");
        query.setRows(150_000);
        System.out.println("QRY: " + query);

        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("CNT: " + docs.size());

        List<String> citnData = new ArrayList<>(200_000);
        for (PlaceRepDoc doc : docs) {
            for (String citn : doc.getCitations()) {
                
                StringBuilder buff = new StringBuilder();
                buff.append(doc.getRepId());
                buff.append("|").append(doc.getPrefLocale());
                buff.append("|").append(doc.getType());
                buff.append("|").append(types.get(String.valueOf(doc.getType())));
                buff.append("|").append(citn);
                citnData.add(buff.toString());
            }
            citnData.add("");
        }

        Files.write(Paths.get("C:/temp/reps-with-old-citations.txt"), citnData, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        System.exit(0);
    }
}
