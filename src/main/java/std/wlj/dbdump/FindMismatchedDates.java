/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.util.PlaceHelper;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;

/**
 * @author wjohnson000
 *
 */
public class FindMismatchedDates {

    private static final String dataDir  = "C:/temp/db-dump";
    private static final String repFile  = "place-rep-all.txt";
    private static final String outFile  = "rep-bad-dates.txt";

    private static SolrConnection solrConn;
    private static List<String> results = new ArrayList<>(250_000);
    
    public static void main(String...args) throws Exception {
        Map<String, String> types = DumpTypes.loadAllTypes();
        solrConn = SolrManager.localEmbeddedConnection("C:/D-drive/solr/standalone-7.7.1");

        // Extract dates for all non-deleted reps that have either a from or to date
        Map<String, Integer[]> repDates = new HashMap<>();
        try(FileInputStream fis = new FileInputStream(new File(dataDir, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                String   rowData   = scan.nextLine();
                String[] rowFields = PlaceHelper.split(rowData, '|');
                if (rowFields.length > 5) {
                    String sRepId = rowFields[0];
                    String frDate = rowFields[7];
                    String toDate = rowFields[8];
                    String sDelId = rowFields[9];

                    if (sDelId.isEmpty()  &&  (! frDate.isEmpty()  ||  ! toDate.isEmpty())) {
                        Integer from = (frDate.isEmpty()) ? null : Integer.parseInt(frDate); 
                        Integer to   = (toDate.isEmpty()) ? null : Integer.parseInt(toDate);
                        repDates.put(sRepId, new Integer[] { from, to });
                    }
                }
            }
        }

        // Compare rep dates with parent dates
        try(FileInputStream fis = new FileInputStream(new File(dataDir, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                String   rowData   = scan.nextLine();
                String[] rowFields = PlaceHelper.split(rowData, '|');
                if (rowFields.length > 5) {
                    String sRepId = rowFields[0];
                    String sParId = rowFields[2];
                    String sTypId = rowFields[6];
                    String sDelId = rowFields[9];

                    if (sDelId.isEmpty()) {
                        Integer[] cFromTo = repDates.get(sRepId);
                        Integer[] pFromTo = repDates.get(sParId);
                        if (cFromTo != null  &&  pFromTo != null  &&  ! overlapOK(pFromTo, cFromTo)) {
                            String cName = getRepName(sRepId);
                            String pName = getRepName(sParId);
                            results.add(sRepId + "|" + cName + "|" + types.getOrDefault(sTypId, "Unknown") +  "|" + cFromTo[0] + "|" + cFromTo[1] + "|" + sParId + "|" + pName + "|" + pFromTo[0] + "|" + pFromTo[1]);
                        }
                    }
                }
            }
        }

        Files.write(Paths.get(dataDir, outFile), results, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.out.println("Dates: " + repDates.size());
        System.out.println("  Bad: " + results.size());
        System.exit(0);
    }

    static boolean overlapOK(Integer[] parYears, Integer[] childYears) {
        if (childYears[0] != null  &&  parYears[0] != null  &&  childYears[0] < parYears[0]) {
            return false;
        } else if (childYears[1] != null  &&  parYears[1] != null  &&  childYears[1] > parYears[1]) {
            return false;
        } else {
            return true;
        }
    }

    static String getRepName(String repId) throws PlaceDataException {
        SolrQuery solrQuery = new SolrQuery("repId:" + repId);
        List<PlaceRepDoc> docs = solrConn.search(solrQuery);
        if (docs.isEmpty()) {
            return "Unknown";
        }

        PlaceRepDoc doc = docs.get(0);
        Map<String, String> dispNames = doc.getAllDisplayNames();
    
        if (dispNames.containsKey("en")) {
            return dispNames.get("en");
        } else if (dispNames.containsKey(doc.getDefaultLocale())) {
            return dispNames.get(doc.getDefaultLocale());
        } else {
            return dispNames.values().stream().findFirst().orElse("Unknown");
        }
    }
}
