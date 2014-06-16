package std.wlj.dbnew;

import java.io.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import org.familysearch.standards.core.logging.Logger;

import std.wlj.util.FileUtils;


/**
 * Replace place-, name-, attribute- and citation-type identifiers with associated
 * codes, so that IDs can change w/out affecting the tables which have FK values
 * to the "type" table.
 * 
 * @author wjohnson000
 */
public class MassageBaseTableData {

    private static Logger logger = new Logger(MassageBaseTableData.class);

    private static String dataFileIn = "C:/temp/load-place-db/load-base-values.sql";
    private static String dataFileOut = "C:/temp/load-place-db/load-base-values-xxx.sql";

    private static Map<Integer,Integer> placeToPlaceMap = new HashMap<>();
    static {
//        placeToPlaceMap.put(2, 254);
        placeToPlaceMap.put(17, 254);
        placeToPlaceMap.put(19, 278);
        placeToPlaceMap.put(42, 375);
        placeToPlaceMap.put(47, 362);
        placeToPlaceMap.put(127, 209);
        placeToPlaceMap.put(207, 186);
        placeToPlaceMap.put(501, 186);
    }

    /**
     * Load the type data into arrays (ID --> CODE, CODE --> ID); read in the data
     * and massage the PLACE_NAME, PLACE_REP, REP_ATTR and CITATION table data.
     */
    private static void fixSQL() {
        Map<Integer,String> idToCodeMap = new HashMap<>();

        BufferedReader reader = null;
        PrintWriter    writer = null;
        try {
            reader = FileUtils.getReader(dataFileIn);
            writer = FileUtils.getWriter(dataFileOut);
            String sql = null;
            while((sql = reader.readLine()) != null) {
                if (sql.startsWith("INSERT INTO type(")) {
                    writer.println(sql);
                    int ndx0 = sql.indexOf(" VALUES(");
                    int ndx1 = sql.indexOf(',', ndx0+1);
                    int ndx2 = sql.indexOf(',', ndx1+1);
                    int typeNdx = Integer.parseInt(sql.substring(ndx0+8, ndx1));
                    String typeCod = sql.substring(ndx1+3, ndx2-1);
                    idToCodeMap.put(typeNdx, typeCod);
                } else if (sql.startsWith("INSERT INTO place_name(")) {
                    for (int key=1025;  key<=1049;  key++) {
                        String code = idToCodeMap.get(key-591);
                        String token = ", NT:" + code + ",";
                        sql = sql.replaceAll(", " + key + ",", token);
                    }
                    writer.println(sql);
                } else if (sql.startsWith("INSERT INTO place_rep(")) {
                    for (Map.Entry<Integer,Integer> entry : placeToPlaceMap.entrySet()) {
                        int fPlace = entry.getKey();
                        int tPlace = entry.getValue();
                        String code = idToCodeMap.get(tPlace);
                        String token = ", PT:" + code + ",";
                        sql = sql.replaceAll(", " + fPlace + ",", token);
                    }
                    writer.println(sql);
                } else if (sql.startsWith("INSERT INTO rep_attr(")) {
                    for (int key=988;  key<=1024;  key++) {
                        String code = idToCodeMap.get(key-585);
                        String token = ", AT:" + code + ",";
                        sql = sql.replaceAll(", " + key + ",", token);
                    }
                    writer.println(sql);
                } else if (sql.startsWith("INSERT INTO citation(")) {
                    for (int key=1050;  key<=1055;  key++) {
                        String code = idToCodeMap.get(key-591);
                        String token = ", CT:" + code + ",";
                        sql = sql.replaceAll(", " + key + ",", token);
                    }
                    writer.println(sql);
                } else {
                    writer.println(sql);
                }
            }
        } catch(Exception ex) {
            logger.error("Unable to do something ..." + ex.getMessage());
        } finally {
            try { reader.close(); } catch(Exception ex) { }
            try { writer.close(); } catch(Exception ex) { }
        }
    }

    /**
     * Get this silly thing a-goin'
     *
     * @param args
     */
    public static void main(String... args) throws SQLException {
        fixSQL();
    }
}