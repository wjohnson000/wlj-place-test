package std.wlj.dbdump;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.familysearch.standards.place.util.PlaceHelper;

public class Delete_2017_12_Citations {

    static final String baseDir     = "C:/temp";
    static final String repFile     = "db-place-rep-all.txt";
    static final String citnFile    = "db-citation-all.txt";
    static final String sqlFileName = "del-citn-%04d.sql";

    static final String[] beginSQL = {
        "DO $$",
        "DECLARE",
        "  tranx_id INTEGER := NEXTVAL('transaction_tran_id_seq');",
        "BEGIN",
    };

    static final String[] endSQL = {
            "END $$;"    
    };

    public static void main(String...args) throws IOException {
        Set<Integer> delRepIds = getDelRepIds();
        genSqlFile(delRepIds);
        System.exit(0);
    }

    static Set<Integer> getDelRepIds() throws IOException {
        Set<Integer> ids = new HashSet<>(5_000_000);

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
                        ids.add(Integer.parseInt(repId));
                    }
                }
            }
        }
        System.out.println("DeletedRep.count=" + ids.size());

        return ids;
    }

    static void genSqlFile(Set<Integer> delRepIds) throws IOException {
        int fileCount = 1;
        Set<String> citnIds = new TreeSet<>();
        List<String> sqlStuff = new ArrayList<>(50_000);
        Arrays.stream(beginSQL).forEach(sql -> sqlStuff.add(sql));

        int lineCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, citnFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("CITN.read: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 5) {
                    String repId  = chunks[0];
                    String citnId = chunks[1];
                    try {
                        if (delRepIds.contains(Integer.parseInt(repId))) {
                            citnIds.add(citnId);
                            if (citnIds.size() == 50) {
                                sqlStuff.add(delCitnSQL(citnIds));
                                citnIds.clear();
                            }
                            if (sqlStuff.size() == 50_000) {
                                Arrays.stream(endSQL).forEach(sql -> sqlStuff.add(sql));
                                generateSqlFile(fileCount, sqlStuff);
                                fileCount++;
                                sqlStuff.clear();
                                Arrays.stream(beginSQL).forEach(sql -> sqlStuff.add(sql));
                            }
                        }
                    } catch(NumberFormatException ex) {
                        System.out.println("EX>> " + repData);
                    }
                }
            }
        }

        Arrays.stream(endSQL).forEach(sql -> sqlStuff.add(sql));
        generateSqlFile(fileCount, sqlStuff);
    }

    static String delCitnSQL(Set<String> citnIds) {
        StringBuilder buff = new StringBuilder();

        buff.append("  DELETE FROM citation WHERE citation_id IN ");
        buff.append(citnIds.stream().collect(Collectors.joining(",", "(", ");")));

        return buff.toString();
    }

    static String characterData(String datum) {
        if ("null".equals(datum)) {
            return datum;
        } else {
            return "'" + datum + "'";
        }
    }

    static String numericData(String datum) {
        return datum;
    }

    static String booleanData(String datum) {
        return (datum.toLowerCase().startsWith("t")) ? "TRUE" : "FALSE";
    }

    static void generateSqlFile(int fileCount, List<String> sqlStuff) throws IOException {
        String fileName = String.format(sqlFileName, fileCount);
        System.out.println("Saving file: " + fileName);
        Files.write(Paths.get(baseDir, fileName), sqlStuff, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }
}
