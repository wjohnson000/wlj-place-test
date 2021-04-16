package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;


public class STD2696Details {

    private static Map<String,PlaceRepDoc> docCache = new HashMap<>();
    private static Set<String> fixedIds = new HashSet<>();
    static {
        fixedIds.add("10330742");        
        fixedIds.add("10335852");        
        fixedIds.add("10335858");        
        fixedIds.add("10336168");        
    }


    public static void main(String... args) throws PlaceDataException, IOException {
        SolrConnection solrConn = SolrManager.awsProdConnection(true);

        List<String> entries = Files.readAllLines(Paths.get("C:/temp/deleted-reps.txt"), StandardCharsets.UTF_8);
        entries.clear();
        entries.add("1160755|10330742");  // Bad Entry
        entries.add("1442484|10335852");  // Bad Entry
        entries.add("1442492|10335858");  // Bad Entry
        entries.add("1470205|10336168");  // Bad Entry
        entries.add("8863508|10332351");  // Bad Entry
        entries.add("8865453|10333901");  // Bad Entry
        entries.add("8911772|10334653");  // Bad Entry
        entries.add("8912802|10335186");  // Bad Entry
        entries.add("9192726|10335440");  // Bad Entry
        entries.add("8186295|7076840");   // Good Entry

        int count = 0;
        for (String entry : entries) {
            if (++count % 200 == 0) System.out.println("Processed count=" + count);

            String[] tokens = entry.split("\\|");
            for (String repId : tokens) {
                boolean isOK = true;
                Set<Integer> chainIds = new HashSet<>();
                List<PlaceRepDoc> chainDocs = new ArrayList<>();
                try {
                    PlaceRepDoc prDoc = getDoc(solrConn, repId);
                    if (prDoc != null) {
                        chainDocs.add(prDoc);
                        chainIds.add(prDoc.getRepId());

                        // Follow the chain of the replacement id ...
                        while (prDoc != null  &&  isOK) {
                            if (prDoc.getDeleteId() != null  &&  ! fixedIds.contains(String.valueOf(prDoc.getDeleteId()))) {
                                prDoc = getDoc(solrConn, String.valueOf(prDoc.getDeleteId()));
                            } else {
                                prDoc = (prDoc.getParentId() <= 0) ? null : getDoc(solrConn, String.valueOf(prDoc.getParentId()));
                            }
                            if (prDoc != null) {
                                chainDocs.add(prDoc);
                                if (chainIds.contains(prDoc.getRepId())) {
                                     isOK = false;
                                }
                                chainIds.add(prDoc.getRepId());
                            }
                        }
                    }
                    
                } catch(Exception ex) {
                    System.out.println("Unable to process line: " + entry + " --> " + ex.getMessage());
                }

//                if (! isOK) {
                    System.out.println("OK? " + isOK);
                    for (int i=chainDocs.size()-1;  i>=0;  i--) {
                        PlaceRepDoc prDoc = chainDocs.get(i);
                        StringBuilder buff = new StringBuilder();
                        buff.append(prDoc.getRepId());
                        buff.append("|" + prDoc.getRevision());
                        buff.append("|" + prDoc.getParentId());
                        buff.append("|" + Arrays.toString(prDoc.getRepIdChainAsInt()));
                        buff.append("|" + prDoc.getPrefLocale());
                        buff.append("|" + prDoc.getDisplayName(prDoc.getPrefLocale()));
                        buff.append("|" + prDoc.getDeleteId());
                        System.out.println(buff.toString());
                    }
                    System.out.println();
                    System.out.println();
//                }

            }
        }

        solrConn.shutdown();
        System.exit(0);
    }

    /**
     * Read a document from SOLR based on the rep-id
     * 
     * @param solrConn solr connection
     * @param id place-rep identifier
     * @return latest document ...
     * 
     * @throws PlaceDataException
     */
    private static PlaceRepDoc getDoc(SolrConnection solrConn, String id) throws PlaceDataException {
        if (docCache.containsKey(id)) {
            return docCache.get(id);
        }

        SolrQuery query = new SolrQuery("id:" + id + "-*");
        query.setRows(1);
        query.setSort("revision", SolrQuery.ORDER.desc);

        List<PlaceRepDoc> docs = solrConn.search(query);
        if (docs.size() == 0)  {
            System.out.println("Not Found: " + id);
            return null;
        } else {
            docCache.put(id, docs.get(0));
            return docs.get(0);
        }
    }

}
