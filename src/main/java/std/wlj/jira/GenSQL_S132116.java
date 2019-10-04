/**
 * © 2018 by Intellectual Reserve, Inc. All rights reserved.
 */
package std.wlj.jira;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

import org.familysearch.standards.place.util.PlaceHelper;

import std.wlj.dbdump.DumpTypes;

/**
 * Look for citations which match certain characteristics, all associated with SOURCE=1480, and
 * generate SQL "delete" statements for those which are extraneous:
 * <ul>
 *   <li>Ignore already deleted citations ...</li>
 *   <li>Check for Source=1480 ("Online - Gazetteer")</li>
 *   <ul>
 *     <li>"Name" type (460) and SourceRef contains "ISO"</li>
 *     <li>"Name" type (460) and SourceRef contains "GEO Names"</li>
 *     <li>Not "Place" type (459), Not "Loc" type (464) and SourceRef contains "NGA"</li>
 *   </ul>
 * </ul>
 * @author wjohnson000
 *
 */
public class GenSQL_S132116 {

    static final int    stmtLimit   = 50_000;
    
    static final String inputBase   = "C:/temp/db-dump";
    static final String repFile     = "place-rep-all.txt";
    static final String citnFile    = "citation-all.txt";

    static final String outputBase  = "C:/temp/delete-citation";
    static final String sqlFileName = "delete-citn-%04d.sql";

    static final String[] beginSQL = {
        "DO $$",
        "DECLARE",
        "  tranx_id INTEGER := 0;",
        "BEGIN",
    };

    static final String[] endSQL = {
        "END $$;"    
    };

    static final Map<String, String> typeDetail = DumpTypes.loadAllTypes();

    static final Map<Integer, Boolean>      repIsLive  = new TreeMap<>();
    static final Map<Integer, Boolean>      repCitnOK  = new HashMap<>();
    static final Map<Integer, String>       repToType  = new HashMap<>();
    static final Map<Integer, List<String>> sqlByRep = new TreeMap<>();

    public static void main(String... args) throws IOException {
        // Step 01 -- Read in place-reps and store the "is-published" flag
        int lineCount = 0;
        System.out.println();
        try(FileInputStream fis = new FileInputStream(new File(inputBase, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {
            scan.nextLine();  // Skip the header line

            while (scan.hasNextLine()) {
                if (++lineCount % 500_000 == 0) System.out.println("REP.read: " + lineCount);

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

        // Step 02 -- read citations and generate DELETE statements for the undesirables
        lineCount = 0;
        int fileCount = 1;
        int deleteCount = 0;
        int prevRepIdx = 0;

        System.out.println();
        try(FileInputStream fis = new FileInputStream(new File(inputBase, citnFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {
            scan.nextLine();  // Skip the header line

            while (scan.hasNextLine()) {
                if (++lineCount % 1_000_000 == 0) System.out.println("CITN.read: " + lineCount);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                String repId  = chunks[0];
                int    repIdx = Integer.parseInt(repId);

                if (repIsLive.containsKey(repIdx)) {
                    if (keepCitn(chunks)) {
                        repCitnOK.put(repIdx, true);
                    } else {
                        deleteCount++;
                        generateSql(chunks);
                    }
                }

                if (deleteCount >= stmtLimit  &&  repIdx != prevRepIdx) {
                    List<String> citnsToDelete = filterCitnsToDelete();
                    generateSqlFile(fileCount, citnsToDelete);

                    deleteCount = 0;
                    sqlByRep.clear();
                    fileCount++;
                }
                prevRepIdx = repIdx;
            }
        }

        System.exit(0);
    }

    static boolean keepCitn(String[] chunks) {
        boolean keepIt = true;

        if (chunks.length > 7) {
            String srcId  = chunks[3];
            String typId  = chunks[4];
            String srcRef = chunks[7];
            String delFlg = chunks[8];

            if (! delFlg.toLowerCase().startsWith("t")  &&  srcId.equals("1480")) {
                if (typId.equals("460")  &&  srcRef.contains("ISO")) {
                    keepIt = false;
                } else if (typId.equals("460")  &&  srcRef.contains("GEO Names")) {
                    keepIt = false;
                } else if (! typId.equals("459")  &&  ! typId.equals("464")  &&  srcRef.contains("NGA")) {
                    keepIt = false;
                }
            }
        }

        return keepIt;
    }

    static void generateSql(String[] chunks) {
        if (chunks.length > 7) {
            String repId  = chunks[0];
            String citnId = chunks[1];
//            String tranId = chunks[2];
            int    repIdx = Integer.parseInt(repId);

            StringBuilder buff = new StringBuilder();
            buff.append("  DELETE FROM citation WHERE citation_id = ").append(citnId);
//            buff.append(" AND tran_id = ").append(tranId);
            buff.append(";  -- RepID=").append(repId);

            List<String> deletes = sqlByRep.get(repIdx);
            if (deletes == null) {
                deletes = new ArrayList<>();
                sqlByRep.put(repIdx, deletes);
            }
            deletes.add(buff.toString());
        }
    }

    static List<String> filterCitnsToDelete() {
        List<String> citnsToDelete = new ArrayList<>(stmtLimit);
        Arrays.stream(beginSQL).forEach(sql -> citnsToDelete.add(sql));

        // If a rep has another citation, include ALL targeted citations.  If a rep doesn't have
        // any other citation (than those targetted for deleting), then keep one and delete the
        // remainder.
        for (Map.Entry<Integer, List<String>> entry : sqlByRep.entrySet()) {
            if (repCitnOK.getOrDefault(entry.getKey(), false)) {
                citnsToDelete.addAll(entry.getValue());
            } else {
                System.out.println("Rep: " + entry.getKey() + " will keep one of " + entry.getValue().size() + " citations.");
                entry.getValue().stream()
                    .skip(1)
                    .forEach(sql -> citnsToDelete.add(sql));
            }
        }

        Arrays.stream(endSQL).forEach(sql -> citnsToDelete.add(sql));
        return citnsToDelete;
    }

    static void generateSqlFile(int fileCount, List<String> sqlStuff) throws IOException {
        String fileName = String.format(sqlFileName, fileCount);
        System.out.println("Saving file: " + fileName);
        Files.write(Paths.get(outputBase, fileName), sqlStuff, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
