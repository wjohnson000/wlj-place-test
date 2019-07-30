package std.wlj.dbdump;

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

import org.familysearch.standards.place.util.PlaceHelper;

public class FindProvisionalRepWithChildren {

    static final String baseDir  = "C:/temp/db-dump";
    static final String repFile  = "place-rep-all.txt";
    static final String nameFile = "display-name-all.txt";
    static final String citnFile = "citation-all.txt";
    static final String outFile  = "provisional-rep-with-children.txt";

    public static void main(String... args) throws IOException {
        Set<Integer>          provRepIds     = new HashSet<>();
        Set<Integer>          parentIds      = new HashSet<>();
        Set<Integer>          repWithCitnIds = new HashSet<>();
        Map<Integer, String>  repToName      = new HashMap<>();
        Map<Integer, String>  repToType      = new HashMap<>();

        // Step 00 -- Get the place-type information
        Map<String, String> typeDetail = DumpTypes.loadPlaceTypes();

        // Step 01 -- Read in place-reps and save provisional rep IDs and parent rep IDs
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
                    String delId  = chunks[9];
                    String isPub  = chunks[11];
                    String isVal  = chunks[12];

                    if (delId.trim().isEmpty()) {
                        try { parentIds.add(Integer.parseInt(parId)); } catch(NumberFormatException exx) { }
                        if (isPub.startsWith("f")  &&  isVal.startsWith("f")) {
                            provRepIds.add(Integer.parseInt(repId));
                            repToType.put(Integer.parseInt(repId), typeId);
                        }
                    }
                }
            }
        }

        // Step 02 -- read rep-names for reps that we care about
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
                    try {
                        int repIdx = Integer.parseInt(repId);
                        if (provRepIds.contains(repIdx)) {
                            if ("en".equals(locale)) {
                                repToName.put(repIdx, name);
                            } else if (! repToName.containsKey(repIdx)) {
                                repToName.put(repIdx, name);
                            }
                        }
                    } catch(NumberFormatException ex) { }
                }
            }
        }

        // Step 03 -- read citations to see which of the provisional reps have citations
        lineCnt = 0;
        System.out.println();
        try(FileInputStream fis = new FileInputStream(new File(baseDir, citnFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("CITN.read: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 3) {
                    String repId  = chunks[0];
                    String delFlg = chunks[8];
                    try {
                        int repIdx = Integer.parseInt(repId);
                        if (provRepIds.contains(repIdx)  &&  delFlg.startsWith("f")) {
                            repWithCitnIds.add(repIdx);
                        }
                    } catch(NumberFormatException ex) { }
                }
            }
        }
        
        // Step 04 -- assemble and save the data
        System.out.println("Provisional-Rep.count: " + provRepIds.size());
        List<String> details = new ArrayList<>();
        provRepIds.forEach(repId -> {
            if (parentIds.contains(repId)) {
                details.add(repId + "|" + repToName.get(repId) + "|" + typeDetail.getOrDefault(repToType.get(repId), "Unknown") + "|" + repWithCitnIds.contains(repId));
            }
        });
        Files.write(Paths.get(baseDir, outFile), details, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        System.exit(0);
    }
}
