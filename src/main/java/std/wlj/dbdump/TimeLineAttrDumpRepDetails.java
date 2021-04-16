/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.core.Localized;
import org.familysearch.standards.core.StdLocale;
import org.familysearch.standards.loader.sql.FileResultSet;
import org.familysearch.standards.place.PlaceRepresentation;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrService;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;

/**
 * For all place-reps that have attributes (not counting POPULATION) retrieve details, including:
 * <ul>
 *   <li>Full display name (EN)</li>
 *   <li>Full jurisdiction</li>
 *   <li>Lat/Long</li>
 * </ul>
 * 
 * NOTE: Run the "TimelineDumpNoPopulation" file first, which will generate a current "place-attr-hhs.txt" dataset.
 * @author wjohnson000
 *
 */
public class TimeLineAttrDumpRepDetails {

    private static class TimelineRepData {
        int     repId;
        String  displayName;
        String  jurisChain;
        String  latLong;
        boolean isPub;
        boolean isVal;
        int     deleteId;
    }

    private static final String DELIMITER    = "\\|";
    private static final String REP_HEADER   = "rep_id,T:4,D:11,S:0,P:10|full_name,T:12,D:255,S:0,P:255|juris_chain,T:12,D:255,S:0,P:255|lat_long,T:12,D:255,S:0,P:255|pub_flag,T:-7,D:1,S:0,P:1|validated_flag,T:-7,D:1,S:0,P:1|delete_id,T:4,D:11,S:0,P:10";

    private static final String fileBase     = "C:/D-drive/homelands/place-data";
    private static final String hhsAttrFile  = "place-attr-hhs.txt";
    private static final String hhsRepFile   = "place-rep-hhs.txt";

    private static final Map<Integer, TimelineRepData> repDetails = new TreeMap<>();

    public static void main(String...args) throws Exception {
        SolrService solrSvc = SolrManager.awsBetaService(true);
        System.out.println("Solr-Conn: " + solrSvc.getReadConnection());

        loadExistingRepData(solrSvc);

        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(fileBase, hhsAttrFile));

            while (rset.next()) {
                int repId  = rset.getInt("rep_id");
                if (! repDetails.containsKey(repId)) {
                    readRepData(solrSvc, repId);
                }
            }
        }

        saveRepData();

        System.exit(0);
    }

    static void readRepData(SolrService solrSvc, int repId) throws PlaceDataException {
        SolrQuery query = new SolrQuery("repId:" + repId);

        List<PlaceRepDoc> docs = solrSvc.getReadConnection().search(query);
        if (docs.size() == 1) {
            PlaceRepDoc repDoc = docs.get(0);

            TimelineRepData repData = new TimelineRepData();
            repDetails.put(repId, repData);

            repDoc.setDataService(solrSvc);
            repData.repId      = repId;
            repData.jurisChain = Arrays.stream(repDoc.getJurisdictionIdentifiers()).mapToObj(id -> String.valueOf(id)).collect(Collectors.joining(","));
            repData.latLong    = repDoc.getCentroid();
            repData.isPub      = repDoc.isPublished();
            repData.isVal      = repDoc.isValidated();
            repData.deleteId   = repDoc.getDeleteId() == null ? 0 : repDoc.getDeleteId();

            PlaceRepresentation placeRep = new PlaceRepresentation(repDoc);
            Localized<String> fullName = placeRep.getFullDisplayName(StdLocale.ENGLISH);
            repData.displayName = fullName.get();
            System.out.println("NEW: " + repId + " --> " + repData.displayName + " .. " + repData.jurisChain + " .. " + repData.latLong);
        } else {
            System.out.println("Unable to read doc, repId=" + repId);
        }
    }

    static void loadExistingRepData(SolrService solrSvc) throws Exception {
        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(fileBase, hhsRepFile));

            while (rset.next()) {
                TimelineRepData repData = new TimelineRepData();
                repData.repId    = rset.getInt("rep_id");
                repData.jurisChain = rset.getString("juris_chain");
                repData.displayName = rset.getString("full_name");
                repData.latLong     = rset.getString("lat_long");
                repData.isPub       = rset.getBoolean("pub_flag");
                repData.isVal       = rset.getBoolean("validated_flag");
                repData.deleteId    = rset.getInt("delete_id");
                if (! repData.isPub  &&  ! repData.isVal) {
                    System.out.println("PRV: " + repData.repId + " --> " + repData.displayName + " .. " + repData.jurisChain + " .. " +
                                                 repData.latLong + " .. " + repData.isPub + " .. " + repData.isVal + " .. " + repData.deleteId);
                }
                if (repData.deleteId > 0) {
                    System.out.println("DEL: " + repData.repId + " --> " + repData.displayName + " .. " + repData.jurisChain + " .. " +
                                                 repData.latLong + " .. " + repData.isPub + " .. " + repData.isVal + " .. " + repData.deleteId);
                }

                repDetails.put(repData.repId, repData);
            }
        } catch(Exception ex) {
            System.out.println("Unable to load existing data: " + ex.getMessage());
        }

        System.out.println("Existing data size=" + repDetails.size());
    }

    static void updateRepData(SolrService solrSvc, TimelineRepData repData) throws Exception {
        SolrQuery query = new SolrQuery("repId:" + repData.repId);

        List<PlaceRepDoc> docs = solrSvc.getReadConnection().search(query);
        if (docs.size() == 1) {
            PlaceRepDoc repDoc = docs.get(0);
            repData.isPub      = repDoc.isPublished();
            repData.isVal      = repDoc.isValidated();
            repData.deleteId   = repDoc.getDeleteId() == null ? 0 : repDoc.getDeleteId();
            System.out.println("UPD: " + repData.repId + " --> " + repData.displayName + " .. " + repData.jurisChain + " .. " + repData.latLong + " .. " + repData.isPub + " .. " + repData.isVal);
        } else {
            System.out.println("Unable to update doc, repId=" + repData.repId);
        }        
    }

    static void saveRepData() throws Exception {
        List<String> rows = new ArrayList<>(repDetails.size()+10);

        rows.add(REP_HEADER);
        for (TimelineRepData repData : repDetails.values()) {
            StringBuilder buff = new StringBuilder();

            buff.append(repData.repId);
            buff.append("|").append(repData.displayName);
            buff.append("|").append(repData.jurisChain);
            buff.append("|").append(repData.latLong);
            buff.append("|").append(repData.isPub);
            buff.append("|").append(repData.isVal);
            buff.append("|").append(repData.deleteId);

            rows.add(buff.toString());
        }

        Files.write(Paths.get(fileBase, hhsRepFile), rows, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
