/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.familysearch.standards.loader.sql.FileResultSet;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class FindTimelineAttributeCountsByJurisdiction {

    private static final String dataDir   = "C:/temp/db-dump";
    private static final String repFile   = "place-rep-all.txt";
    private static final String attrFile  = "attribute-all.txt";
    private static final String dNameFile = "display-name-all.txt";
    private static final String chainFile = "rep-chain-all.txt";

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
        attributeTypes.put(535, "SPECIFIC_TYPE");
        attributeTypes.put(575, "SPORT");
        attributeTypes.put(576, "STORY");
        attributeTypes.put(577, "TRANSPORT");
        attributeTypes.put(545, "TRIVIA");
//        attributeTypes.put(506, "WEB_LINK");
//        attributeTypes.put(474, "WIKIPEDIA_LINK");
        attributeTypes.put(579, "WORK_CHORES");
    }

    private static boolean printHeader = true;


    public static void main(String...args) throws Exception {
        List<Integer> topLevelReps = getTopLevelReps();
        List<Integer> usStateReps  = getUsStateReps();
        Set<Integer> allReps = Stream.concat(topLevelReps.stream(), usStateReps.stream())
                .collect(Collectors.toSet());
        System.out.println("Rep-size: " + allReps.size());
        removeDeletedReps(allReps);
        System.out.println("Rep-size: " + allReps.size());

        Map<Integer, String> repNames = getRepNames(allReps);
        Map<Integer, Map<int[], Integer>> attrCount = getAttributesByRep(allReps);

        for (int repId : topLevelReps) {
            if (repNames.containsKey(repId)) {
                dumpDetails(repId, repNames, attrCount);
            }
        }

        System.out.println("\n\n");
        for (int repId : usStateReps) {
            if (repNames.containsKey(repId)) {
                dumpDetails(repId, repNames, attrCount);
            }
        }
    }

    static List<Integer> getTopLevelReps() throws Exception {
        List<Integer> repIds = new ArrayList<>();

        List<String> chainAll = Files.readAllLines(Paths.get(dataDir, chainFile), StandardCharsets.UTF_8);
        for (String chain : chainAll) {
            String[] chunks = PlaceHelper.split(chain, '|');
            if (chunks.length == 2) {
                String[] juris = PlaceHelper.split(chunks[1], ',');
                if (juris.length == 1) {
                    repIds.add(Integer.parseInt(chunks[0]));
                }
            }
        }

        return repIds;
    }

    static List<Integer> getUsStateReps() throws Exception {
        List<Integer> repIds = new ArrayList<>();

        List<String> chainAll = Files.readAllLines(Paths.get(dataDir, chainFile), StandardCharsets.UTF_8);
        for (String chain : chainAll) {
            String[] chunks = PlaceHelper.split(chain, '|');
            if (chunks.length == 2) {
                String[] juris = PlaceHelper.split(chunks[1], ',');
                if (juris.length == 2  &&  juris[1].equals("1")) {
                    repIds.add(Integer.parseInt(chunks[0]));
                }
            }
        }

        return repIds;
    }

    static void removeDeletedReps(Set<Integer> repIds) throws Exception {
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
    }

    static Map<Integer, String> getRepNames(Set<Integer> repIds) throws Exception {
        Map<Integer, String> repNames = new HashMap<>();

        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(dataDir, dNameFile));

            while (rset.next()) {
                int repId = rset.getInt("rep_id");
                if (repIds.contains(repId)) {
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

        return repNames;
    }
    
    static Map<Integer, Map<int[], Integer>> getAttributesByRep(Set<Integer> allReps) throws Exception {
        Map<Integer, Map<int[], Integer>> attrJunk = new HashMap<>();

        int prevRepId       = 0;
        int prevAttrId      = 0;
        int prevTypeId      = 0;
        int prevFromYr      = 0;
        int prevToYr        = 0;
        boolean prevDelFlag = false;

        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(dataDir, attrFile));

            while (rset.next()) {
                int repId  = rset.getInt("rep_id");
                if (allReps.contains(repId)) {
                    int attrId      = rset.getInt("attr_id");
                    int typeId      = rset.getInt("attr_type_id");
                    int fromYr      = rset.getInt("year");
                    int toYr        = rset.getInt("to_year");
                    boolean delFlag = rset.getBoolean("delete_flag");

                    if (attrId != prevAttrId  &&  ! prevDelFlag) {
                        if (attributeTypes.containsKey(prevTypeId)) {
                            Map<int[], Integer> attrCount = attrJunk.get(prevRepId);
                            if (attrCount == null) {
                                attrCount = new LinkedHashMap<>();
                                attrJunk.put(prevRepId, attrCount);
                                setPeriodRanges(attrCount);
                            }
                            final int fromYear = prevFromYr;
                            final int toYear   = prevToYr;
                            attrCount.replaceAll((k, v) -> overlap(k, fromYear, toYear) ? v+1 : v);
                        }
                    }

                    prevRepId   = repId;
                    prevAttrId  = attrId;
                    prevTypeId  = typeId;
                    prevFromYr  = fromYr;
                    prevToYr    = toYr;
                    prevDelFlag = delFlag;
                }
            }
        }

        if (! prevDelFlag) {
            if (attributeTypes.containsKey(prevTypeId)) {
                Map<int[], Integer> attrCount = attrJunk.get(prevRepId);
                if (attrCount != null) {
                    final int fromYear = prevFromYr;
                    final int toYear   = prevToYr;
                    attrCount.replaceAll((k, v) -> overlap(k, fromYear, toYear) ? v+1 : v);
                }
            }
        }

        return attrJunk;
    }

    static void setPeriodRanges(Map<int[], Integer> attrCount) {
        // Dummy range for attributes with no from/to year
        attrCount.put(new int[] { 0, 0 }, 0);

        // Beginning-of-time to year "0"
        attrCount.put(new int[] { Integer.MIN_VALUE, 0 }, 0);

        // Century ranges from year "1" to year "1800"
        for (int yr=1;  yr<1800;  yr+=100) {
            attrCount.put(new int[] { yr, yr+99 }, 0);
        }

        // Decade ranges from year "1801" to year "2050"
        for (int yr=1801;  yr<2050;  yr+=10) {
            attrCount.put(new int[] { yr, yr+9 }, 0);
        }

        // Year "2051" to end-of-time
        attrCount.put(new int[] { 2050, Integer.MAX_VALUE }, 0);
    }
    
    static boolean overlap(int[] range, int fromYr, int toYr) {
        if (range[0] == 0  &&  range[1] == 0) {
            return fromYr == 0;
        } else if (fromYr == 0) {
            return false;
        }

        if (toYr == 0) {
            return fromYr <= range[1]  &&  fromYr >= range[0];
        } else {
            return fromYr <= range[1]  &&  toYr >= range[0];
        }
    }

    static void dumpDetails(int repId, Map<Integer, String> repNames, Map<Integer, Map<int[], Integer>> attrCount) {
        StringBuilder buff = new StringBuilder();

        buff.append(repId);
        buff.append("|").append(repNames.get(repId));
        Map<int[], Integer> counts = attrCount.get(repId);
        if (counts != null) {
            if (printHeader) {
                printHeader = false;
                StringBuilder hbuff = new StringBuilder();
                hbuff.append("rep-id").append("|").append("name");
                for (int[] ranges : counts.keySet()) {
                    hbuff.append("|").append(ranges[0]).append("-").append(ranges[1]);
                }
                System.out.println(hbuff.toString());
            }
            for (int count : counts.values()) {
                buff.append("|").append(count);
            }
        }

        System.out.println(buff.toString());
    }
}
