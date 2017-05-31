package std.wlj.flatfile;

import java.io.File;
import java.util.List;
import java.util.Optional;

import org.familysearch.standards.place.db.loader.model.PlaceDataModel;
import org.familysearch.standards.place.db.loader.model.PlaceRepDataModel;
import org.familysearch.standards.place.db.loader.model.RepAuxDataModel;
import org.familysearch.standards.place.db.reader.IdChainReader;
import org.familysearch.standards.place.db.reader.PlaceDataReader;
import org.familysearch.standards.place.db.reader.PlaceNameReader;
import org.familysearch.standards.place.db.reader.PlaceRepDataReader;
import org.familysearch.standards.place.db.reader.RepAttributeReader;
import org.familysearch.standards.place.db.reader.RepCitationReader;
import org.familysearch.standards.place.db.reader.RepExtXrefReader;
import org.familysearch.standards.place.db.reader.RepNameReader;

public class CheckRepTranId {

    private File               parentDir = new File("D:/tmp/flat-files/one-infinity");

    private PlaceRepDataReader placeRepDataReader;
    private PlaceDataReader    placeDataReader;
    private PlaceNameReader    placeNameReader;
    private RepNameReader      repNameReader;
    private RepAttributeReader repAttrReader;
    private RepCitationReader  repCitnReader;
    private RepExtXrefReader   repExtXrefReader;
    private IdChainReader      idChainReader;

    public static void main(String...args) {
        CheckRepTranId engine = new CheckRepTranId();
        engine.dumpClassPath();
        engine.openReaders();
        engine.processAllDocs();
        engine.closeReaders();
    }

    private void dumpClassPath() {
        String cp = System.getProperty("java.class.path");
        String[] cps = cp.split(";");
        for (String cpx : cps) {
            System.out.println(cpx);
        }
        System.out.println();
    }

    /**
     * Open readers for the eight files.
     */
    protected void openReaders() {
        placeRepDataReader = new PlaceRepDataReader(parentDir);
        placeDataReader    = new PlaceDataReader(parentDir);
        placeNameReader    = new PlaceNameReader(parentDir);
        repNameReader      = new RepNameReader(parentDir);
        repAttrReader      = new RepAttributeReader(parentDir);
        repCitnReader      = new RepCitationReader(parentDir);
        repExtXrefReader   = new RepExtXrefReader(parentDir);
        idChainReader      = new IdChainReader(parentDir);

        placeRepDataReader.openReader();
        placeDataReader.openReader();
        placeNameReader.openReader();
        repNameReader.openReader();
        repAttrReader.openReader();
        repCitnReader.openReader();
        repExtXrefReader.openReader();
        idChainReader.openReader();

        // Seed the "place-rep" data
        placeRepDataReader.getReaderData(0);
    }

    protected void processAllDocs() {
        int nextRepId = placeRepDataReader.nextRepId();
        while (nextRepId > 0) {
            List<PlaceRepDataModel> placeRepData = placeRepDataReader.getReaderData(nextRepId);
            Optional<Integer> repTranId = placeRepData.stream().map(e -> e.tranId).reduce(Integer::max);

            List<PlaceDataModel> placeData = placeDataReader.getReaderData(nextRepId);
            Optional<Integer> placeTranId = placeData.stream().map(e -> e.tranId).reduce(Integer::max);

            List<RepAuxDataModel> placeNameData = placeNameReader.getReaderData(nextRepId);
            Optional<Integer> placeNameTranId = placeNameData.stream().map(e -> e.tranId).reduce(Integer::max);

            List<RepAuxDataModel> repNameData = repNameReader.getReaderData(nextRepId);
            Optional<Integer> repNameTranId = repNameData.stream().map(e -> e.tranId).reduce(Integer::max);

            List<RepAuxDataModel> repAttrData = repAttrReader.getReaderData(nextRepId);
            Optional<Integer> repAttrTranId = repAttrData.stream().map(e -> e.tranId).reduce(Integer::max);

            List<RepAuxDataModel> repCitnData = repAttrReader.getReaderData(nextRepId);
            Optional<Integer> repCitnTranId = repCitnData.stream().map(e -> e.tranId).reduce(Integer::max);

            int maxRep = repTranId.orElse(0);
            int maxPlace = placeTranId.orElse(0);
            int maxOther = Math.max(placeNameTranId.orElse(0), repNameTranId.orElse(0));
//            maxOther = Math.max(maxOther, repAttrTranId.orElse(0));
//            maxOther = Math.max(maxOther, repCitnTranId.orElse(0));
            boolean isOK = maxRep >= maxOther || maxPlace >= maxOther;
            if (! isOK) {
                System.out.println(nextRepId + " | " + maxRep + " | " + maxPlace + " | " + maxOther + " | " + isOK);
            }

            nextRepId = placeRepDataReader.nextRepId();
//            if (nextRepId > 1_000_000) break;
        }
    }

    /**
     * Close all of the files and set the readers back to "null".
     */
    protected void closeReaders() {
        placeRepDataReader.closeReader();
        placeDataReader.closeReader();
        placeNameReader.closeReader();
        repNameReader.closeReader();
        repAttrReader.closeReader();
        repCitnReader.closeReader();
        repExtXrefReader.closeReader();
        idChainReader.closeReader();

        placeRepDataReader = null;
        placeDataReader = null;
        placeNameReader = null;
        repNameReader = null;
        repAttrReader = null;
        repCitnReader = null;
        repExtXrefReader = null;
        idChainReader = null;
    }

}
