package std.wlj.jira;

import java.io.IOException;
import java.nio.charset.Charset;
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
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;


public class STD2696Details {

    private static Map<String,PlaceRepDoc> docCache = new HashMap<>();

    public static void main(String... args) throws PlaceDataException, IOException {
        String solrHome = "http://familysearch.org/int-solr-repeater/places";

        System.setProperty("solr.solr.home", solrHome);
        System.setProperty("solr.master.url", solrHome);
        System.setProperty("solr.master", "false");
        System.setProperty("solr.slave", "false");
        SolrConnection solrConn = SolrConnection.connectToRemoteInstance(solrHome);

        List<String> entries = Files.readAllLines(Paths.get("C:/temp/deleted-reps.txt"), Charset.forName("UTF-8"));
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

            boolean isOK = true;
            Set<Integer> chainIds = new HashSet<>();
            List<PlaceRepDoc> chainDocs = new ArrayList<>();
            String[] tokens = entry.split("\\|");
            if (tokens.length == 2) {
                try {
                    PlaceRepDoc pxrDoc = getDoc(solrConn, tokens[0]);
                    PlaceRepDoc parDoc = getDoc(solrConn, tokens[1]);

                    if (pxrDoc != null) {
                        chainDocs.add(pxrDoc);
                        chainIds.add(pxrDoc.getRepId());
                    }
                    if (parDoc != null) {
                        chainDocs.add(parDoc);
                        chainIds.add(parDoc.getRepId());

                        // Follow the chain of the replacement id ...
                        while (parDoc != null  &&  isOK) {
//                            if (parDoc.getDeleteId() != null) {
//                                parDoc = getDoc(solrConn, String.valueOf(parDoc.getDeleteId()));
//                            } else {
//                                parDoc = (parDoc.getParentId() <= 0) ? null : getDoc(solrConn, String.valueOf(parDoc.getParentId()));
//                            }
                            parDoc = (parDoc.getParentId() <= 0) ? null : getDoc(solrConn, String.valueOf(parDoc.getParentId()));
                            if (parDoc != null) {
                                chainDocs.add(parDoc);
                                if (chainIds.contains(parDoc.getRepId())) {
                                     isOK = false;
                                }
                                chainIds.add(parDoc.getRepId());
                            }
                        }
                    }
                    
                } catch(Exception ex) {
                    System.out.println("Unable to process line: " + entry + " --> " + ex.getMessage());
                }

//                if (! isOK) {
                    System.out.println("OK? " + isOK);
                    for (PlaceRepDoc prDoc : chainDocs) {
                        StringBuilder buff = new StringBuilder();
                        buff.append(prDoc.getRepId());
                        buff.append("|" + prDoc.getRevision());
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
