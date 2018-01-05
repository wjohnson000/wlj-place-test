package std.wlj.dbdump;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
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

public class RepWithNoCitation {

    static final String baseDir  = "C:/temp";
    static final String repFile  = "db-place-rep-all.txt";
    static final String nameFile = "db-display-name-all.txt";
    static final String citnFile = "db-citation-all.txt";
    static final String outFile  = "rep-no-citation.txt";

    public static void main(String... args) throws IOException {
        Map<Integer, Boolean> repIsPub   = new TreeMap<>();
        Map<Integer, String>  repToName  = new HashMap<>();
        Map<Integer, String>  repToType  = new HashMap<>();
        Set<Integer>          parentIds  = new HashSet<>();

        // Step 00 -- Get the place-type information
        Map<String, String> typeDetail = DumpTypes.loadPlaceTypes();

        // Step 01 -- Read in place-reps and store the "is-published" flag
        int lineCnt = 0;
        System.out.println();
        try(FileInputStream fis = new FileInputStream(new File(baseDir, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("REP.read: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 11) {
                    String repId  = chunks[0];
                    String parId  = chunks[2];
                    String typeId = chunks[6];
                    String isPub  = chunks[11];
                    String delId  = chunks[9];
                    try {
                        Integer.parseInt(delId);
                    } catch(NumberFormatException ex) {
                        repIsPub.put(Integer.parseInt(repId), isPub.startsWith("t"));
                        repToType.put(Integer.parseInt(repId), typeId);
                        try { parentIds.add(Integer.parseInt(parId)); } catch(NumberFormatException exx) { }
                    }
                }
            }
        }

        // Step 02 -- read citations and remove reps that have at least one citation
        lineCnt = 0;
        System.out.println();
        try(FileInputStream fis = new FileInputStream(new File(baseDir, citnFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 1_000_000 == 0) System.out.println("CITN.read: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 6) {
                    int repId = Integer.parseInt(chunks[0]);
                    repIsPub.remove(repId);
                    repToType.remove(repId);
                }
            }
        }

        // Step 03 -- read rep-names for reps that we care about
        lineCnt = 0;
        System.out.println();
        try(FileInputStream fis = new FileInputStream(new File(baseDir, nameFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("NAME.read: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 3) {
                    String repId  = chunks[0];
                    String locale = chunks[2];
                    String name   = chunks[3];
                    int    repIdx = Integer.parseInt(repId);
                    if (repIsPub.containsKey(repIdx)) {
                        if ("en".equals(locale)) {
                            repToName.put(repIdx, name);
                        } else if (! repToName.containsKey(repIdx)) {
                            repToName.put(repIdx, name);
                        }
                    }
                }
            }
        }
        
        // Step 04 -- assemble and save the data
        System.out.println("No-Cit-Rep.count: " + repIsPub.size());
        List<String> details = new ArrayList<>();
        repIsPub.entrySet().forEach(entry -> {
            int repId = entry.getKey();
            details.add(repId + "|" + entry.getValue() + "|" + repToName.get(repId) + "|" + typeDetail.getOrDefault(repToType.get(repId), "Unknown") + "|" + parentIds.contains(repId));
        });
        Files.write(Paths.get(baseDir, outFile), details, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.exit(0);
    }
}
