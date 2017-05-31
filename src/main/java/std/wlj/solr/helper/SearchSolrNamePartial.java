package std.wlj.solr.helper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.PlaceDataException;

public class SearchSolrNamePartial {

    public static void main(String... args) throws PlaceDataException, IOException {
        SolrConnectionX solrConn = SolrConnectionX.connectToEmbeddedInstance("D:/solr/stand-alone-6.1.0");
        System.out.println("SOLR-conn: " + solrConn);

        List<String> names = Files.readAllLines(Paths.get("C:/temp/place-names-lots.txt"), Charset.forName("UTF-8"));

        int total = 0;
        long time0 = System.nanoTime();
        for (String name : names) {
            // Do a look-up by documents ...
            SolrQuery query = new SolrQuery("names:" + name);
            query.setSort("repId", SolrQuery.ORDER.desc);
            query.setRows(1111);

            List<int[]> ids = solrConn.searchLite(query);
            System.out.println(name + " >> CNT-0: " + ids.size());
            total += ids.size();

            for (int[] id : ids) {
                System.out.println("  Rep: " + id[0] + " .. " + id[1]);
            }
        }
        long time1 = System.nanoTime();

        System.out.println("NAMES: " + names.size());
        System.out.println("TOTAL: " + total);
        System.out.println(" TIME: " + (time1 - time0) / 1_000_000.0);

        System.exit(0);
    }
}
