package std.wlj.kml.newberry;

import java.sql.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.appdata.AppDataTypeMapper;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.TypeBridge;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;

import std.wlj.datasource.DbConnectionManager;
import std.wlj.util.SolrManager;

/**
 * Pull place-rep data for US states and counties from Solr, saving the following fields:
 * <ul>
 *   <li>hierarchy level (0=root place-rep)</li>
 *   <li>rep-id</li>
 *   <li>Display name (en)</li>
 *   <li>Place type</li>
 *   <li>Start year</li>
 *   <li>End year</li>
 *   <li>latitude</li>
 *   <li>longitude</li>
 * </ul>
 * 
 * @author wjohnson000
 *
 */
public class Analyze02_SolrUSAHierarchy {

    static class Type02X {
        int    typeId;
        String code;
        String term;
    }

    static class PlaceRep02X {
        int    repId;
        int    parentId;
        int    typeId;
        int    fromYear;
        int    toYear;
        int    deleteId;
        double centerLong;
        double centerLattd;
        String name;

        @Override public String toString() {
            return name;
        }
    }

    static SolrConnection solrConn;

    static Map<Integer, Type02X> typeMap = new HashMap<>();
    static Map<Integer, PlaceRep02X> repMap = new HashMap<>();

    public static void main(String...args) {
        solrConn = SolrManager.awsProdConnection(false);

        try(Connection conn=DbConnectionManager.getConnectionAws()) {
            populateTypes(conn);

            getReps("repId", Arrays.asList(1));
            getReps("parentId", Arrays.asList(1));

            List<Integer> parentIds = repMap.values().stream()
                    .map(rep -> rep.repId)
                    .collect(Collectors.toList());
            getReps("parentId", parentIds);

            List<Integer> deleteIds = repMap.values().stream()
                    .filter(rep -> rep.deleteId > 1)
                    .map(rep -> rep.deleteId)
                    .collect(Collectors.toList());
            getReps("repId", deleteIds);

        } catch (SQLException | PlaceDataException ex) {
            System.out.println("SQL-EX: " + ex);
        }

        dumpTree();
    }

    static void dumpTree() {
        dumpTree(0, 1);
    }

    static void dumpTree(int level, int repId) {
        PlaceRep02X repX = repMap.get(repId);
        if (repX == null) {
            System.out.println("OUCH!! " + repId);
            return;
        }

        Type02X typeX = typeMap.get(repX.typeId);

        StringBuilder buff = new StringBuilder();
        buff.append(level);
        buff.append("|").append(repX.repId);
        buff.append("|").append(repX.name);
        buff.append("|").append(typeX.term);
        buff.append("|").append(repX.fromYear);
        buff.append("|").append(repX.toYear);
        buff.append("|").append(repX.centerLattd);
        buff.append("|").append(repX.centerLong);

        System.out.println(buff.toString());

        // Process the kidlets
        List<PlaceRep02X> kids = repMap.values().stream()
            .filter(rep -> rep.parentId == repId)
            .collect(Collectors.toList());
        kids.sort(Comparator.comparing(PlaceRep02X::toString));
        kids.forEach(kid -> dumpTree(level+1, kid.repId));
    }

    static void populateTypes(Connection conn) throws PlaceDataException {
        SolrQuery query = new SolrQuery("id:PLACE-TYPE");
        query.setRows(1);
        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("CNT: " + docs.size());
        if (! docs.isEmpty()) {
            Set<TypeBridge> placeTypes = new AppDataTypeMapper().parseTypes(TypeBridge.TYPE.PLACE, docs.get(0), null);
            for (TypeBridge typeB : placeTypes) {
                Type02X type = new Type02X();
                type.typeId = typeB.getTypeId();
                type.code   = typeB.getCode();
                type.term   = typeB.getNames().get("en");
                typeMap.put(type.typeId, type);
            }
        }
    }

    static void getReps(String field, List<Integer> repIds) throws PlaceDataException {
        if (repIds.isEmpty()) {
            System.out.println("NO ids to process ... " + field);
            return;
        }

        String queryStr = repIds.stream()
            .map(id -> field + ":" + id)
            .collect(Collectors.joining(" OR "));
        System.out.println("QRY-STR: " + queryStr);

        SolrQuery query = new SolrQuery(queryStr);
        query.setRows(12_000);
        List<PlaceRepDoc> docs = solrConn.search(query);
        System.out.println("SOLR-CNT: " + docs.size());

        for (PlaceRepDoc doc : docs) {
            PlaceRep02X rep = new PlaceRep02X();
            rep.repId = doc.getRepId();
            if (! repMap.containsKey(rep.repId)) {
                rep.parentId = doc.getParentId();
                rep.typeId = doc.getType();
                rep.deleteId = (doc.getDeleteId() == null) ? 0 : doc.getDeleteId();
                rep.fromYear = (doc.getStartYear() == Integer.MIN_VALUE) ? 0 : doc.getStartYear();
                rep.toYear = (doc.getEndYear() == Integer.MAX_VALUE) ? 0 : doc.getEndYear();
                rep.centerLattd = (doc.getLatitude() == null) ? 0.0 : doc.getLatitude();
                rep.centerLong = (doc.getLongitude() == null) ? 0.0 : doc.getLongitude();
                rep.name = doc.getDisplayName("en");
                repMap.put(rep.repId, rep);
            }
        }
    }
}
