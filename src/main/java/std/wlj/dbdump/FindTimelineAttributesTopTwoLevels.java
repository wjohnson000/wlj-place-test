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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.familysearch.standards.loader.sql.FileResultSet;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class FindTimelineAttributesTopTwoLevels {

    private static class AttrDetail implements Comparable<AttrDetail> {
        int    repId;
        int    tranId;
        int    year;
        int[]  timePeriod;
        String typeName;
        String value;

        @Override
        public int compareTo(AttrDetail that) {
            return this.year - that.year;
        }
    }

    private static final String dataDir    = "C:/temp/db-dump";
    private static final String repFile    = "place-rep-all.txt";
    private static final String attrFile   = "attribute-all.txt";
    private static final String dNameFile  = "display-name-all.txt";
    private static final String chainFile  = "rep-chain-all.txt";
    private static final String tranxFile  = "transaction-all.txt";
    private static final String resultFile = "attribute-detail-extended.txt";

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

    private static Map<int[], String> transactionDates = new HashMap<>();
    private static List<String> outputDetails = new ArrayList<>(10_000);

    public static void main(String...args) throws Exception {
        Set<Integer> level01Reps = getTopLevelReps();
        Map<Integer, Integer> level02Reps = getSecondLevelReps();

        Set<Integer> allReps = Stream.concat(level01Reps.stream(), level02Reps.keySet().stream())
                .collect(Collectors.toSet());
        System.out.println("Rep-size: " + allReps.size());
        removeDeletedReps(allReps);
        System.out.println("Rep-size: " + allReps.size());

        buildTransactionMap();
        System.out.println("Trx-size: " + transactionDates.size());

        Map<Integer, String> repNames = getRepNames(allReps);
        Map<Integer, List<AttrDetail>> attrDetail = getAttributesByRep(allReps);

        for (Integer rep01Id : level01Reps) {
            dumpDetails(rep01Id, repNames.get(rep01Id), 0, attrDetail);
            for (Map.Entry<Integer, Integer> entry : level02Reps.entrySet()) {
                if (rep01Id.equals(entry.getValue())) {
                    dumpDetails(entry.getKey(), repNames.get(entry.getKey()), rep01Id, attrDetail);
                }
            }
        }

        Files.write(Paths.get(dataDir, resultFile), outputDetails, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    static Set<Integer> getTopLevelReps() throws Exception {
        Set<Integer> repIds = new HashSet<>();

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

    static Map<Integer, Integer> getSecondLevelReps() throws Exception {
        Map<Integer, Integer> repIds = new HashMap<>();

        List<String> chainAll = Files.readAllLines(Paths.get(dataDir, chainFile), StandardCharsets.UTF_8);
        for (String chain : chainAll) {
            String[] chunks = PlaceHelper.split(chain, '|');
            if (chunks.length == 2) {
                String[] juris = PlaceHelper.split(chunks[1], ',');
                if (juris.length == 2) { //  &&  juris[1].equals("1")) {
                    int repId = Integer.parseInt(juris[0]);
                    int parId = Integer.parseInt(juris[1]);
                    repIds.put(repId, parId);
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
    
    static Map<Integer, List<AttrDetail>> getAttributesByRep(Set<Integer> allReps) throws Exception {
        Map<Integer, List<AttrDetail>> attrJunk = new HashMap<>();

        int prevRepId       = 0;
        int prevAttrId      = 0;
        int prevTranId      = 0;
        int prevTypeId      = 0;
        int prevFromYr      = 0;
        int prevToYr        = 0;
        String prevValue    = null;
        boolean prevDelFlag = false;

        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(dataDir, attrFile));

            while (rset.next()) {
                int repId  = rset.getInt("rep_id");
                if (allReps.contains(repId)) {
                    int attrId      = rset.getInt("attr_id");
                    int tranId      = rset.getInt("tran_id");
                    int typeId      = rset.getInt("attr_type_id");
                    int fromYr      = rset.getInt("year");
                    int toYr        = rset.getInt("to_year");
                    String value    = rset.getString("attr_value");
                    boolean delFlag = rset.getBoolean("delete_flag");

                    if (attrId != prevAttrId  &&  ! prevDelFlag  &&  attributeTypes.containsKey(prevTypeId)) {
                        List<AttrDetail> repAttrs = attrJunk.get(prevRepId);
                        if (repAttrs == null) {
                            repAttrs = new ArrayList<>();
                            attrJunk.put(prevRepId, repAttrs);
                        }

                        AttrDetail attrDetail = new AttrDetail();
                        attrDetail.repId = prevRepId;
                        attrDetail.tranId = prevTranId;
                        attrDetail.year = prevFromYr;
                        attrDetail.timePeriod = getPeriodRange(prevFromYr, prevToYr);
                        attrDetail.typeName = attributeTypes.getOrDefault(prevTypeId, String.valueOf(prevTypeId));
                        attrDetail.value = prevValue;
                        repAttrs.add(attrDetail);
                    }

                    prevRepId   = repId;
                    prevTranId  = tranId;
                    prevAttrId  = attrId;
                    prevTypeId  = typeId;
                    prevFromYr  = fromYr;
                    prevToYr    = toYr;
                    prevValue    = value;
                    prevDelFlag = delFlag;
                }
            }
        }

        if (! prevDelFlag  &&  attributeTypes.containsKey(prevTypeId)) {
            List<AttrDetail> repAttrs = attrJunk.get(prevRepId);
            if (repAttrs == null) {
                repAttrs = new ArrayList<>();
                attrJunk.put(prevRepId, repAttrs);
            }

            AttrDetail attrDetail = new AttrDetail();
            attrDetail.repId = prevRepId;
            attrDetail.tranId = prevTranId;
            attrDetail.year = prevFromYr;
            attrDetail.timePeriod = getPeriodRange(prevFromYr, prevToYr);
            attrDetail.typeName = attributeTypes.getOrDefault(prevTypeId, String.valueOf(prevTypeId));
            attrDetail.value = prevValue;
            repAttrs.add(attrDetail);
        }

        return attrJunk;
    }

    static int[] getPeriodRange(int fromYear, int toYear) {
        if (fromYear == 0  &&  toYear == 0) {
            return new int[] { 0, 0 };
        } else if (toYear == 0) {
            if (fromYear < 0) {
                int yr0 = (fromYear / 100) * 100;
                return new int[] { yr0 - 100, yr0 - 1 };
            } else if (fromYear < 1800) {
                int yr0 = (fromYear / 100) * 100;
                return new int[] { yr0, yr0 + 99 };
            } else {
                int yr0 = (fromYear / 10) * 10;
                return new int[] { yr0, yr0 + 9 };
            }
        } else {
            if (fromYear < 0) {
                int yr0 = (fromYear / 100) * 100;
                return new int[] { yr0 - 100, yr0 - 1 };
            } else if (fromYear < 1800) {
                int yr0 = (fromYear / 100) * 100;
                int yrX = (toYear / 100) * 100;
                return new int[] { yr0, yrX + 99 };
            } else {
                int yr0 = (fromYear / 10) * 10;
                int yrX = (toYear / 10) * 10;
                return new int[] { yr0, yrX + 9 };
            }
        }
    }
    
    static void dumpDetails(int repId, String repName, int parId, Map<Integer, List<AttrDetail>> attrDetails) {
        if (attrDetails.containsKey(repId)) {
            outputDetails.add("");
            List<AttrDetail> repDetails = attrDetails.get(repId);
            Collections.sort(repDetails);

            for (AttrDetail attrDetail : repDetails) {
                StringBuilder buff = new StringBuilder();
                buff.append(attrDetail.repId);
                buff.append("|").append(parId);
                buff.append("|").append(repName);
                buff.append("|").append((attrDetail.year == 0) ? "" : String.valueOf(attrDetail.year));
                buff.append("|").append(attrDetail.timePeriod[0]).append("-").append(attrDetail.timePeriod[1]);
                buff.append("|").append(attrDetail.typeName);
                buff.append("|").append(getTranxDate(attrDetail.tranId));
                buff.append("|").append(attrDetail.value);
                outputDetails.add(buff.toString());
            }
        }
    }

    static void buildTransactionMap() throws Exception {
        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(dataDir, tranxFile));

            int prevTranId = 0;
            String prevTranDate = "";

            while (rset.next()) {
                int tranId      = rset.getInt("tran_id");
                String tranDate = rset.getString("create_date");
                if (! tranDate.equals(prevTranDate)) {
                    int[] key = new int[] { prevTranId, tranId-1 };
                    transactionDates.put(key, prevTranDate);

                    prevTranId = tranId;
                    prevTranDate = tranDate;
                }
            }

            int[] key = new int[] { prevTranId, Integer.MAX_VALUE };
            transactionDates.put(key, prevTranDate);
        }
    }

    static String getTranxDate(int tranId) {
        for (Map.Entry<int[], String> entry : transactionDates.entrySet()) {
            int[] key = entry.getKey();
            if (tranId >= key[0]  &&  tranId <= key[1]) {
                return entry.getValue();
            }
        }

        return "Unknown";
    }
}
