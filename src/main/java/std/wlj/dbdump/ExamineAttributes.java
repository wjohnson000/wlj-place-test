/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.familysearch.standards.loader.sql.FileResultSet;

/**
 * @author wjohnson000
 *
 */
public class ExamineAttributes {

    private static final String dataDir    = "C:/temp/db-dump";
    private static final String attrFile   = "attribute-all.txt";
    private static final String DELIMITER  = "\\|";

    private static Map<Integer, String> attributeTypes = new TreeMap<>();
    static {
        attributeTypes.put(554, "ART");
        attributeTypes.put(556, "CLIMATE");
        attributeTypes.put(557, "CLOTHING_FASHION");
        attributeTypes.put(558, "COMMUNICATION");
        attributeTypes.put(559, "CUSTOMS");
        attributeTypes.put(537, "DENOM");
        attributeTypes.put(560, "DISASTERS");
        attributeTypes.put(561, "EDUCATION");
        attributeTypes.put(562, "ENTERTAINMENT");
        attributeTypes.put(546, "ETYMOLOGY");
        attributeTypes.put(563, "FOOD_RECIPES");
        attributeTypes.put(542, "GEOGRAPHIC_INFO");
        attributeTypes.put(541, "HISTORIC_INFO");
        attributeTypes.put(564, "HISTORICAL_MAPS");
        attributeTypes.put(538, "HISTORY");
        attributeTypes.put(565, "HOLIDAYS_CELEBRATIONS");
        attributeTypes.put(507, "INCORP_DATE");
        attributeTypes.put(566, "INVENTIONS_TECH");
        attributeTypes.put(429, "LDS_TMPLST");
        attributeTypes.put(567, "MEDICAL");
        attributeTypes.put(568, "MIGRATIONS");
        attributeTypes.put(569, "MUSIC");
        attributeTypes.put(570, "NEWSPAPERS");
        attributeTypes.put(513, "NOTE");
        attributeTypes.put(571, "OCCUPATIONS");
        attributeTypes.put(553, "PARISH_REGISTER");
        attributeTypes.put(543, "POLITIC_INFO");
        attributeTypes.put(479, "POP");
        attributeTypes.put(573, "POP_CULTURE");
        attributeTypes.put(574, "PROMINENT_PEOPLE");
        attributeTypes.put(581, "RELIG_HISTORY");
        attributeTypes.put(575, "SPORT");
        attributeTypes.put(576, "STORY");
        attributeTypes.put(577, "TRANSPORT");
        attributeTypes.put(545, "TRIVIA");
//        attributeTypes.put(474, "WIKIPEDIA_LINK");
        attributeTypes.put(579, "WORK_CHORES");
    }

    private static boolean doMore = true;
    private static int     count  = 0;

    public static void main(String... args) throws Exception {
        int doType = 571;

        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(dataDir, attrFile));

            while (rset.next()  &&  doMore) {
                int     typeId = rset.getInt("attr_type_id");
                boolean isDel  = rset.getBoolean("delete_flag");
                if (! isDel  &&  attributeTypes.containsKey(typeId)) {
                    dumpEvent(doType, rset);
                }
            }
        }

        System.out.println("COUNT-TOTAL: " + count);
        System.exit(0);
    }

    static void dumpEvent(int targetType, FileResultSet rset) throws Exception {
        int     typeId = rset.getInt("attr_type_id");
        int     repId  = rset.getInt("rep_id");
        int     frYear = rset.getInt("year");
        int     toYear = rset.getInt("to_year");
        String  locale = rset.getString("locale");
        String  value  = rset.getString("attr_value");
        String  title  = rset.getString("title");
        String  url    = rset.getString("attr_url");
        String  urlTtl = rset.getString("attr_title");
        String  cpyNtc = rset.getString("copyright_notice");
        String  cpyUrl = rset.getString("copyright_url");
        boolean isDel  = rset.getBoolean("delete_flag");

        if (targetType == typeId) {
            System.out.println("=========================================================================");
            System.out.println("Dell? " + isDel);
            System.out.println("Repp: " + repId);
            System.out.println("Type: " + typeId + " --> " + attributeTypes.get(typeId));
            System.out.println("Year: " + frYear + " --> " + toYear);
            System.out.println("Lcle: " + locale);
            System.out.println("Valu: " + value);
            System.out.println("Titl: " + title);
            System.out.println("Urll: " + urlTtl + " --> " + url);
            System.out.println("Cypr: " + cpyNtc + " --> " + cpyUrl);
        }
    }
}
