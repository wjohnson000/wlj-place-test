/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.util.*;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.appdata.AppDataManager;
import org.familysearch.standards.place.data.GroupBridge;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;

/**
 * Find Display names that are NOT variant names for a given place-rep.  This application relies
 * on the "db-dump" files generated by the db-to-solr load process.  Each subdirectory contains
 * files for about 1,000,000 place-reps.
 * 
 * @author wjohnson000
 *
 */
public class FindPlaceRepGroupStats {

    private static SolrConnection solrConn;
    private static SolrService    solrService;
    private static AppDataManager appDataMgr;

    public static void main(String...args) throws Exception {
        setupSolr();

        List<PlaceRepDoc> docs = getRepsWithGroup();
        System.out.println("\nDoc-Count: " + docs.size());

        docs.forEach(FindPlaceRepGroupStats::processDoc);
        shutdownSolr();
    }

    static void processDoc(PlaceRepDoc doc) {
        TypeBridge typeB = appDataMgr.getType(TypeBridge.TYPE.PLACE, doc.getPlaceTypeId());
        String typeInfo = (typeB == null) ? (doc.getPlaceTypeId() + "|unknown") : (typeB.getTypeId() + "|" + typeB.getCode());

        GroupBridge groupB = appDataMgr.getGroup(GroupBridge.TYPE.PLACE_TYPE, doc.getTypeGroup());
        String groupInfo = (groupB == null) ? (doc.getTypeGroup() + "|unknown") : (groupB.getGroupId() + "|" + groupB.getNames().getOrDefault("en", "no-name"));
        System.out.println("\n");
        System.out.println(doc.getRepId() + "|" + getName(doc) + "|" + doc.isDeleted() + "|" + doc.getDeleteId() + "|" + Arrays.toString(doc.getJurisdictionIdentifiers()) + "|" + typeInfo + "|" + groupInfo);

        Set<Integer> okTypeIds = groupB.getMembers();
        List<PlaceRepDoc> docs = getChildren(doc);
        for (PlaceRepDoc kid : docs) {
            typeB = appDataMgr.getType(TypeBridge.TYPE.PLACE, kid.getPlaceTypeId());
            typeInfo = (typeB == null) ? (doc.getPlaceTypeId() + "|unknown") : (typeB.getTypeId() + "|" + typeB.getCode());
            System.out.println(kid.getRepId() + "|" + getName(kid) + "|" + kid.isDeleted() + "|" + doc.getDeleteId() + "|" + Arrays.toString(kid.getJurisdictionIdentifiers()) + "|" + typeInfo + "|" + okTypeIds.contains(kid.getPlaceTypeId()));
        }
    }

    static void setupSolr() {
        solrService = SolrManager.awsBetaService(true);
        solrConn    = solrService.getReadConnection();
        appDataMgr = new AppDataManager(solrService, true);
    }

    static void shutdownSolr() {
        solrConn.shutdown();
        solrService.shutdown();
    }

    static List<PlaceRepDoc> getRepsWithGroup() {
        SolrQuery query = new SolrQuery("typeGroup:[1 TO *]");
        query.setSort("repId", SolrQuery.ORDER.asc);
        query.setRows(250);

        try {
            return solrConn.search(query);
        } catch (PlaceDataException e) {
            return Collections.emptyList();
        }
    }

    static List<PlaceRepDoc> getChildren(PlaceRepDoc doc) {
        SolrQuery query = new SolrQuery("parentId:" + doc.getRepId());
        query.setSort("repId", SolrQuery.ORDER.asc);
        query.setRows(250);

        try {
            return solrConn.search(query);
        } catch (PlaceDataException e) {
            return Collections.emptyList();
        }
    }

    static String getName(PlaceRepDoc doc) {
        String name = doc.getDisplayName("en");
        if (name == null) {
            name = doc.getDisplayName(doc.getPrefLocale());
        };
        if (name == null) {
            name = doc.getDisplayNames().stream().findFirst().orElse("Uknown");
        }

        return name;
    }
}
