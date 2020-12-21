package std.wlj.dbdump;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.familysearch.standards.place.util.PlaceHelper;

public class AnalyzeCitations {

    static final String baseDir  = "C:/temp";
    static final String repFile  = "db-place-rep-all.txt";
    static final String citnFile = "db-citation-all.txt";

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

        // Step 01 -- read in citations and do stuff
        int totalRows  = 0;  // total number of rows in DB
        int uniqueRows = 0;  // number of unique citation IDs
        int notDLRows  = 0;  // number of unique citation IDs that are not deleted
        int aliveRows  = 0;  // number of unique citation IDs that are not deleted and rep is not deleted
        int delPrRows  = 0;  // number of rows that are tied to deleted place-reps
        try(FileInputStream fis = new FileInputStream(new File(baseDir, citnFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            String prevCitnId  = "";
            String prevDelFlag = "";
            while (scan.hasNextLine()) {
                if (++totalRows % 1_000_000 == 0) System.out.println("CITN.read: " + totalRows);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 8) {
                    String repId    = chunks[0];
                    String citnId   = chunks[1];
                    String delFlag  = chunks[8];

                    if (deletedReps.contains(Integer.parseInt(repId))) {
                        delPrRows++;
                    }

                    if (! prevCitnId.equals(citnId)) {
                        uniqueRows++;
                        if ("false".equals(prevDelFlag)) {
                            notDLRows++;
                            if (! deletedReps.contains(Integer.parseInt(repId))) {
                                aliveRows++;
                            }
                        }
                    }
                    prevCitnId = citnId;
                    prevDelFlag = delFlag;
                }
            }
        }

        System.out.println();
        System.out.println("CITN.total=" + totalRows);
        System.out.println("CITN.uniqu=" + uniqueRows);
        System.out.println("CITN.notDL=" + notDLRows);
        System.out.println("CITN.alive=" + aliveRows);
        System.out.println("CITN.delPR=" + delPrRows);

        System.exit(0);
    }
}
