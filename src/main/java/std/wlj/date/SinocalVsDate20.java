/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.familysearch.standards.core.lang.dict.Dictionary;
import org.familysearch.standards.core.lang.dict.Word;
import org.familysearch.standards.date.common.ImperialDictionary;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class SinocalVsDate20 {

    static class CJKMeta {
        String type;
        String id;
        String name;
        String locale;
        int    startYr = 0;
        int    endYr   = 0;
    }

    static Map<String, Map<String, Map<String, Integer>>> dynEmpRgnSinoMap = new LinkedHashMap<>();
    static Map<String, Map<String, Map<String, Integer>>> dynEmpRgnDateMap = new LinkedHashMap<>();

    static Map<String, List<CJKMeta>> derAltMap     = new HashMap<>();
    static Map<String, String>        sinoToDateMap = new HashMap<>();

    static Dictionary cjkDict;

    static String NORTH = "北";
    static String SOUTH = "南";
    static String EAST  = "東";
    static String WEST  = "西";

    static String doOnly = null;  // "ming";

    public static void main(String... args) throws Exception {
        cjkDict = ImperialDictionary.getImperialDictionary();
        loadSinoMap();
        loadCJKDynasty();
        mapSinoToCJK();
        dynEmpRgnSinoMap.keySet().forEach(dyn -> printStuff(dyn, sinoToDateMap.get(dyn)));
    }

    static void loadSinoMap() throws Exception {
        List<String> sinoData = Files.readAllLines(Paths.get("C:/temp/sinocal-results-no-date.csv"), StandardCharsets.UTF_8);

        for (String line : sinoData) {
            String[] chunks = PlaceHelper.split(line, ',');
            if (chunks.length > 6) {
                String dyn = chunks[0];
                String emp = chunks[1];
                String rgn = chunks[2];

                String tDate = chunks[chunks.length-1];
                String[] ymd = PlaceHelper.split(tDate, '-');
                String sYear = ymd[0];
                if (sYear.startsWith("'")) {
                    sYear = sYear.substring(1);
                }
                int year = Integer.parseInt(sYear);

                Map<String, Map<String, Integer>> dynData = dynEmpRgnSinoMap.computeIfAbsent(dyn, kk -> new LinkedHashMap<>());
                Map<String, Integer>              empData = dynData.computeIfAbsent(emp, kk -> new LinkedHashMap<>());
                empData.put(rgn, year);
            }
        }
    }

    static void loadCJKDynasty() throws Exception {
        List<Word> dWords = cjkDict.findWords(null, "dynasty", null);

        for (Word dWord : dWords) {
            if (! dWord.getLanguage().toString().startsWith("zh")) {
                continue;
            }

            CJKMeta cjkm = new CJKMeta();
            cjkm.type = "dynasty";
            cjkm.name = dWord.getText();
            cjkm.locale = dWord.getLanguage().toString();
            String[] meta = PlaceHelper.split(dWord.getMetadata(), '|');
            if (meta.length == 3) {
                cjkm.id = meta[0];
                try {
                    cjkm.startYr = Integer.parseInt(meta[1]);
                    cjkm.endYr = Integer.parseInt(meta[2]);
                } catch(NumberFormatException ex) { }
            }

            dynEmpRgnDateMap.put(cjkm.id, new LinkedHashMap<>());
            List<CJKMeta> tList = derAltMap.computeIfAbsent(cjkm.id, kk -> new ArrayList<>());
            tList.add(cjkm);

            loadCJKEmperor(cjkm.id);
        }
    }

    static void loadCJKEmperor(String dynId) throws Exception {
        List<Word> eWords = cjkDict.findWords(null, dynId, null);

        for (Word eWord : eWords) {
            CJKMeta cjkm = new CJKMeta();
            cjkm.type = "emperor";
            cjkm.name = eWord.getText();
            cjkm.locale = eWord.getLanguage().toString();
            String[] meta = PlaceHelper.split(eWord.getMetadata(), '|');
            if (meta.length == 3) {
                cjkm.id = meta[0];
                try {
                    cjkm.startYr = Integer.parseInt(meta[1]);
                    cjkm.endYr = Integer.parseInt(meta[2]);
                } catch(NumberFormatException ex) { }
            }

            Map<String, Map<String, Integer>> dynData = dynEmpRgnDateMap.get(dynId);
            if (dynData != null) {
                Map<String, Integer> empStart = new LinkedHashMap<>();
                empStart.put("", cjkm.startYr);
                dynData.put(cjkm.id, empStart);
            }

            List<CJKMeta> tList = derAltMap.computeIfAbsent(cjkm.id, kk -> new ArrayList<>());
            boolean isNew = tList.stream()
                   .noneMatch(cjk -> cjk.name.equals(cjkm.name));
            if (isNew) {
                tList.add(cjkm);
            }

            loadCJKReign(dynId, cjkm.id);
        }
    }

    static void loadCJKReign(String dynId, String empId) throws Exception {
        List<Word> rWords = cjkDict.findWords(null, empId, null);

        for (Word rWord : rWords) {
            CJKMeta cjkm = new CJKMeta();
            cjkm.type = "reign";
            cjkm.name = rWord.getText();
            cjkm.locale = rWord.getLanguage().toString();
            String[] meta = PlaceHelper.split(rWord.getMetadata(), '|');
            if (meta.length == 3) {
                cjkm.id = meta[0];
                try {
                    cjkm.startYr = Integer.parseInt(meta[1]);
                    cjkm.endYr = Integer.parseInt(meta[2]);
                } catch(NumberFormatException ex) { }
            }

            Map<String, Map<String, Integer>> dynData = dynEmpRgnDateMap.get(dynId);
            if (dynData != null) {
                Map<String, Integer> empStart = dynData.get(empId);
                if (empStart != null) {
                    empStart.put(cjkm.id, cjkm.startYr);
                }
            }

            List<CJKMeta> tList = derAltMap.computeIfAbsent(cjkm.id, kk -> new ArrayList<>());
            boolean isNew = tList.stream()
                   .noneMatch(cjk -> cjk.name.equals(cjkm.name));
            if (isNew) {
                tList.add(cjkm);
            }
        }
    }

    static void mapSinoToCJK() {
        for (Map.Entry<String, Map<String, Map<String, Integer>>> entry : dynEmpRgnSinoMap.entrySet()) {
            String dynName = entry.getKey();
            Set<String> empNames = entry.getValue().keySet();
            int[] range = getRangeDyn(entry.getValue());

            String dynId = findBestMatch(dynName, empNames, range[0], range[1]);
            if (dynId != null) {
                sinoToDateMap.put(dynName, dynId);
            }
        }
    }

    static String findBestMatch(String sinoDynName, Set<String> sinoEmpNames, int sinoStartYr, int sinoEndYr) {
        int    bestCnt = 0;
        String bestId  = null;

        for (Map.Entry<String, Map<String, Map<String, Integer>>> entry : dynEmpRgnDateMap.entrySet()) {
            String dynId = entry.getKey();
            Set<String> empIds = entry.getValue().keySet();
            int[] range = getRangeDyn(entry.getValue());

            boolean isOverlap = sinoStartYr <= range[1]  &&  range[0] <= sinoEndYr;
            boolean dynNameMatch = derAltMap.getOrDefault(dynId, Collections.emptyList()).stream()
                  .anyMatch(meta -> meta.name.equals(sinoDynName)  ||
                                    (NORTH + meta.name).equals(sinoDynName)  ||
                                    (SOUTH + meta.name).equals(sinoDynName)  ||
                                    (EAST  + meta.name).equals(sinoDynName)  ||
                                    (WEST  + meta.name).equals(sinoDynName));
            int empNameMatchCnt = 0;
            for (String empId : empIds) {
                List<CJKMeta> metas = derAltMap.get(empId);
                if (metas != null) {
                    for (CJKMeta meta : metas) {
                        for (String sinoEmpName : sinoEmpNames) {
                            if (meta.name.equals(sinoEmpName)  ||
                                     (NORTH + meta.name).equals(sinoEmpName)  ||
                                     (SOUTH + meta.name).equals(sinoEmpName)  ||
                                     (EAST + meta.name).equals(sinoEmpName)  ||
                                     (WEST + meta.name).equals(sinoEmpName)) {
                                empNameMatchCnt++;
                                break;
                            }
                        }
                    }
                }
            }

            if (isOverlap  &&  empNameMatchCnt > bestCnt) {
                bestCnt = empNameMatchCnt;
                bestId  = dynId;
            }
        }

        return bestId;
    }

    static CJKMeta findMatch(String type, String sinoEmpName, Set<String> empIds) {
        for (String empId : empIds) {
            List<CJKMeta> metas = derAltMap.get(empId);
            if (metas != null) {
                for (CJKMeta meta : metas) {
                    if (meta.type.equals(type)) {
                        if (meta.name.equals(sinoEmpName)  ||
                               (NORTH + meta.name).equals(sinoEmpName)  ||
                               (SOUTH + meta.name).equals(sinoEmpName)  ||
                               (EAST + meta.name).equals(sinoEmpName)  ||
                               (WEST + meta.name).equals(sinoEmpName)) {
                            return meta;
                        }
                    }
                }
            }
        }

        return null;
    }

    static int count = 1;
    static void printStuff(String dynName, String date20Id) {
        if (doOnly != null  &&  ! doOnly.equals(date20Id)) {
            return;
        }

        Map<String, Map<String, Integer>> dynDataS = dynEmpRgnSinoMap.get(dynName);
        Map<String, Map<String, Integer>> dynDataD = dynEmpRgnDateMap.get(date20Id);

        // Dynasty-level stuff
        System.out.println("\n\n");
        StringBuilder buff = new StringBuilder();
        buff.append(dynName);
        buff.append("||||");
        buff.append("|").append(getDate20Name(date20Id, "dynasty"));
        buff.append("|").append(date20Id);
        System.out.println(buff.toString());

        // Emperor-level stuff
        Set<String> foundEmps = new HashSet<>();
        for (Map.Entry<String, Map<String, Integer>> sEntry : dynDataS.entrySet()) {
            buff = new StringBuilder();
            buff.append("|").append(sEntry.getKey());

            int[] range = getRangeEmp(sEntry.getValue());
            buff.append("|").append(range[0]);
            buff.append("||||");

            CJKMeta metaEmp = findMatch("emperor", sEntry.getKey(), dynDataD.keySet());
            if (metaEmp == null) {
                buff.append("|EE???");
                System.out.println(buff.toString());
            } else {
                foundEmps.add(metaEmp.id);
                buff.append(getDate20Name(metaEmp.id, "emperor"));
                buff.append("|").append(metaEmp.startYr);
                if (range[0] != metaEmp.startYr) {
                    buff.append("|||mismatch start year");
                }
                System.out.println(buff.toString());

                // Reign-level stuff
                Set<String> foundReigns = new HashSet<>();
                for (Map.Entry<String, Integer> rEntry : sEntry.getValue().entrySet()) {
                    if (rEntry.getKey().isEmpty()) {
                        continue;
                    }

                    buff = new StringBuilder();
                    buff.append("|||").append(rEntry.getKey());
                    buff.append("|").append(rEntry.getValue());

                    Map<String, Integer> reigns = dynDataD.get(metaEmp.id);
                    CJKMeta metaRgn = findMatch("reign", rEntry.getKey(), reigns.keySet());
                    if (metaRgn == null) {
                        buff.append("|RR???||||missing reign");

                        // Create a "<word>" suitable for defining the reign ...
                        StringBuilder xxx = new StringBuilder();
                        xxx.append("    <word lang=\"zh\" type=\"");
                        xxx.append(metaEmp.id);
                        xxx.append("\" meta=\"");
                        xxx.append(metaEmp.id).append("-A").append(count++);  // New tag ...
                        xxx.append("|").append(rEntry.getValue()).append("|").append(rEntry.getValue()+1);
                        xxx.append("\">").append(rEntry.getKey()).append("</word>");
                        System.out.println(xxx.toString());
                    } else {
                        foundReigns.add(metaEmp.id);
                        buff.append("||||").append(getDate20Name(metaRgn.id, "reign"));
                        buff.append("|").append(metaRgn.startYr);
                        if (rEntry.getValue().intValue() != metaRgn.startYr) {
                            buff.append("|mismatch start year");
                        }
                    }
                    System.out.println(buff.toString());
                }
            }
        }

//        for (Map.Entry<String, Map<String, Integer>> entry : dynDataD.entrySet()) {
//            if (! foundEmps.contains(entry.getKey())) {
//                buff = new StringBuilder();
//                buff.append("||||||").append(getDate20Name(entry.getKey(), "emperor"));
//                buff.append("|").append(getRangeEmp(entry.getValue())[0]);
//                buff.append("|||emperor not matched");
//                System.out.println(buff.toString());
//            }
//        }
    }

    static int[] getRangeDyn(Map<String, Map<String, Integer>> dynData) {
        int startYr = dynData.values().stream()
               .flatMap(ee -> ee.values().stream())
               .mapToInt(v -> v)
               .min().orElse(0);
        int endYr = dynData.values().stream()
               .flatMap(ee -> ee.values().stream())
               .mapToInt(v -> v)
               .max().orElse(0);

        return new int[] { startYr, endYr };
    }

    static int[] getRangeEmp(Map<String, Integer> empData) {
        int startYr = empData.values().stream()
               .mapToInt(v -> v)
               .min().orElse(0);
        int endYr = empData.values().stream()
               .mapToInt(v -> v)
               .max().orElse(0);

        return new int[] { startYr, endYr };
    }

    static String getDate20Name(String id, String type) {
        return derAltMap.getOrDefault(id, Collections.emptyList()).stream()
            .filter(meta -> meta.type.equals(type))
            .map(meta -> meta.name)
            .collect(Collectors.joining(", "));
    }
}
