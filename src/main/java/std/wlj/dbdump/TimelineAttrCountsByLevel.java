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
public class TimelineAttrCountsByLevel {

    private static final String DELIMITER = "\\|";

    private static final String dataDir     = "C:/temp/db-dump";
    private static final String repFile     = "place-rep-all.txt";
    private static final String chainFile   = "rep-chain-all.txt";
    private static final String hhsAttrFile = "place-attr-hhs.txt";

    static final Map<String, Integer> regularCountLevel01 = new TreeMap<>();
    static final Map<String, Integer> regularCountLevel02 = new TreeMap<>();
    static final Map<String, Integer> regularCountLevel0X = new TreeMap<>();
    static final Map<String, Integer> countryCountLevel01 = new TreeMap<>();
    static final Map<String, Integer> countryCountLevel02 = new TreeMap<>();
    static final Map<String, Integer> countryCountLevel0X = new TreeMap<>();

    public static void main(String...args) throws Exception {
        System.out.println("Start the silly thing ...");

        Set<Integer> repsWithAttrs       = getRepsWithAttrs();
        Map<Integer, Integer> repParents = getParentIds(repsWithAttrs);
        Map<Integer, String>  repChains  = getRepChains(repParents);

        countAttributes(repsWithAttrs, repChains);
        Set<String> codes = new TreeSet<>();
        codes.addAll(regularCountLevel01.keySet());
        codes.addAll(regularCountLevel02.keySet());
        codes.addAll(regularCountLevel0X.keySet());
        codes.addAll(countryCountLevel01.keySet());
        codes.addAll(countryCountLevel02.keySet());
        codes.addAll(countryCountLevel0X.keySet());

        int t01 = 0;
        int t02 = 0;
        int t0X = 0;
        for (String code : codes) {
            int r01 = regularCountLevel01.getOrDefault(code, 0);
            int r02 = regularCountLevel02.getOrDefault(code, 0);
            int r0X = regularCountLevel0X.getOrDefault(code, 0);
            int c01 = countryCountLevel01.getOrDefault(code, 0);
            int c02 = countryCountLevel02.getOrDefault(code, 0);
            int c0X = countryCountLevel0X.getOrDefault(code, 0);

            t01 += r01 + c01;
            t02 += r02 + c02;
            t0X += r0X + c0X;

            System.out.println(code + "\t" + (r01 + c01) + "\t" + (r02 + c02) + "\t" + (r0X + c0X));
        }
        System.out.println("TOTAL\t" + t01 + "\t" + t02 + "\t" + t0X);
    }

    static Set<Integer> getRepsWithAttrs() throws Exception {
        System.out.println("getRepsWithAttrs ...");
        Set<Integer> repsWithAttrs = new HashSet<>();

        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(dataDir, hhsAttrFile));

            while (rset.next()) {
                int repId  = rset.getInt("rep_id");
                repsWithAttrs.add(repId);
            }
        }

        System.out.println("getRepsWithAttrs ...");
        System.out.println("    count=" + repsWithAttrs.size());
        return repsWithAttrs;
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

    static void countAttributes(Set<Integer> repsWithAttrs, Map<Integer, String> repChains) throws Exception {
        try (FileResultSet rset = new FileResultSet()) {
            rset.setSeparator(DELIMITER);
            rset.openFile(new File(dataDir, hhsAttrFile));

            while (rset.next()) {
                int      repId  = rset.getInt("rep_id");
                String   code   = rset.getString("code");
                String   cpyrgt = rset.getString("copyright_notice");
                String   chainS = String.valueOf(repChains.getOrDefault(repId, "UNK-CHAIN"));
                String[] chain  = PlaceHelper.split(chainS, ',');

                if (cpyrgt.contains("CountryReports")  ||  cpyrgt.contains("Country Reports")) {
                    if (chain.length == 1) {
                        int cnt = countryCountLevel01.getOrDefault(code, 0);
                        countryCountLevel01.put(code, cnt+1);
                    } else if (chain.length == 2) {
                        int cnt = countryCountLevel02.getOrDefault(code, 0);
                        countryCountLevel02.put(code, cnt+1);
                    } else {
                        int cnt = countryCountLevel0X.getOrDefault(code, 0);
                        countryCountLevel0X.put(code, cnt+1);
                    }
                } else {
                    if (chain.length == 1) {
                        int cnt = regularCountLevel01.getOrDefault(code, 0);
                        regularCountLevel01.put(code, cnt+1);
                    } else if (chain.length == 2) {
                        int cnt = regularCountLevel02.getOrDefault(code, 0);
                        regularCountLevel02.put(code, cnt+1);
                    } else {
                        int cnt = regularCountLevel0X.getOrDefault(code, 0);
                        regularCountLevel0X.put(code, cnt+1);
                    }
                }
            }
        }
    }
}
