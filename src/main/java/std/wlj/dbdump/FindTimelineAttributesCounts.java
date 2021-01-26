/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.dbdump;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.familysearch.standards.loader.sql.FileResultSet;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * Find the number of attributes of each type by:
 *   -- country level
 *   -- second level (state, province)
 *   -- total
 *   -- each of the above by locale (language)
 * @author wjohnson000
 *
 */
public class FindTimelineAttributesCounts {

    private static class RepAttrCount {
        String level01Id;
        String level02Id;
        String level03Id;
        String name;
        Map<String, Map<String, Integer>> typeByLang = new TreeMap<>();
    }

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

        Map<Integer, Integer> repParents = getParentIds(repsWithAttrs);
        Map<Integer, String>  repNames   = getRepNames(repsWithAttrs);
        Map<Integer, String>  repChains  = getRepChains(repParents);

        Map<String, RepAttrCount> thingMap = countAttributes(repsWithAttrs, repNames, repChains);
        System.out.println("TM: " + thingMap.size());
        long count1 = thingMap.values().stream()
                                       .filter(rac -> rac.level02Id == null)
                                       .count();
        long count2 = thingMap.values().stream()
                                       .filter(rac -> rac.level02Id != null  &&  rac.level03Id == null)
                                       .count();
        long count3 = thingMap.values().stream()
                                       .filter(rac -> rac.level03Id != null)
                                       .count();
        System.out.println("L01: " + count1);
        System.out.println("L02: " + count2);
        System.out.println("L03: " + count3);
        System.out.println();
        thingMap.values().stream().forEach(rac -> printIt(rac));
    }

    static void printIt(RepAttrCount rac) {
        for (Map.Entry<String, Map<String, Integer>> tentry : rac.typeByLang.entrySet()) {
            String type = tentry.getKey();
            for (Map.Entry<String, Integer> lentry : tentry.getValue().entrySet()) {
                System.out.print(rac.level01Id);
                System.out.print("|");
                System.out.print(rac.level02Id == null ? "" : rac.level02Id);
                System.out.print("|");
                System.out.print(rac.level03Id == null ? "" : rac.level03Id);
                System.out.print("|");
                System.out.print(rac.name);
                System.out.print("|");
                System.out.print(type);
                System.out.print("|");
                System.out.print(lentry.getKey());
                System.out.print("|");
                System.out.print(lentry.getValue());
                System.out.println();
            }
        }
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

        System.out.println("getRepsWithAttrs ...");
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

    static Map<Integer, Integer> getParentIds(Set<Integer> repIds) throws Exception {
        System.out.println("retrieveParentRepIds ...");
        Map<Integer, Integer> parIds = new HashMap<>();

        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(dataDir, repFile));

            while(rset.next()) {
                int repId = rset.getInt("rep_id");
                int parId = rset.getInt("parent_id");
                if (repIds.contains(repId)) {
                    parIds.put(repId, parId);
                }
            }
        }

        System.out.println("    count=" + parIds.size());
        return parIds;
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

    static Map<Integer, String> getRepChains(Map<Integer, Integer> repParents) throws IOException {
        System.out.println("getRepChains ...");
        Map<Integer, String> repChains = new HashMap<>();

        Set<Integer> uniqueParents = repParents.values().stream().collect(Collectors.toSet());

        List<String> chainAll = Files.readAllLines(Paths.get(dataDir, chainFile), StandardCharsets.UTF_8);
        for (String chain : chainAll) {
            String[] chunks = PlaceHelper.split(chain, '|');
            if (chunks.length == 2) {
                if (chunks[0].equals(chunks[1])) {
                    int repId = Integer.parseInt(chunks[0]);
                    if (repParents.containsKey(repId)  ||  uniqueParents.contains(repId)) {
                        repChains.put(repId, chunks[1]);
                    }
                } else {
                    int repId = Integer.parseInt(chunks[0]);
                    repChains.put(repId, chunks[1]);
                }
            }
        }
        System.out.println("    count=" + repChains.size());

        for (Map.Entry<Integer, Integer> entry : repParents.entrySet()) {
            int repId = entry.getKey();
            if (! repChains.containsKey(repId)) {
                String parChain = repChains.get(entry.getValue());
                if (parChain != null) {
                    parChain = repId + "," + parChain;
                    repChains.put(repId, parChain);
                }
            }
        }
        System.out.println("    count=" + repChains.size());

        return repChains;
    }

    static Map<String, RepAttrCount> countAttributes(Set<Integer> repsWithAttrs, Map<Integer, String> repNames, Map<Integer, String> repChains) throws Exception {
        Map<String, RepAttrCount> results = new HashMap<>();
        Set<Integer> usedAttrIds = new HashSet<>(300_000);

        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(dataDir, attrFile));

            while (rset.next()) {
                int     repId  = rset.getInt("rep_id");
                int     attrId = rset.getInt("attr_id");
                int     typeId = rset.getInt("attr_type_id");
                String  locale = rset.getString("locale");
                boolean delFlg = rset.getBoolean("delete_flag");

                if (usedAttrIds.contains(attrId)) {
                    if (delFlg) {
                        String   chainS = String.valueOf(repChains.getOrDefault(repId, "UNK-CHAIN"));
                        String[] chain  = PlaceHelper.split(chainS, ',');

                        String level01 = chain[chain.length-1];
                        String level02 = chain.length == 1 ? null : ("L2-" + chain[chain.length-1]);
                        String level03 = chain.length <= 2 ? null : ("L3-" + chain[chain.length-1]);

                        String key = (level03 != null) ? level03 : (level02 != null) ? level02 : level01;
                        RepAttrCount rac = results.get(key);
                        if (rac != null) {
                            String attrType = attributeTypes.getOrDefault(typeId, "UNK-TYPE");
                            Map<String, Integer> attrCount = rac.typeByLang.get(attrType);
                            if (attrCount != null) {
                                Integer count = attrCount.getOrDefault(locale, new Integer(0));
                                if (count > 0) {
                                    attrCount.put(locale, count-1);
                                }
                            }
                        }
                    }
                } else if (attributeTypes.containsKey(typeId)  &&  ! delFlg  &&  repNames.containsKey(repId)) {
                    usedAttrIds.add(attrId);
                    String   chainS = String.valueOf(repChains.getOrDefault(repId, "UNK-CHAIN"));
                    String[] chain  = PlaceHelper.split(chainS, ',');

                    String level01 = chain[chain.length-1];
                    String level02 = chain.length == 1 ? null : ("L2-" + chain[chain.length-1]);
                    String level03 = chain.length <= 2 ? null : ("L3-" + chain[chain.length-1]);

                    String key = (level03 != null) ? level03 : (level02 != null) ? level02 : level01;
                    RepAttrCount rac = results.get(key);
                    if (rac == null) {
                        rac = new RepAttrCount();
                        rac.level01Id = level01;
                        rac.level02Id = level02;
                        rac.level03Id = level03;
                        rac.name = repNames.getOrDefault(repId, "UNK-NAME");
                        results.put(key, rac);
                    }

                    String attrType = attributeTypes.getOrDefault(typeId, "UNK-TYPE");
                    Map<String, Integer> attrCount = rac.typeByLang.computeIfAbsent(attrType, kk -> new TreeMap<>());
                    Integer count = attrCount.getOrDefault(locale, new Integer(0));
                    attrCount.put(locale, count+1);
                }
            }
        }

        return results;
    }
}
