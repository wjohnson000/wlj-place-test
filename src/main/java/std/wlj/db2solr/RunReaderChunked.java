package std.wlj.db2solr;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.familysearch.standards.place.data.solr.PlaceRepDoc;
import org.familysearch.standards.place.solr.load.PlaceRepReader;

import edu.emory.mathcs.backport.java.util.Arrays;


public class RunReaderChunked {
    public static void main(String... args) throws IOException {
        String dataDir = "C:/temp/place-extract/wlj-chunk";
        String jsonDir = "C:/temp/place-extract/docs-chunk";

        String[] subDirs = new File(dataDir).list();
        for (String subDir : subDirs) {
            PlaceRepReader prReader = new PlaceRepReader(new File(dataDir, subDir));
            Iterator<PlaceRepDoc> iter = prReader.iterator();
            while (iter.hasNext()) {
                PlaceRepDoc prDoc = iter.next();
                if (prDoc.getRepId() % 1001 == 0  ||  prDoc.getRepId() == 8866947) {
                    Path path = Paths.get(jsonDir, prDoc.getId() + ".txt");
                    saveDoc(prDoc, path);
                }
            }
        }
    }

    /**
     * Create a reasonable format for the pr-doc output
     * 
     * @param prDoc place-rep document
     * @param path path where the file is to be saved
     * @throws IOException
     */
    private static void saveDoc(PlaceRepDoc prDoc, Path path) throws IOException {
        List<String> details = new ArrayList<>();

        details.add("id: " + prDoc.getId());
        details.add("repId: " + prDoc.getRepId());
        details.add("parentId: " + prDoc.getParentId());
        details.add("revision: " + prDoc.getRevision());
        details.add("fwdRevision: " + prDoc.getForwardRevision());
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
        details.add("uuid: " + Arrays.toString(prDoc.getRepIdChainAsInt()));

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

        Files.write(path, details, Charset.forName("UTF-8"));
    }

    private static List<String> sortData(List<String> data) {
        List<String> sData = new ArrayList<String>(data);
        Collections.sort(sData);
        return sData;
    }
}
