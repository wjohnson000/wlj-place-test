package std.wlj.solrload;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.familysearch.standards.place.util.flatfile.FileResultSet;


public class AnalyzePlaceNoRepNoDeleteId {
    private static final String DELIMITER = "\\|";
    private static final String FILE_PLACE_MAIN = "place-main.txt";
    private static final String FILE_PLACE_NAME = "place-name.txt";

    private static FileResultSet  placeRS = null;
    private static FileResultSet  placeNameRS = null;
    private static File parentDir = new File("C:/temp/place-extract/wlj-one");

    private static PlaceData currPlaceData;
    private static PlaceName currNameData;

    /**
     * Helper class that stores data from a row in the main "place" file.
     * 
     * @author wjohnson000
     */
    private static class PlaceData {
        private int repId;
        private int placeId;
        private int tranId;
        private int fromYear;
        private int toYear;
        private int deleteId;
    }

    /**
     * Helper class that stores data from a row in an auxiliary data file, such
     * as the rep name, rep attribute, rep citation or rep external-xref.
     * 
     * @author wjohnson000
     */
    private static class PlaceName {
        private int repId;
        private int placeId;
        private int nameId;
        private int tranId;
        private int typeId;
        private String text;
        private String locale;
        private String delFlg;
    }


    public static void main(String... args) throws SQLException, IOException {
        openReaders();
        positionReaders();

        List<String> results = new ArrayList<>();
        while (currPlaceData != null) {
            if (currPlaceData.deleteId <= 1) {
                List<PlaceName> names = readPlaceNames(currPlaceData.placeId);
                boolean first = true;
                for (PlaceName name : names) {
                    if (name.typeId == 437  ||  name.typeId == 441  ||  name.typeId == 445) {
                        StringBuilder buff = new StringBuilder();
                        buff.append(first ? ""+currPlaceData.placeId : "");
                        buff.append("|").append(name.typeId);
                        buff.append("|").append(name.locale);
                        buff.append("|").append(name.text);
                        first = false;
                        results.add(buff.toString());
                    }
                }
                results.add("");

                if (first) {
                    System.out.println("OOPS!! -- " + currPlaceData.placeId);
                }
            }
            currPlaceData = readPlace();
        }

        Path outPath = Paths.get("C:/temp/place-no-rep-no-delete-analysis.txt");
        Files.write(outPath, results, Charset.forName("UTF-8"));

        closeReaders();
        System.exit(0);
    }

    /**
     * Open readers for the seven files.  If any of them fail to open, close any that
     * were opened.
     */
    private static void openReaders() {
        File aFile = new File(parentDir, FILE_PLACE_MAIN);
        placeRS = new FileResultSet();
        placeRS.setSeparator(DELIMITER);
        placeRS.openFile(aFile);

        aFile = new File(parentDir, FILE_PLACE_NAME);
        placeNameRS = new FileResultSet();
        placeNameRS.setSeparator(DELIMITER);
        placeNameRS.openFile(aFile);
    }

    /**
     * Position the two readers to the first non-rep-id row
     */
    private static void positionReaders() {
        try {
            int ignCnt = 0;
            currPlaceData = readPlace();
            while (currPlaceData != null  &&  currPlaceData.repId != 0) {
                ignCnt++;
                currPlaceData = readPlace();
            }
            System.out.println("Skipped " + ignCnt + " places ...");

            ignCnt = 0;
            currNameData = readPlaceName();
            while (currNameData != null  &&  currNameData.repId != 0) {
                ignCnt++;
                currNameData = readPlaceName();
            }
            System.out.println("Skipped " + ignCnt + " place-names ...");
        } catch(SQLException ex) {
            
        }
    }

    /**
     * Close all of the files and set the readers back to "null".
     */
    private static void closeReaders() {
        try {
            placeRS.close();
            placeNameRS.close();
        } catch(SQLException ex) {
            System.out.println("EX: " + ex.getMessage());
        }

        placeRS = null;
        placeNameRS = null;
    }

    /**
     * Read a single row from the 'placeRS' file
     * 
     * @return next row of data, or null if there is no more
     */
    private static PlaceData readPlace() throws SQLException {
        PlaceData pdRow = null;

        if (placeRS.next()) {
            pdRow = new PlaceData();
            pdRow.repId    = placeRS.getInt("rep_id");
            pdRow.placeId  = placeRS.getInt("place_id");
            pdRow.tranId   = placeRS.getInt("tran_id");
            pdRow.fromYear = placeRS.getInt("from_year");
            pdRow.toYear   = placeRS.getInt("to_year");
            pdRow.deleteId = placeRS.getInt("delete_id");
        }

        return pdRow;
    }

    /**
     * Read a single row from the 'placeNameRS' file
     * 
     * @return next row of data, or null if there is no more
     */
    private static PlaceName readPlaceName() throws SQLException {
        PlaceName pdRow = null;

        if (placeNameRS.next()) {
            pdRow = new PlaceName();
            pdRow.repId   = placeNameRS.getInt("rep_id");
            pdRow.placeId = placeNameRS.getInt("place_id");
            pdRow.nameId  = placeNameRS.getInt("name_id");
            pdRow.tranId  = placeNameRS.getInt("tran_id");
            pdRow.typeId  = placeNameRS.getInt("type_id");
            pdRow.locale  = placeNameRS.getString("locale");
            pdRow.text    = placeNameRS.getString("text");
            pdRow.delFlg  = placeNameRS.getString("delete_flg");
        }

        return pdRow;
    }

    /**
     * Read the list of place-name records for the given place-id
     * 
     * @param placeId place identifier that we need to match
     * @Return list of place-name records
     * @throws SQLException
     */
    private static List<PlaceName> readPlaceNames(int placeId) throws SQLException {
        List<PlaceName> result = new ArrayList<>();

        // Read past any old names
        while (currNameData != null  &&  currNameData.placeId < placeId) {
            currNameData = readPlaceName();
        }

        // Load the matching names
        while (currNameData != null  &&  currNameData.placeId == placeId) {
            result.add(currNameData);
            currNameData = readPlaceName();
            
        }

        return result;
    }

}
