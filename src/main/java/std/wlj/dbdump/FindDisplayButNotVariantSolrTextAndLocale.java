/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.util.PlaceHelper;

import std.wlj.util.SolrManager;

/**
 * Find Display names that are NOT variant names for a given place-rep.  This application relies
 * on reading directly from Solr -- each documents contains its display names and the variants
 * from the associated place.
 * 
 * @author wjohnson000
 *
 */
public class FindDisplayButNotVariantSolrTextAndLocale {

    private static SolrConnection solrConn;
    private static List<String> results = new ArrayList<>(100_000);

    public static void main(String...args) throws Exception {
//        solrConn = SolrManager.localEmbeddedConnection("C:/D-drive/solr/standalone-7.7.1");
        solrConn = SolrManager.awsIntConnection(false);

        List<Integer> repIds = new ArrayList<>();
        for (int repId=1;  repId<10_999_999;  repId++) {
            repIds.add(repId);
            if (repIds.size() > 5000) {
                processReps(repIds);
                repIds.clear();
            }
        }

        solrConn.shutdown();
        results.forEach(System.out::println);
        Files.write(Paths.get("C:/temp/display-no-variant.txt"), results, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.exit(0);
    }

    static void processReps(List<Integer> repIds) throws PlaceDataException {
        String query = "repId:[" + repIds.get(0) + " TO " + repIds.get(repIds.size()-1) +"]";

        SolrQuery solrQuery = new SolrQuery(query);
        solrQuery.setSort("repId", SolrQuery.ORDER.asc);
        solrQuery.setRows(repIds.size()+10);

        List<PlaceRepDoc> docs = solrConn.search(solrQuery);
        System.out.println("Process: " + repIds.get(0) + " --> " + query + " --> " + docs.size());
        docs.stream()
            .filter(doc -> ! doc.isDeleted())
            .forEach(doc -> processDoc(doc));
    }

    static void processDoc(PlaceRepDoc repDoc) {
        Set<String> dNames = new TreeSet<>();
        Set<String> vNames = new TreeSet<>();

        for (String dName : repDoc.getDisplayNames()) {
            dNames.add(dName.toLowerCase());
        }

        for (String vName : repDoc.getVariantNames()) {
            String[] chunks = PlaceHelper.split(vName, '|');
            vNames.add(chunks[2].toLowerCase() + "|" + chunks[3].toLowerCase());
        }

        dNames.removeAll(vNames);
        if (! dNames.isEmpty()) {
            results.add("");
            results.add(repDoc.getRepId() + "|" + Arrays.toString(repDoc.getJurisdictionIdentifiers()));
            repDoc.getVariantNames().stream()
                .map(vName -> PlaceHelper.split(vName, '|'))
                .map(chunks -> chunks[2] + "|" + chunks[3])
                .forEach(vName -> results.add(repDoc.getRepId() + "|vrnt|" + vName));
            repDoc.getDisplayNames().stream()
                .map(dname -> (dname + (vNames.contains(dname.toLowerCase()) ? "|" : "|miss")))
                .forEach(dName -> results.add(repDoc.getRepId() + "|disp|" + dName));
        }
    }
}
