package std.wlj.dbdump;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.familysearch.standards.place.util.PlaceHelper;

public class AnalyzeExternalXref {

    static final String baseDir  = "C:/temp";
    static final String repFile  = "db-place-rep-all.txt";
    static final String xrefFile = "db-ext-xref-all.txt";

    public static void main(String... args) throws IOException {
        Set<Integer> deletedReps = new HashSet<>();

        // Step 00 -- Read in place-reps and store IDs of those that are deleted
        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("REP.read: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 11) {
                    String repId    = chunks[0];
                    String deleteId = chunks[9];
                    if (deleteId.trim().length() > 0  &&  ! "null".equals(deleteId)) {
                        deletedReps.add(Integer.parseInt(repId));
                    }
                }
            }
        }
        System.out.println("DeletedRep.count=" + deletedReps.size());

        // Step 01 -- read in external-xref and do stuff
        int totalRows = 0;  // total number of rows in DB
        int aliveRows = 0;  // number of unique ext-xref IDs where rep is not deleted
        try(FileInputStream fis = new FileInputStream(new File(baseDir, xrefFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++totalRows % 1_000_000 == 0) System.out.println("XREF.read: " + totalRows);

                String xrefData = scan.nextLine();
                String[] chunks = PlaceHelper.split(xrefData, '|');
                if (chunks.length > 4) {
                    String repId = chunks[1];
                    if (! deletedReps.contains(Integer.parseInt(repId))) {
                        aliveRows++;
                    }
                }
            }
        }

        System.out.println();
        System.out.println("XREF.total=" + totalRows);
        System.out.println("XREF.alive=" + aliveRows);

        System.exit(0);
    }
}
