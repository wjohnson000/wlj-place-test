package std.wlj.jira;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;

import org.familysearch.standards.place.util.PlaceHelper;

import std.wlj.dbdump.DumpTypes;

public class STD_132116_CitationsToDelete {

    static final String baseDir  = "C:/temp/db-dump";
    static final String repFile  = "place-rep-all.txt";
    static final String nameFile = "display-name-all.txt";
    static final String citnFile = "citation-all.txt";
    static final String outFile  = "citation-delete.sql";

    static final Map<String, String> typeDetail = DumpTypes.loadAllTypes();

    public static void main(String... args) throws IOException {
        Map<Integer, Boolean> repIsLive  = new TreeMap<>();
        Map<Integer, Boolean> repCitnOK  = new HashMap<>();
        Map<Integer, String>  repToName  = new HashMap<>();
        Map<Integer, String>  repToType  = new HashMap<>();

        // Step 01 -- Read in place-reps and store the "is-published" flag
        int lineCnt = 0;
        System.out.println();
        try(FileInputStream fis = new FileInputStream(new File(baseDir, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {
            scan.nextLine();  // Skip the header line

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("REP.read: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 11) {
                    String repId  = chunks[0];
                    String typeId = chunks[6];
                    String delId  = chunks[9];
                    int    repIdx = Integer.parseInt(repId);

                    try {
                        Integer.parseInt(delId);
                        repIsLive.put(repIdx, false);
                    } catch(NumberFormatException ex) {
                        repIsLive.put(repIdx, true);
                        repToType.put(repIdx, typeId);
                    }
                }
            }
        }

        // Step 02 -- read citations and remove reps that have at least one citation
        lineCnt = 0;
        System.out.println();
        try(FileInputStream fis = new FileInputStream(new File(baseDir, citnFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {
            scan.nextLine();  // Skip the header line

            while (scan.hasNextLine()) {
                if (++lineCnt % 1_000_000 == 0) System.out.println("CITN.read: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                String repId  = chunks[0];
                int    repIdx = Integer.parseInt(repId);

                if (repIsLive.containsKey(repIdx)) {
                    if (keepCitn(chunks)) {
                        repCitnOK.put(repIdx, true);
                    } else {
                        generateSql(chunks);
                    }
                }
            }
        }

        // Step 03 -- read rep-names for reps that we care about
        lineCnt = 0;
        System.out.println();
        try(FileInputStream fis = new FileInputStream(new File(baseDir, nameFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {
            scan.nextLine();  // Skip the header line

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("NAME.read: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 3) {
                    String repId  = chunks[0];
                    String locale = chunks[2];
                    String name   = chunks[3];
                    int    repIdx = Integer.parseInt(repId);
                    if (repIsLive.getOrDefault(repIdx, false)) {
                        if ("en".equals(locale)) {
                            repToName.put(repIdx, name);
                        } else if (! repToName.containsKey(repIdx)) {
                            repToName.put(repIdx, name);
                        }
                    }
                }
            }
        }
        
        System.out.println("\n\nDelete-Count: " + deleteCount);

        // Step 04 -- assemble and save the data
//        System.out.println("No-Cit-Rep.count: " + repIsPub.size());
//        List<String> details = new ArrayList<>();
//        repIsLive.entrySet().stream()
//            .filter(entry -> entry.getValue())
//            .forEach(entry -> {
//                int repId = entry.getKey();
//                details.add(repId + "|" + entry.getValue() + "|" + repToName.get(repId) + "|" + typeDetail.getOrDefault(repToType.get(repId), "Unknown") + "|" + parentIds.contains(repId));
//            });
//
//        Files.write(Paths.get(baseDir, outFile), details, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.exit(0);
    }

    static java.util.Random random = new java.util.Random();
    static boolean keepCitn(String[] chunks) {
        boolean keepIt = true;

        if (chunks.length > 7) {
            String repId  = chunks[0];
            String srcId  = chunks[3];
            String typId  = chunks[4];
            String srcRef = chunks[7];
            String delFlg = chunks[8];

            if (! delFlg.toLowerCase().startsWith("t")  &&  srcId.equals("1480")) {
                if (typId.equals("460")  &&  srcRef.contains("ISO")) {
                    keepIt = false;
                } else if (typId.equals("460")  &&  srcRef.contains("GEO Names")) {
                    keepIt = false;
                } else if (! typId.equals("459")  &&  ! typId.equals("464")) {
                    if (srcRef.contains("NGA")  ||  srcRef.contains("NGA_US")) {
                        keepIt = false;
                    }
                }

                if (random.nextInt() % 11111 == 0) {
                    System.out.println(repId + "|" + srcId + "|" + typId + "|" + typeDetail.get(typId) + "|" + srcRef + "|" + delFlg + "|" + keepIt);
                }
            }
        }

        return keepIt;
    }

    static int deleteCount = 0;
    static void generateSql(String[] chunks) {
        deleteCount++;

//        if (chunks.length > 7) {
//            String repId  = chunks[0];
//            String citnId = chunks[1];
//            String srcId  = chunks[3];
//            String typId  = chunks[4];
//            String srcRef = chunks[7];
//            String delFlg = chunks[8];
//        }
    }
}
