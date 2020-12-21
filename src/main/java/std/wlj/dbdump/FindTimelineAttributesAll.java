/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import org.familysearch.standards.loader.sql.FileResultSet;

/**
 * @author wjohnson000
 *
 */
public class FindTimelineAttributesAll {

    private static final String dataDir   = "C:/temp/db-dump";
    private static final String repFile   = "place-rep-all.txt";
    private static final String attrFile  = "attribute-all.txt";
    private static final String dNameFile = "display-name-all.txt";
    private static final String chainFile = "rep-chain-all.txt";
    private static final String outFile   = "all-history-attr.txt";

    private static final String DELIMITER = "\\|";

    private static Map<Integer, String> attributeTypes = new TreeMap<>();
    static {
        attributeTypes.put(554, "ART");
//        attributeTypes.put(552, "BISHOP_TRANSCRIPT");
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
//        attributeTypes.put(433, "FS_REG");
//        attributeTypes.put(504, "FS_WIKI_LINK");
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
//        attributeTypes.put(422, "NGA_ELEV");
        attributeTypes.put(513, "NOTE");
        attributeTypes.put(571, "OCCUPATIONS");
        attributeTypes.put(553, "PARISH_REGISTER");
        attributeTypes.put(543, "POLITIC_INFO");
        attributeTypes.put(479, "POP");
        attributeTypes.put(573, "POP_CULTURE");
        attributeTypes.put(574, "PROMINENT_PEOPLE");
        attributeTypes.put(581, "RELIG_HISTORY");
//        attributeTypes.put(535, "SPECIFIC_TYPE");
        attributeTypes.put(575, "SPORT");
        attributeTypes.put(576, "STORY");
        attributeTypes.put(577, "TRANSPORT");
        attributeTypes.put(545, "TRIVIA");
//        attributeTypes.put(506, "WEB_LINK");
//        attributeTypes.put(474, "WIKIPEDIA_LINK");
        attributeTypes.put(579, "WORK_CHORES");
    }


    public static void main(String...args) throws Exception {
        System.out.println("Start the silly thing ...");

        Set<Integer> repsWithAttrs = getRepsWithAttrs();
        removeDeletedReps(repsWithAttrs);

        Map<Integer, String> repNames  = getRepNames(repsWithAttrs);
        Map<Integer, String> repChains = getRepChains(repsWithAttrs);

        dumpAttributes(repsWithAttrs, repNames, repChains);
    }

    static Set<Integer> getRepsWithAttrs() throws Exception {
        System.out.println("getRepsWithAttrs ...");
        Set<Integer> repsWithAttrs = new HashSet<>();

        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(dataDir, attrFile));

            while (rset.next()) {
                int repId  = rset.getInt("rep_id");
                int typeId = rset.getInt("attr_type_id");

                if (attributeTypes.containsKey(typeId)) {
                    repsWithAttrs.add(repId);
                }
            }
        }

        System.out.println("    count=" + repsWithAttrs.size());
        return repsWithAttrs;
    }

    static void removeDeletedReps(Set<Integer> repIds) throws Exception {
        System.out.println("removeDeletedReps ...");

        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(dataDir, repFile));

            while(rset.next()) {
                int repId = rset.getInt("rep_id");
                int delId = rset.getInt("delete_id");
                if (delId > 0) {
                    repIds.remove(repId);
                }
            }
        }

        System.out.println("    count=" + repIds.size());
    }

    static Map<Integer, String> getRepNames(Set<Integer> repsWithAttrs) throws Exception {
        System.out.println("getRepNames ...");
        Map<Integer, String> repNames = new HashMap<>();

        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(dataDir, dNameFile));

            while (rset.next()) {
                int repId = rset.getInt("rep_id");
                if (repsWithAttrs.contains(repId)) {
                    String locale = rset.getString("locale");
                    String text   = rset.getString("text");
                    if (! repNames.containsKey(repId)) {
                        repNames.put(repId, text);
                    } else if (locale.equals("en")) {
                        repNames.put(repId, text);
                    }
                }
            }
        }

        System.out.println("    count=" + repNames.size());
        return repNames;
    }

    static Map<Integer, String> getRepChains(Set<Integer> repsWithAttrs) {
        System.out.println("getRepChains ...");
        Map<Integer, String> repChains = new HashMap<>();

        System.out.println("    count=" + repChains.size());
        return repChains;
    }

    static void dumpAttributes(Set<Integer> repsWithAttrs, Map<Integer, String> repNames, Map<Integer, String> repChains) throws Exception {
        List<String> allAll = new ArrayList<>(repsWithAttrs.size());
        Map<String, String> typeData = DumpTypes.loadAllTypes();

        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(dataDir, attrFile));

            while (rset.next()) {
                int typeId = rset.getInt("attr_type_id");
                boolean delFlg = rset.getBoolean("delete_flag");

                if (attributeTypes.containsKey(typeId)  &&  ! delFlg) {
                    int     repId  = rset.getInt("rep_id");
                    int     attrId = rset.getInt("attr_id");
                    String  frYear = rset.getString("year");
                    String  toYear = rset.getString("to_year");
                    String  locale = rset.getString("locale");
                    String  value  = rset.getString("attr_value");
                    String  title  = rset.getString("title");
                    String  url    = rset.getString("attr_url");
                    String  urlTtl = rset.getString("attr_title");
                    String  cpyNtc = rset.getString("copyright_notice");
                    String  cpyUrl = rset.getString("copyright_url");

                    StringBuilder buff = new StringBuilder();
                    buff.append(repId);
                    buff.append("|").append(repNames.getOrDefault(repId, "Unknown"));
                    buff.append("|").append(attrId);
                    buff.append("|").append(typeId);
                    buff.append("|").append(typeData.getOrDefault(String.valueOf(typeId), "Unknown"));
                    buff.append("|").append(frYear);
                    buff.append("|").append(toYear);
                    buff.append("|").append(locale);
                    buff.append("|").append(value);
                    buff.append("|").append(title);
                    buff.append("|").append(urlTtl);
                    buff.append("|").append(url);
                    buff.append("|").append(cpyNtc);
                    buff.append("|").append(cpyUrl);

                    allAll.add(buff.toString());
                }
            }
        }

        Files.write(Paths.get(dataDir, outFile), allAll, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("Record-count: " + allAll.size());
    }

}
