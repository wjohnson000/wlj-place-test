package std.wlj.solr;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.familysearch.standards.place.appdata.AppDataGroupMapper;
import org.familysearch.standards.place.appdata.AppDataNamePriorityMapper;
import org.familysearch.standards.place.appdata.AppDataSourceMapper;
import org.familysearch.standards.place.appdata.AppDataTypeMapper;
import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.data.solr.SolrConnection;
import org.familysearch.standards.place.exceptions.PlaceDataException;

import std.wlj.util.SolrManager;


public class SearchForSelectedDocuments {

    public static void main(String... args) throws PlaceDataException, IOException {
        SolrConnection solrConn = SolrManager.awsDevConnection(false);
        processLotsOfDocuments(solrConn, "D:/tmp/solr-docs/dev-new");
        solrConn.shutdown();

        System.exit(0);
    }

    protected static void processLotsOfDocuments(SolrConnection solrConn, String outputPath) throws PlaceDataException, IOException {
        File outputDir = new File(outputPath);
        if (outputDir.exists()  &&  outputDir.isDirectory()) {
            String[] files = outputDir.list();
            if (files.length > 0) {
                System.out.println("Output directory isn't empty: " + outputPath);
                return;
            }
        } else {
            System.out.println("Output directory isn't valid: " + outputPath);
            return;
        }

        // Process the first 1000 documents
        for (int i=0;  i<1000;  i++) {
            String docId = "" + i + "-*";
            processRepDoc(solrConn, docId, outputPath);
        }

        // Process selected documents from here on out
        for (int i=1000;  i<=11_000_000;  i+=8765) {
            String docId = "" + i + "-*";
            processRepDoc(solrConn, docId, outputPath);
        }

        // Process some of the place-no-rep documents
        for (int i=8_000_000;  i<=9_000_000;  i+=1111) {
            String docId = "PLACE-" + i + "*";
            processRepDoc(solrConn, docId, outputPath);
        }

        // Process the APP-DATA documents
        processRepDoc(solrConn, AppDataTypeMapper.ATTR_TYPE_ID, outputPath);
        processRepDoc(solrConn, AppDataTypeMapper.CITATION_TYPE_ID, outputPath);
        processRepDoc(solrConn, AppDataTypeMapper.EXT_XREF_TYPE_ID, outputPath);
        processRepDoc(solrConn, AppDataTypeMapper.NAME_TYPE_ID, outputPath);
        processRepDoc(solrConn, AppDataTypeMapper.PLACE_TYPE_ID, outputPath);
        processRepDoc(solrConn, AppDataTypeMapper.RESOLUTION_TYPE_ID, outputPath);
        processRepDoc(solrConn, AppDataTypeMapper.FEEDBACK_RESOLUTION_TYPE_ID, outputPath);
        processRepDoc(solrConn, AppDataTypeMapper.FEEDBACK_STATUS_TYPE_ID, outputPath);
        processRepDoc(solrConn, AppDataSourceMapper.SOURCE_ID, outputPath);
        processRepDoc(solrConn, AppDataGroupMapper.GROUP_HIERARCHY_ID, outputPath);
        processRepDoc(solrConn, AppDataNamePriorityMapper.NAME_PRIORITY_ID, outputPath);
    }

    protected static void processRepDoc(SolrConnection solrConn, String docId, String outputPath) throws PlaceDataException, IOException {
        SolrQuery query = new SolrQuery("id:" + docId);
        query.setSort("revision", SolrQuery.ORDER.asc);
        query.setRows(100);

        List<PlaceRepDoc> docs = solrConn.search(query);
        for (PlaceRepDoc doc : docs) {
            List<String> details = getDocDetails(doc);

            System.out.println("ID: " + doc.getId());
            String fileName = doc.getId() + ".txt";
            Files.write(Paths.get(outputPath, fileName), details, StandardCharsets.UTF_8);
        }
    }

    /**
     * Create a reasonable format for the pr-doc output
     * 
     * @param prDoc place-rep document
     * @param path path where the file is to be saved
     * @throws IOException
     */
    private static List<String> getDocDetails(PlaceRepDoc prDoc) throws IOException {
        List<String> details = new ArrayList<>();

        details.add("id: " + prDoc.getId());
        details.add("repId: " + prDoc.getRepId());
        details.add("parentId: " + prDoc.getParentId());
        details.add("revision: " + prDoc.getRevision());
        details.add("ownerId: " + prDoc.getOwnerId());
        details.add("typeId: " + prDoc.getType());
        details.add("centroid: " + prDoc.getCentroid());
        details.add("prefLocale: " + prDoc.getPrefLocale());
        details.add("startYr: " + prDoc.getStartYear());
        details.add("endYr: " + prDoc.getEndYear());
        details.add("ownerStartYr: " + prDoc.getOwnerStartYear());
        details.add("ownerEndYr: " + prDoc.getOwnerEndYear());
        details.add("deleteId: " + prDoc.getDeleteId());
        details.add("placeDeleteId: " + prDoc.getPlaceDeleteId());
        details.add("published: " + prDoc.getPublished());
        details.add("validated: " + prDoc.getValidated());
        details.add("groupId: " + prDoc.getTypeGroup());
        details.add("uuid: " + prDoc.getUUID());
        details.add("idChain: " + Arrays.toString(prDoc.getRepIdChainAsInt()));

        for (String xx : sortData(prDoc.getVariantNames())) {
            details.add("varName: " + xx);
        }

        for (String xx : sortData(prDoc.getDisplayNames())) {
            details.add("dispName: " + xx);
        }

        for (String xx : sortData(prDoc.getCitations())) {
            details.add("citation: " + xx);
        }

        for (String xx : sortData(prDoc.getAttributes())) {
            details.add("attribute: " + xx);
        }

        for (String xx : sortData(prDoc.getExtXrefs())) {
            details.add("extXref: " + xx);
        }

        for (String xx : sortData(prDoc.getAppData())) {
            details.add("appData: " + xx);
        }

        return details;
    }

    private static List<String> sortData(List<String> data) {
        List<String> sData = new ArrayList<String>();
        for (String datum : data) {
            sData.add(datum.replace('\n', ' ').replace('\r', ' '));
        }

        Collections.sort(sData);
        return sData;
    }
}
