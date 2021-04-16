package std.wlj.interpretation;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;
import org.familysearch.standards.place.util.PlaceHelper;

import std.wlj.util.SolrManager;

/**
 * Based on the "C:\D-drive\request-splunk\splunk-request-all.txt" file, which contain details
 * from 2M interpretation requests, run a "proof of concept" approach to searching for a
 * matching place-rep.
 * 
 * @author wjohnson000
 *
 */
public class TopDownInterp {

    static final class InterpResultX {
        String       fullName;
        String[]     names;
        String[]     normalNames;
        String[][]   repIds;
        PlaceRepDoc  prDocs[][];
    }

    static final String baseDir = "C:/D-drive/request-splunk";
    static final String reqFile = "splunk-request-all.txt";

    static int totalCount = 0;
    static int candidateCount = 0;
    static int fullMatchCount = 0;

    static Set<String> uniqueNames = new HashSet<>();
    static Set<String> candidateNames = new HashSet<>();
    static Set<String> fullMatchNames = new HashSet<>();

    static Map<String, Set<String>> topLevelNames;

    public static void main(String... args) throws PlaceDataException, IOException {
        SolrConnection solrConn = SolrManager.localEmbeddedConnection("C:/D-drive/solr/standalone-7.1.0");
        System.out.println("Write-Ready: " + solrConn.isWriteReady());

        topLevelNames = getLevel01(solrConn);
        System.out.println("Top-level name size: " + topLevelNames.size());

        processRequestsFromSplunk(solrConn);

        System.out.println("Total.count: " + totalCount);
        System.out.println("    U.count: " + uniqueNames.size());

        System.out.println("Candt.count: " + candidateCount);
        System.out.println("    U.count: " + candidateNames.size());

        System.exit(0);
    }

    static Map<String, Set<String>> getLevel01(SolrConnection conn) throws PlaceDataException {
        Map<String, Set<String>> nameToRep = new HashMap<>();

        SolrQuery query = new SolrQuery("parentId:\\-1");
        query.addFilterQuery("-deleteId:[* TO *]");

        query.setRows(1000);
        List<PlaceRepDoc> docs = conn.search(query);

        for (PlaceRepDoc doc : docs) {
            for (String name : doc.getNames()) {
                Set<String> repIds = nameToRep.get(name);
                if (repIds == null) {
                    repIds = new HashSet<>();
                    nameToRep.put(name, repIds);
                }
                repIds.add(String.valueOf(doc.getRepId()));
            }
        }

        return nameToRep;
    }
    
    static void processRequestsFromSplunk(SolrConnection conn) throws IOException, PlaceDataException {
        List<String> requests = Files.readAllLines(Paths.get(baseDir, reqFile), StandardCharsets.UTF_8);
        System.out.println("Number of requests: " + requests.size());

        for (String request : requests) {
            processRequest(conn, request);
        }
    }

    static void processRequest(SolrConnection conn, String request) throws PlaceDataException {
        String[] chunks = PlaceHelper.split(request, '|');
        if (chunks.length > 3) {
            totalCount++;

            String name = chunks[0].trim();
            while (name.startsWith(" ")  ||  name.startsWith(",")) name = name.substring(1);
            uniqueNames.add(name);

            if (fullMatchNames.contains(name)) {
                fullMatchCount++;
            } else {
                String[] nameChunks = PlaceHelper.split(name, ',');
                InterpResultX irx = processRequest(conn, name, nameChunks);
                if (irx != null  &&  irx.prDocs != null  &&  irx.prDocs[0] != null  &&  irx.prDocs[0].length > 0) {
                    fullMatchCount++;
                }
            }
            System.out.println("Total: " + totalCount + " --> Match: " + fullMatchCount);
        }
    }

    static InterpResultX processRequest(SolrConnection conn, String name, String[] nameChunks) throws PlaceDataException {
        InterpResultX irx = null;

        String topLevelName = PlaceHelper.normalize(nameChunks[nameChunks.length-1]).trim().toLowerCase();
        if (topLevelNames.containsKey(topLevelName)) {
            candidateCount++;
            candidateNames.add(topLevelName);

            irx = new InterpResultX();
            irx.fullName    = name;
            irx.names       = nameChunks;
            irx.normalNames = new String[nameChunks.length];
            irx.repIds      = new String[nameChunks.length][];
            irx.prDocs      = new PlaceRepDoc[nameChunks.length][];

            for (int i=0;  i<nameChunks.length-1;  i++) {
                irx.normalNames[i] = PlaceHelper.normalize(nameChunks[i]).replace('(', ' ').replace(')', ' ').replace('<', ' ').replace('>', ' ').replace('[', ' ').replace(']', ' ').replace('{', ' ').replace('}', ' ').replace(':', ' ').trim().toLowerCase();
            }
            irx.normalNames[nameChunks.length-1] = topLevelName;

            Set<String> repIds = topLevelNames.get(topLevelName);
            irx.repIds[nameChunks.length-1] = repIds.toArray(new String[0]);
            irx.prDocs[nameChunks.length-1] = getDocs(conn, repIds).toArray(new PlaceRepDoc[0]);

            for (int i=nameChunks.length-2;  i>=0;  i--) {
                String nName          = irx.normalNames[i];
                Set<String> parRepIds = Arrays.stream(irx.prDocs[i+1]).map(prDoc -> String.valueOf(prDoc.getRepId())).collect(Collectors.toSet());
                if (parRepIds.isEmpty()  ||  nName.trim().isEmpty()) break;

                List<PlaceRepDoc> candidates = getDocs(conn, nName, parRepIds);
                irx.repIds[i] = candidates.stream().map(prDoc -> String.valueOf(prDoc.getRepId())).collect(Collectors.toList()).toArray(new String[0]);
                irx.prDocs[i] = candidates.toArray(new PlaceRepDoc[0]);
            }
        }

        return irx;
    }

    static List<PlaceRepDoc> getDocs(SolrConnection conn, Set<String> repIds) throws PlaceDataException {
        String repIdStr = repIds.stream().collect(Collectors.joining(" ", "repId:(", ")"));
        SolrQuery query = new SolrQuery(repIdStr);
        query.addFilterQuery("-deleteId:[* TO *]");
        query.setRows(100);
        return conn.search(query);
    }
    
    static List<PlaceRepDoc> getDocs(SolrConnection conn, String nName, Set<String> parRepIds) throws PlaceDataException {
        String repIdStr = parRepIds.stream().collect(Collectors.joining(" ", "parentId:(", ")"));
        SolrQuery query = new SolrQuery(repIdStr + " AND names:" + nName);
        query.addFilterQuery("-deleteId:[* TO *]");
        query.setRows(100);
        return conn.search(query);
    }
}
