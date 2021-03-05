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
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrService;

import std.wlj.util.SolrManager;

/**
 * For all place-reps that have attributes (not counting POPULATION) retrieve details, including:
 * <ul>
 *   <li>Full display name (EN)</li>
 *   <li>Full jurisdiction</li>
 *   <li>Lat/Long</li>
 * </ul>
 * 
 * NOTE: Run the "TimelineDumpNoPopulation" file first, which will generate a current ""place-attr-hhs.txt" dataset.
 * @author wjohnson000
 *
 */
public class TimeLineAttrDumpRepDetails {

    private static final String DELIMITER    = "\\|";
    private static final String REP_HEADER   = "rep_id,T:4,D:11,S:0,P:10|full_name,T:12,D:255,S:0,P:255|juris_chain,T:12,D:255,S:0,P:255|lat_long,T:12,D:255,S:0,P:255";

    private static final String fileBase     = "C:/D-drive/homelands/place-data";
    private static final String hhsAttrFile  = "place-attr-hhs.txt";
    private static final String hhsRepFile   = "place-rep-hhs.txt";

    private static final Map<Integer, String> repDisplayName = new TreeMap<>();
    private static final Map<Integer, String> repJurisChain  = new HashMap<>();
    private static final Map<Integer, String> repLatLong     = new HashMap<>();

    public static void main(String...args) throws Exception {
        loadExistingRepData();

        SolrService solrSvc = SolrManager.awsBetaService(true);
        System.out.println("Solr-Conn: " + solrSvc.getReadConnection());

        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(fileBase, hhsAttrFile));

            while (rset.next()) {
                int repId  = rset.getInt("rep_id");
                if (! repDisplayName.containsKey(repId)) {
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
            repDoc.setDataService(solrSvc);
            String jChain   = Arrays.stream(repDoc.getJurisdictionIdentifiers()).mapToObj(id -> String.valueOf(id)).collect(Collectors.joining(","));
            String latLong  = repDoc.getCentroid();

            PlaceRepresentation placeRep = new PlaceRepresentation(repDoc);
            Localized<String> fullName = placeRep.getFullDisplayName(StdLocale.ENGLISH);
            System.out.println(repId + " --> " + fullName.get() + " .. " + jChain + " .. " + latLong);
            repDisplayName.put(repId, fullName.get());
            repJurisChain.put(repId, jChain);
            repLatLong.put(repId, latLong);
        } else {
            System.out.println("Unable to read doc, repId=" + repId);
        }
    }

    static void loadExistingRepData() throws Exception {
        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(fileBase, hhsRepFile));

            while (rset.next()) {
                int repId       = rset.getInt("rep_id");
                String fullName = rset.getString("full_name");
                String jChain   = rset.getString("juris_chain");
                String latLong  = rset.getString("lat_long");

                repDisplayName.put(repId, fullName);
                repJurisChain.put(repId, jChain);
                repLatLong.put(repId, latLong);
            }
        } catch(Exception ex) {
            System.out.println("Unable to load existing data: " + ex.getMessage());
        }

        System.out.println("Existing data size=" + repDisplayName.size());
    }

    static void saveRepData() throws Exception {
        List<String> rows = new ArrayList<>(repDisplayName.size()+10);

        rows.add(REP_HEADER);
        for (Integer repId : repDisplayName.keySet()) {
            StringBuilder buff = new StringBuilder();

            buff.append(repId);
            buff.append("|").append(repDisplayName.getOrDefault(repId, ""));
            buff.append("|").append(repJurisChain.getOrDefault(repId, ""));
            buff.append("|").append(repLatLong.getOrDefault(repId, ""));

            rows.add(buff.toString());
        }

        Files.write(Paths.get(fileBase, hhsRepFile), rows, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
