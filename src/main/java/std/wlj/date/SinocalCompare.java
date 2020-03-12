/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.date;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import org.familysearch.standards.core.lang.dict.Dictionary;
import org.familysearch.standards.core.lang.dict.Word;
import org.familysearch.standards.date.common.ImperialDictionary;
import org.familysearch.standards.place.util.PlaceHelper;

/**
 * @author wjohnson000
 *
 */
public class SinocalCompare {

    static class Triad {
        List<ImperialDictionary.Dynasty> dynasty;
        List<ImperialDictionary.Emperor> emperor;
        List<ImperialDictionary.Reign>   reign;
    }

    static Dictionary                cjkDictionary  = ImperialDictionary.getImperialDictionary();
    static Map<String, List<String>> sinocalResults = new LinkedHashMap<>();
    static Map<String, String[]>     sinocalDate    = new HashMap<>();
    static Map<String, String[]>     sinocalDER     = new HashMap<>();
    static Map<String, List<String>> date20Results  = new LinkedHashMap<>();
    static Set<String>               cjkNSEW        = new HashSet<>();

    static {
        cjkNSEW.add("北");  // North
        cjkNSEW.add("南");  // South
        cjkNSEW.add("東");  // East
        cjkNSEW.add("西");  // West
    }

    public static void main(String... args) throws Exception {
        System.out.println(cjkNSEW);

        loadSinocalDynEmpRgn();
        loadSinocalResults();
        loadDate20Results();
        compareResults();
    }

    static void loadSinocalDynEmpRgn() throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("C:/temp/sinocal-input-batch.csv"), StandardCharsets.UTF_8);

        for (String line : lines) {
            String[] chunks = PlaceHelper.split(line, ',');
            if (chunks.length > 2) {
                String   cjk = chunks[0] + chunks[1] + chunks[2];
                String[] dynEmpRgn = new String[] { chunks[0], chunks[1], chunks[2] };
                sinocalDER.put(cjk, dynEmpRgn);
            }
        }
    }

    static void loadSinocalResults() throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("C:/temp/sinocal-results.csv"), StandardCharsets.UTF_8);
        for (String line : lines) {
            String[] chunks = PlaceHelper.split(line, ',');
            if (chunks.length > 5) {
                String cjk = chunks[0] + chunks[1] + chunks[2];
                String[] ymdInput = new String[] { chunks[3], chunks[4], chunks[5] };

                String tDate = chunks[chunks.length-1];
                String[] ymd = PlaceHelper.split(tDate, '-');
                if (ymd[0].startsWith("'")) {
                    ymd[0] = ymd[0].substring(1);
                }
                while (ymd[0].length() < 4) {
                    ymd[0] = "0" + ymd[0];
                }
                if (ymd[1].length() == 1) {
                    ymd[1] = "0" + ymd[1];
                }
                if (ymd[2].length() == 1) {
                    ymd[2] = "0" + ymd[2];
                }
                String date = "+" + ymd[0] + "-" + ymd[1] + "-" + ymd[2];
                sinocalResults.put(cjk, Arrays.asList(date));
                sinocalDate.put(cjk, ymdInput);
            }
        }
    }

    static void loadDate20Results() throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("C:/temp/sinocal-results-date20.csv"), StandardCharsets.UTF_8);
        for (String line : lines) {
            String[] chunks = PlaceHelper.split(line, '|');
            if (chunks.length == 2) {
                String cjk = chunks[0];
                int ndx0 = cjk.indexOf(' ');
                if (ndx0 > -1) {
                    cjk = cjk.substring(0, ndx0).trim();
                }

                List<String> results = date20Results.computeIfAbsent(cjk, ky -> new ArrayList<String>());
                results.add(chunks[1]);
            }
        } 
    }

    static void compareResults() {
        for (Map.Entry<String, List<String>> sinocalEntry : sinocalResults.entrySet()) {
            List<String> res01 = sinocalEntry.getValue();
            List<String> res02 = date20Results.getOrDefault(sinocalEntry.getKey(), Arrays.asList(""));
            String[] dynEmpRgn = sinocalDER.getOrDefault(sinocalEntry.getKey(), new String[] { "", "", "" });
            String[] ymdInput = sinocalDate.get(sinocalEntry.getKey());

            System.out.println("");
            boolean first = true;
            for (String res : res02) {
                int year = 1;
                try {
                    year = Integer.parseInt(res.substring(1, 5));
                } catch(Exception ex) { } 

                Triad match = matchDynEmpRgn(dynEmpRgn[0], dynEmpRgn[1], dynEmpRgn[2], year);
                Set<String> date20DynNames = getDynNames(match.dynasty);
                Set<String> date20EmpNames = getEmpNames(match.emperor);
                Set<String> date20RgnNames = getRgnNames(match.reign);

                String date20DynName = date20DynNames.stream().collect(Collectors.joining(", "));
                String date20EmpName = date20EmpNames.stream().collect(Collectors.joining(", "));
                String date20RgnName = date20RgnNames.stream().collect(Collectors.joining(", "));

                String cjkMatch = "";
                if (date20DynNames.contains(dynEmpRgn[0])) {
                    cjkMatch = "dynasty";
                } else {
                    for (String dynName : date20DynNames) {
                        for (String nsew : cjkNSEW) {
                            if ((nsew + dynName).equals(dynEmpRgn[0])) {
                                cjkMatch = "(dynasty)";
                            }
                        }
                    }
                }
                if (date20EmpNames.contains(dynEmpRgn[1])) {
                    cjkMatch += " emperor";
                }
                if (date20RgnNames.contains(dynEmpRgn[2])) {
                    cjkMatch += " reign";
                }
                cjkMatch = cjkMatch.trim();

                if (first) {
                    first = false;
                    String dateMismatch = res01.get(0).equals(res) ? "" : "mismatch";
                    System.out.println(
                           sinocalEntry.getKey() + "|" + dynEmpRgn[0] + "|" + dynEmpRgn[1] + "|" + dynEmpRgn[2] + "|" +
                           ymdInput[0] + "年 " + ymdInput[1] + "月 " + ymdInput[0] + "日" + "|" + res01.get(0) + "|" + 
                           date20DynName + "|" + date20EmpName + "|" + date20RgnName + "|" + res + "|" + cjkMatch + "|" + dateMismatch);
                } else {
                    System.out.println("||||||" + date20DynName + "|" + date20EmpName + "|" + date20RgnName + "|" + res + "|" + cjkMatch);
                }
            }
        }
    }

    static Triad matchDynEmpRgn(String dynName, String empName, String rgnName, int year) {
        List<Word> rWords = cjkDictionary.findWords(rgnName, "reign", null);
        if (! rWords.isEmpty()) {
            List<ImperialDictionary.Reign> rgns = rWords.stream()
                .map(ww -> getName(ww.getMetadata()))
                .map(nn -> ImperialDictionary.lookupReign(nn))
//                .flatMap(ss -> ss.stream())
                .filter(rr -> rr.getStartYear()-5 <= year  &&  rr.getEndYear()+5 >= year)
                .collect(Collectors.toList());

            if (rgns.size() > 0) {
                Triad ttt = new Triad();
                ttt.dynasty = rgns.stream().map(rr -> rr.getDynasty()).collect(Collectors.toList());
                ttt.emperor = rgns.stream().map(rr -> rr.getEmperor()).collect(Collectors.toList());
                ttt.reign   = rgns;
                return ttt;
            }
        }

        List<Word> eWords = cjkDictionary.findWords(empName, "emperor", null);
        if (! eWords.isEmpty()) {
            List<ImperialDictionary.Emperor> emps = eWords.stream()
                .map(ww -> getName(ww.getMetadata()))
                .map(nn -> ImperialDictionary.lookupEmperor(nn))
                .filter(ee -> ee.getStartYear()-5 <= year  &&  ee.getEndYear()+5 >= year)
                .collect(Collectors.toList());

            if (emps.size() == 1) {
                Triad ttt = new Triad();
                ttt.dynasty = emps.stream().map(ee -> ee.getDynasty()).collect(Collectors.toList());
                ttt.emperor = emps;
                ttt.reign   = null;
                return ttt;
            }
        }

        List<Word> dWords = cjkDictionary.findWords(dynName, "dynasty", null);
        if (! dWords.isEmpty()) {
            List<ImperialDictionary.Dynasty> dyns = dWords.stream()
                .map(ww -> getName(ww.getMetadata()))
                .map(nn -> ImperialDictionary.lookupDynasty(nn))
                .filter(dd -> dd.getStartYear()-5 <= year  &&  dd.getEndYear()+5 >= year)
                .collect(Collectors.toList());

            if (dyns.size() == 1) {
                Triad ttt = new Triad();
                ttt.dynasty = dyns;
                ttt.emperor = null;
                ttt.reign   = null;
                return ttt;
            }
        }

        return new Triad();
    }

    static String getName(String metadata) {
        int ndx = metadata.indexOf('|');
        return (ndx < 0) ? metadata : metadata.substring(0, ndx).trim();
    }

    static Set<String> getDynNames(List<ImperialDictionary.Dynasty> dynasties) {
        return (dynasties == null) ?
              Collections.emptySet() :
              dynasties.stream().map(dd -> dd.getName()).collect(Collectors.toSet());
    }

    static Set<String> getEmpNames(List<ImperialDictionary.Emperor> dynasties) {
        return (dynasties == null) ?
              Collections.emptySet() :
              dynasties.stream().map(dd -> dd.getName()).collect(Collectors.toSet());
    }

    static Set<String> getRgnNames(List<ImperialDictionary.Reign> dynasties) {
        return (dynasties == null) ?
              Collections.emptySet() :
              dynasties.stream().map(dd -> dd.getName()).collect(Collectors.toSet());
    }
}
