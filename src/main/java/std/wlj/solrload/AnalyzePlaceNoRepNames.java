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
import java.util.Map;
import java.util.TreeMap;

import org.familysearch.standards.place.solr.load.PlaceRepFileGenerator;
import org.familysearch.standards.place.util.flatfile.FileResultSet;


public class AnalyzePlaceNoRepNames {
    private static final String DELIMITER = "\\|";
    private static FileResultSet  placeNameRS = null;
    private static final String FILE_PLACE_NAME = "place-name.txt";

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
        File parentDir = new File("C:/temp/place-extract/wlj-one");

        File aFile = new File(parentDir, FILE_PLACE_NAME);
        placeNameRS = new FileResultSet();
        placeNameRS.setSeparator(DELIMITER);
        placeNameRS.openFile(aFile);

        // Some silly counters
        int placeId = -11;
        int nameCnt  = 0;
        Map<String,Integer> nameLocaleCnt = new TreeMap<>();
        List<String> results = new ArrayList<>();

        PlaceName pnRow = readPlaceName();
        while (pnRow != null) {
            if (pnRow.repId == 0) {
                if (pnRow.placeId != placeId) {
                    if (placeId != -11) {
                        results.add(formatResults(placeId, nameCnt, nameLocaleCnt));
                    }
                    placeId = pnRow.placeId;
                    nameCnt = 0;
                    nameLocaleCnt.clear();
                }
                nameCnt++;
                String key = pnRow.locale + "." + pnRow.typeId;
                Integer nlCnt = nameLocaleCnt.get(key);
                nlCnt = (nlCnt == null) ? 1 : (nlCnt + 1);
                if (pnRow.locale != null) {
                    nameLocaleCnt.put(key, nlCnt);
                }
            }
            pnRow = readPlaceName();
        }
        results.add(formatResults(placeId, nameCnt, nameLocaleCnt));

        placeNameRS.close();
        Path outPath = Paths.get("C:/temp/place-no-rep-analysis.txt");
        Files.write(outPath, results, Charset.forName("UTF-8"));

        System.exit(0);
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
     * Print out the results of this place's name
     * 
     * @param placeId place identifier
     * @param nameCnt number of "name" rows
     * @param nameLocaleCnt count of name rows by locale
     */
    private static String formatResults(int placeId, int nameCnt, Map<String, Integer> nameLocaleCnt) {
        StringBuilder buff = new StringBuilder(32);
        
        buff.append(placeId);
        buff.append("|").append(nameCnt);
        buff.append("|").append(nameLocaleCnt);

        return buff.toString();
    }

}
