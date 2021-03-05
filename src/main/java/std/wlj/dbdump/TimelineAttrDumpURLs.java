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
public class TimelineAttrDumpURLs {

    private static final String dataDir   = "C:/temp/db-dump";
    private static final String repFile   = "place-rep-all.txt";
    private static final String attrFile  = "attribute-all.txt";
    private static final String outFile   = "all-history-url.txt";

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
        dumpAttributeURLs(repsWithAttrs);
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

    static void dumpAttributeURLs(Set<Integer> repsWithAttrs) throws Exception {
        List<String> allAll = new ArrayList<>(repsWithAttrs.size());
        Map<String, Integer> urlHostCount = new TreeMap<>();
        Map<String, Integer> attTypeCount = new TreeMap<>();
        Map<String, Integer> attTypeTotal = new TreeMap<>();

        attributeTypes.values().forEach(att -> attTypeCount.put(att, Integer.valueOf(0)));

        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(dataDir, attrFile));

            while (rset.next()) {
                int     typeId = rset.getInt("attr_type_id");
                boolean delFlg = rset.getBoolean("delete_flag");
                String  url    = rset.getString("attr_url");
                String  typeNm = attributeTypes.getOrDefault(typeId, "Unknown");

                if (attributeTypes.containsKey(typeId)  &&  ! delFlg) {
                    if (url == null  ||  url.length() < 2) {
                        // Update counts
                        int cnt = attTypeTotal.getOrDefault(typeNm, Integer.valueOf(0));
                        attTypeTotal.put(typeNm, cnt+1);
                    } else {
                        int     repId  = rset.getInt("rep_id");
                        int     attrId = rset.getInt("attr_id");
                        String  locale = rset.getString("locale");
                        
                        StringBuilder buff = new StringBuilder();
                        buff.append(repId);
                        buff.append("|").append(attrId);
                        buff.append("|").append(typeId);
                        buff.append("|").append(typeNm);
                        buff.append("|").append(locale);
                        buff.append("|").append(url);
                        
                        allAll.add(buff.toString());

                        // Update counts
                        int cnt = attTypeCount.getOrDefault(typeNm, Integer.valueOf(0));
                        attTypeCount.put(typeNm, cnt+1);
                        
                        String host = getHost(url);
                        cnt = urlHostCount.getOrDefault(host, Integer.valueOf(0));
                        urlHostCount.put(host, cnt+1);
                    }
                }
            }
        }

        Files.write(Paths.get(dataDir, outFile), allAll, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("Record-count: " + allAll.size());

        System.out.println();
        attTypeCount.entrySet().stream().map(ee -> ee.getKey() + "|" + ee.getValue() + "|" + attTypeTotal.getOrDefault(ee.getKey(), 0)).forEach(System.out::println);

        System.out.println();
        urlHostCount.entrySet().stream().map(ee -> ee.getKey() + "|" + ee.getValue()).forEach(System.out::println);
    }

    static String getHost(String url) {
        int ndx0 = url.indexOf("//");
        int ndx1 = url.indexOf("/", ndx0+2);
        return (ndx1 == -1) ? url.substring(ndx0+2).trim() : url.substring(ndx0+2, ndx1).trim();
    }
}
