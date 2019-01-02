package std.wlj.jira;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import org.familysearch.standards.place.util.PlaceHelper;

import std.wlj.dbdump.DumpTypes;

/**
 * Look for place-reps tied to the same place that have overlapping from/to dates ...
 * 
 * @author wjohnson000
 *
 */
public class FindPossibleDateIssues {

    private static class RepData implements Comparable<RepData> {
        int ownerId;
        int repId;
        int typeId;
        String prefLocale;
        int fromYr;
        int toYr;
        String pubFlag;
        String valFlag;
        String name;

        @Override public int compareTo(RepData that) {
            int compare = this.fromYr - that.fromYr;
            if (compare == 0) compare = this.toYr - that.toYr;
            return compare;
        }
    }

    static final String baseDir = "C:/temp";
    static final String repFile = "db-place-rep-all.txt";
    static final String namFile = "db-display-name-all.txt";
    static final String outFile = "rep-overlaps.txt";

    static       int conflictCnt = 0;
    static       Map<String, String> typeDetails;
    static       List<String> outDetails = new ArrayList<>(164_000);
    static       List<RepData> conflicts = new ArrayList<>(164_000);

    public static void main(String...arsg) throws IOException {
        typeDetails = DumpTypes.loadPlaceTypes();
        Set<String> multiOwners = getOwnersWithMultipleReps();
        System.out.println("Number of owners: " + multiOwners.size());

        Map<String, List<RepData>> repDetails = getRepsForMultiOwners(multiOwners);
        repDetails.values().forEach(repD -> compareReps(repD));
        addNames();
        prepareReps();

        Files.write(Paths.get(baseDir, outFile), outDetails, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
        System.out.println("\nUnfortunate-count: " + conflictCnt);
    }

    static Set<String> getOwnersWithMultipleReps() throws IOException {
        Map<String, Integer> owners = new HashMap<>();

        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("R.Lines.01: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 11) {
                    String ownerId = chunks[3];
                    String deleteId = chunks[9];
                    if (deleteId.trim().isEmpty()  ||  "null".equalsIgnoreCase(deleteId.trim())) {
                        Integer count  = owners.getOrDefault(ownerId, 0);
                        owners.put(ownerId, count+1);
                    }
                }
            }
        }

        return owners.entrySet().stream()
            .filter(entry -> entry.getValue() > 1)
            .map(entry -> entry.getKey())
            .collect(Collectors.toSet());
    }

    static Map<String, List<RepData>> getRepsForMultiOwners(Set<String> multiOwners) throws IOException {
        Map<String, List<RepData>> repDetails = new HashMap<>();

        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("R.Lines.02: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 12) {
                    int repId   = Integer.parseInt(chunks[0].trim());
                    int ownerId = Integer.parseInt(chunks[3].trim());
                    int typeId  = Integer.parseInt(chunks[6].trim());
                    int fromYr  = (chunks[7].trim().isEmpty()  ||  "null".equals(chunks[7].trim())) ? Integer.MIN_VALUE : Integer.parseInt(chunks[7].trim());
                    int toYr    = (chunks[8].trim().isEmpty()  ||  "null".equals(chunks[8].trim())) ? Integer.MAX_VALUE : Integer.parseInt(chunks[8].trim());
                    String deleteId = chunks[9];

                    if (deleteId.trim().isEmpty()  ||  "null".equalsIgnoreCase(deleteId.trim())) {
                        RepData repDataX = new RepData();
                        repDataX.ownerId = ownerId;
                        repDataX.repId   = repId;
                        repDataX.typeId  = typeId;
                        repDataX.fromYr  = fromYr;
                        repDataX.toYr    = toYr;
                        repDataX.prefLocale = chunks[10];
                        repDataX.pubFlag = chunks[11];
                        repDataX.valFlag = chunks[12];
                        
                        List<RepData> repDataList = repDetails.get(chunks[3]);
                        if (repDataList == null) {
                            repDataList = new ArrayList<>(3);
                            repDetails.put(chunks[3], repDataList);
                        }
                        repDataList.add(repDataX);
                    }
                }
            }
        }

        return repDetails;
    }

    static void compareReps(List<RepData> reps) {
        Collections.sort(reps);
        int overlap = 0;
        for (int i=0;  i<reps.size()-1;  i++) {
            overlap = Math.max(overlap, getOverlap(reps.get(i), reps.get(i+1)));
        }

        if (overlap >= 2) {
            conflictCnt++;
            conflicts.addAll(reps);
            conflicts.add(null);
        }
    }

    static void addNames() throws IOException {
        Set<Integer> repIds = conflicts.stream().filter(rep -> rep != null).map(rep -> rep.repId).collect(Collectors.toSet());

        Map<Integer, String> repNames = new HashMap<>();

        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, namFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("N.Lines.01: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 5) {
                    int repId   = Integer.parseInt(chunks[0].trim());
                    String locale = chunks[2];
                    String text   = chunks[3];
                    if (repIds.contains(repId)  &&  (! repNames.containsKey(repId)  ||  "en".equalsIgnoreCase(locale))) {
                        repNames.put(repId, text);
                    }
                }
            }
        }

        conflicts.stream().filter(rep -> rep != null).forEach(rep -> rep.name = repNames.get(rep.repId));
    }

    static void prepareReps() {
        List<String> blah = conflicts.stream()
                .map(rep -> (rep == null) ?
                                ""        :
                                (rep.ownerId + "|" + rep.repId + "|" + rep.typeId + "|" + typeDetails.get(String.valueOf(rep.typeId))
                                             + "|" + rep.prefLocale + "|" + rep.name
                                             + "|" + (rep.fromYr == Integer.MIN_VALUE ? "" : rep.fromYr)
                                             + "|" + (rep.toYr == Integer.MAX_VALUE ? "" : rep.toYr)
                                             + "|" + rep.pubFlag.toUpperCase() + "|" + rep.valFlag.toUpperCase()))
                .collect(Collectors.toList());

        blah.forEach(System.out::println);
        System.out.println();

        outDetails.addAll(blah);
        outDetails.add("");
    }

    static int getOverlap(RepData repData1, RepData repData2) {
        if (repData2.fromYr < -10_000) {
            return 0;
        } else {
            return repData1.toYr - repData2.fromYr;
        }
    }
}
