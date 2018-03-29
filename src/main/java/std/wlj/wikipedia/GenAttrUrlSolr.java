/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.wikipedia;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.loader.sql.FileResultSet;
import org.familysearch.standards.place.data.AttributeBridge;
import org.familysearch.standards.place.data.PlaceDataException;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.util.PlaceHelper;

import std.wlj.util.SolrManager;

/**
 * Update Solr "Attribute" elements:
 * <ul>
 *   <li>Move the "value" value to "url" for everything starting with "http"</li>
 *   <li>Add an appropriate "title" value if discovered</li>
 * </ul>
 * @author wjohnson000
 *
 */
public class GenAttrUrlSolr {

    static class AttrDatums {
        int repId;
        int attrId;
        int tranId;
        int typeId;
        String value;
        String title;
    }

    private static final String ATTR_FILE   = "C:/temp/db-dump/attribute-all.txt";
    private static final String TITLE_FILE  = "C:/temp/attr-url-title-easy.txt";
    private static final Charset UTF_8      = Charset.forName("UTF-8");

    private static Set<String> badTitles = new HashSet<>();
    static {
        badTitles.add("302 found");
        badTitles.add(" moved");
        badTitles.add("search results");
        badTitles.add("error");
    }

    public static void main(String... args) {
        Map<Integer, List<AttrDatums>> attrData = getAttrsToChange();

        SolrConnection solrConn = SolrManager.awsBetaConnection(true);
        Map<Integer, List<AttrDatums>> attrChunk = chunkOfData(200, attrData);
        while (! attrChunk.isEmpty()) {
            updateSolr(solrConn, attrChunk);
            attrChunk = chunkOfData(200, attrData);
        }
    }

    protected static Map<String, String> loadTitles() {
        List<String> titleData;
        try {
            titleData = Files.readAllLines(Paths.get(TITLE_FILE), UTF_8);
            return titleData.stream()
                .map(line -> PlaceHelper.split(line, '|'))
                .filter(arr -> arr.length > 1)
                .collect(Collectors.toMap(
                    arr -> arr[0],
                    arr -> arr[1],
                    (val1, val2) -> val1,
                    TreeMap::new));
        } catch (IOException e) {
            return new TreeMap<>();
        }
    }

    protected static Map<Integer, List<AttrDatums>> getAttrsToChange() {
        Map<String,String> urlTitle = loadTitles();

        Map<Integer, List<AttrDatums>> attrData = new TreeMap<>();

        try (FileResultSet attrRS = new FileResultSet()) {
            attrRS.setSeparator("\\|");
            attrRS.openFile(ATTR_FILE);

            while (attrRS.next()) {
                int repId    = attrRS.getInt("rep_id");
                int attrId   = attrRS.getInt("attr_id");
                int tranId   = attrRS.getInt("tran_id");
                int typeId   = attrRS.getInt("attr_type_id");
                String value = attrRS.getString("attr_value");
                if (value != null  &&  value.startsWith("http")) {
                    AttrDatums datum = new AttrDatums();
                    datum.repId = repId;
                    datum.attrId = attrId;
                    datum.tranId = tranId;
                    datum.typeId = typeId;
                    datum.value  = value;
                    datum.title  = urlTitle.get(value);

                    if (datum.title != null) {
                        boolean isGood = badTitles.stream()
                                .noneMatch(chunk -> datum.title.toLowerCase().contains(chunk));
                        if (! isGood) {
                            datum.title = null;
                        }
                    }

                    List<AttrDatums> repDatums = attrData.get(repId);
                    if (repDatums == null) {
                        repDatums = new ArrayList<>();
                        attrData.put(repId, repDatums);
                    }
                    repDatums.add(datum);
                }
            }
        } catch (Exception ex) {
            System.out.println("OOPS: " + ex.getMessage());
        }

        return attrData;
    }

    protected static Map<Integer, List<AttrDatums>> chunkOfData(int count, Map<Integer, List<AttrDatums>> fullData) {
        Map<Integer, List<AttrDatums>> chunkData = fullData.entrySet().stream()
            .limit(count)
            .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

        chunkData.keySet().forEach(key -> fullData.remove(key));
        return chunkData;
    }

    static boolean ppp = true;
    protected static void updateSolr(SolrConnection solrConn, Map<Integer, List<AttrDatums>> attrChunk) {
        String queryStr = attrChunk.keySet().stream()
            .map(key -> String.valueOf(key))
            .collect(Collectors.joining(" ", "repId:(", ")"));

        try {
            System.out.println("Updating: " + queryStr.substring(0, 111));
            SolrQuery query = new SolrQuery(queryStr);
            query.setRows(500);
            List<PlaceRepDoc> docs = solrConn.search(query);
            for (PlaceRepDoc doc : docs) {
                ppp = (doc.getRepId() > 350  &&  doc.getRepId() < 375);
                if (ppp) dumpAttributes(doc);
                updateAttribute(doc, attrChunk);
                if (ppp) dumpAttributes(doc);
            }
//            solrConn.add(docs);
//            solrConn.commit();
        } catch (PlaceDataException ex) {
            System.out.println("OOPS: " + ex.getMessage());
        }
    }

    static void updateAttribute(PlaceRepDoc doc, Map<Integer, List<AttrDatums>> attrChunk) {
        List<String> currAttributes = new ArrayList<>(doc.getAttributes());

        doc.clearAttributes();
        for (String attr : currAttributes) {
            String[] chunks = PlaceHelper.split(attr, '|');
            int attrId = getInt(chunks, 0);
            int typeId = getInt(chunks, 1);
            Integer fromYr = getInteger(chunks, 2);
            String value  = getString(chunks, 3);
            String locale = getString(chunks, 4);
            Integer toYr  = getInteger(chunks, 5);
            String cpyNte = getString(chunks, 6);
            String cpyUrl = getString(chunks, 7);
            String url    = getString(chunks, 8);
            String title  = getString(chunks, 9);

            if (value != null  &&  value.toLowerCase().startsWith("http")  &&  attrChunk.containsKey(doc.getRepId())) {
                AttrDatums match = null;
                for (AttrDatums candidate : attrChunk.get(doc.getRepId())) {
                    if (candidate.attrId == attrId) {
                        if (match == null  ||  candidate.tranId > match.tranId) {
                            match = candidate;
                        }
                    }
                }
                if (match != null) {
                    url = match.value;
                    value = null;
                    title = match.title;
                }
            }
            doc.addAttribute(attrId, typeId, fromYr, toYr, value, locale, url, title, cpyNte, cpyUrl);
        }
    }

    static String getString(String[] chunks, int ndx) {
        return (ndx < chunks.length) ? chunks[ndx] : null;
    }

    static int getInt(String[] chunks, int ndx) {
        return (ndx < chunks.length) ? Integer.parseInt(chunks[ndx]) : 0;
    }

    static Integer getInteger(String[] chunks, int ndx) {
        return (ndx < chunks.length  &&  chunks[ndx].length() > 0) ? Integer.parseInt(chunks[ndx]) : null;
    }

    static void dumpAttributes(PlaceRepDoc doc) {
        System.out.println("============================================================================");
        System.out.println("REP: " + doc.getRepId());
        doc.getAttributes().forEach(attr -> System.out.println("  " + attr));
    }
}
