package std.wlj.solr.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.solr.helper.SolrConnectionX;

public class CollectNames {

    public static void main(String... args) throws PlaceDataException {
        Random random = new Random();
        SolrConnectionX solrConn = SolrConnectionX.connectToEmbeddedInstance("D:/solr/newbie-6.1.0");
        System.out.println("SOLR-conn: " + solrConn);

        List<String> names = new ArrayList<>();
        for (int repid=1;  repid<=11_111_111;  repid+=111) {
            SolrQuery query = new SolrQuery("repId:" + repid);
            query.setRows(1);
            
            List<PlaceRepDoc> docs = solrConn.search(query);
            if (! docs.isEmpty()) {
                List<String> varNames = docs.get(0).getVariantNames();
                String varName = varNames.get(random.nextInt(varNames.size()));
                int ndx = varName.lastIndexOf('|');
                names.add(varName.substring(ndx+1));
            }
        }

        System.out.println("NAMES: " + names.size());
        names.forEach(System.out::println);

        System.exit(0);
    }
}
