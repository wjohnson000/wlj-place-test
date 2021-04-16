package std.wlj.solr;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
//import java.util.TreeSet;
//import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.dbdump.DumpTypes;
import std.wlj.util.SolrManager;


public class SearchRepWithNoCitation {

    static Set<Integer> parentIds = new HashSet<>();
    static {
        parentIds.add(389196);
        parentIds.add(389198);
        parentIds.add(389561);
        parentIds.add(389596);
        parentIds.add(389606);
        parentIds.add(389752);
        parentIds.add(389970);
        parentIds.add(390228);
        parentIds.add(391968);
        parentIds.add(3472932);
        parentIds.add(3473031);
        parentIds.add(3473072);
        parentIds.add(3480965);
        parentIds.add(3480967);
        parentIds.add(3481367);
        parentIds.add(3481402);
        parentIds.add(3481412);
        parentIds.add(3481474);
        parentIds.add(3481696);
        parentIds.add(3481919);
        parentIds.add(3482053);
        parentIds.add(3482273);
        parentIds.add(3482456);
        parentIds.add(3483227);
        parentIds.add(3483228);
        parentIds.add(3483229);
        parentIds.add(3483234);
        parentIds.add(3483237);
        parentIds.add(3483243);
        parentIds.add(3483277);
        parentIds.add(3485605);
        parentIds.add(3492358);
        parentIds.add(3495980);
        parentIds.add(3495992);
        parentIds.add(3496025);
        parentIds.add(8173516);
        parentIds.add(8173667);
        parentIds.add(8173668);
        parentIds.add(8175334);
        parentIds.add(8180110);
        parentIds.add(8180918);
        parentIds.add(8185644);
        parentIds.add(8190898);
        parentIds.add(8191703);
        parentIds.add(8191746);
        parentIds.add(8191891);
        parentIds.add(9548453);
        parentIds.add(10266087);
        parentIds.add(10267497);
        parentIds.add(10267503);
        parentIds.add(10267506);
        parentIds.add(10267514);
        parentIds.add(10267516);
        parentIds.add(10267517);
        parentIds.add(10267534);
        parentIds.add(10267541);
        parentIds.add(10267701);
        parentIds.add(10267811);
        parentIds.add(10268451);
        parentIds.add(10268453);
        parentIds.add(10268515);
        parentIds.add(10268569);
        parentIds.add(10268586);
        parentIds.add(10269201);
        parentIds.add(10269223);
        parentIds.add(10269246);
        parentIds.add(10269271);
        parentIds.add(10269275);
        parentIds.add(10269311);
        parentIds.add(10269314);
        parentIds.add(10269315);
        parentIds.add(10269316);
        parentIds.add(10269318);
        parentIds.add(10269321);
        parentIds.add(10269328);
        parentIds.add(10269527);
        parentIds.add(10269528);
        parentIds.add(10269529);
        parentIds.add(10278968);
        parentIds.add(10279533);
        parentIds.add(10282717);
        parentIds.add(10293909);
        parentIds.add(10305761);
        parentIds.add(10306263);
        parentIds.add(10306911);
        parentIds.add(10306942);
        parentIds.add(10307170);
        parentIds.add(10307180);
        parentIds.add(10307182);
        parentIds.add(10307186);
        parentIds.add(10307233);
        parentIds.add(10307249);
        parentIds.add(10307837);
        parentIds.add(10554657);
        parentIds.add(10554658);
        parentIds.add(10557685);
        parentIds.add(10557953);
        parentIds.add(10564460);
        parentIds.add(10623565);
        parentIds.add(10635382);
        parentIds.add(10707865);
        parentIds.add(10707882);
        parentIds.add(10707924);
        parentIds.add(10709114);
        parentIds.add(10731985);
        parentIds.add(10731989);
        parentIds.add(10734721);
    }
    
    public static void main(String... args) throws PlaceDataException, IOException {
        Map<String, String> typeDetail = DumpTypes.loadPlaceTypes();

        SolrConnection solrConn = SolrManager.awsProdConnection(false);
        SolrQuery query = new SolrQuery("published:1 AND !citSourceId:[* TO *] AND !deleteId:[* TO *]");
        query.setRows(21_000);
        query.setSort("repId", SolrQuery.ORDER.asc);
        System.out.println("QRY: " + query);

        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("CNT: " + docs.size());

        Set<Integer> repIds  = new HashSet<>();
        List<String> details = new ArrayList<>();
        for (PlaceRepDoc doc : docs) {
            repIds.add(doc.getRepId());
            String type = typeDetail.get(String.valueOf(doc.getType()));
            String name = doc.getDisplayName("en");
            if (name == null  ||  name.isEmpty()) {
                name = doc.getDisplayNameMap().entrySet().stream().filter(entry -> entry.getKey().contains("Latn")).map(entry -> entry.getValue()).findFirst().orElse(null);
            }
            if (name == null  ||  name.isEmpty()) {
                name = doc.getDisplayNameMap().values().stream().findFirst().orElse("Unknown");
            }
            details.add(doc.getRepId() + "|" +doc.isPublished() + "|" + name + "|" + type + "|" + parentIds.contains(doc.getRepId()));
        }

//        Set<Integer> parents = getParents(solrConn, repIds);
//        parents.forEach(System.out::println);

        Files.write(Paths.get("C:/temp/rep-no-citation-solr.txt"), details, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("CNT: " + docs.size());

        solrConn.shutdown();
        System.exit(0);
    }

//    static Set<Integer> getParents(SolrConnection solrConn, Set<Integer> candidates) throws PlaceDataException {
//        Set<Integer> parents = new TreeSet<>();
//
//        List<Integer> cList = new ArrayList<>(candidates);
//        for (int ndx=0;  ndx<cList.size();  ndx+=50) {
//            System.out.println("Rows " + ndx + " of " + candidates.size());
//            List<Integer> repIds = cList.subList(ndx, ndx+49);
//            String query = repIds.stream().map(id -> String.valueOf(id)).collect(Collectors.joining(" ", "parentId:(", ")"));
//            SolrQuery sQuery = new SolrQuery(query);
//            sQuery.setRows(1_000);
//            List<PlaceRepDoc> docs = solrConn.search(sQuery);
//            for (PlaceRepDoc doc : docs) {
//                parents.add(doc.getParentId());
//            }
//        }
//
//        return parents;
//    }
}
