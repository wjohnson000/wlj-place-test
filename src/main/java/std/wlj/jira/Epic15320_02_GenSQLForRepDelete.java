package std.wlj.jira;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.familysearch.standards.place.util.PlaceHelper;

/**
 * Generate SQL for unpublishing place-reps w/out latitude or longitude.

 * @author wjohnson000
 *
 */
public class Epic15320_02_GenSQLForRepDelete {

    static final String baseDir      = "C:/temp";
    static final String repFile      = "db-place-rep-all.txt";
    static final String repUnpubFile = "e15320-rep-delete.sql";

    static final String[] beginSQL = {
        "DO $$",
        "DECLARE",
        "  tranx_id INTEGER := NEXTVAL('transaction_tran_id_seq');",
        "BEGIN",
        "  INSERT INTO transaction(tran_id, create_ts, create_id) VALUES(tranx_id, now(), 'system-delete-reps');"
    };

    static final String[] endSQL = {
        "END $$;"    
    };

    public static void main(String... args) throws IOException {
        List<String> unpubSQL = new ArrayList<>(100_000);
        Arrays.stream(beginSQL).forEach(sql -> unpubSQL.add(sql));

        System.out.println();
        int lineCnt = 0;
        int sampleCnt = 0;
        try(FileInputStream fis = new FileInputStream(new File(baseDir, repFile));
                Scanner scan = new Scanner(fis, "UTF-8")) {

            while (scan.hasNextLine()) {
                if (++lineCnt % 500_000 == 0) System.out.println("Lines read: " + lineCnt);

                String repData = scan.nextLine();
                String[] chunks = PlaceHelper.split(repData, '|');
                if (chunks.length > 11) {
                    String repId    = chunks[0];
                    String parentId = chunks[2];
                    String deleteId = chunks[9];
                    String pubFlag  = chunks[11];

                    if (deleteId == null  ||  deleteId.trim().isEmpty()  ||  deleteId.trim().equals("0")  ||  deleteId.trim().equals("null")) {
                        if (pubFlag == null  ||  pubFlag.trim().isEmpty()  ||  pubFlag.trim().toLowerCase().startsWith("f")) {
                            unpubSQL.add(updateRepSQL(chunks));
                            if (++sampleCnt < 12) {
                                System.out.println(repId + "|" + parentId);
                            }
                        }
                    }
                }
            }
        }
        Arrays.stream(endSQL).forEach(sql -> unpubSQL.add(sql));

        System.out.println();
        System.out.println("Un-pub size: " + unpubSQL.size());

        Files.write(Paths.get(baseDir, repUnpubFile), unpubSQL, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /**
     * Generate the SQL that will update the place-rep to version it.
     * 
     * @param data Array with all of the existing data fields ... change nothing
     *        but the "delete_id" and the "tran_id"
     */
    static String updateRepSQL(String[] data) {
        if (data.length < 16) {
            System.out.println("Too few fields!! " + Arrays.toString(data));
            return "";
        } else if (! "null".equals(data[9])) {
            System.out.println("Already deleted!! " + Arrays.toString(data));
            return "";
        }

        StringBuilder buff = new StringBuilder();

        buff.append("  INSERT INTO place_rep(rep_id, tran_id, parent_id, owner_id, centroid_long, centroid_lattd, ");
        buff.append("place_type_id, parent_from_year, parent_to_year, delete_id, pref_locale, pub_flag, validated_flag, ");
        buff.append("uuid, group_id, pref_boundary_id) ");
        buff.append("VALUES (").append(numericData(data[0]));
        buff.append(", ").append("tranx_id");
        buff.append(", ").append(numericData(data[2]));
        buff.append(", ").append(numericData(data[3]));
        buff.append(", ").append(numericData(data[4]));
        buff.append(", ").append(numericData(data[5]));
        buff.append(", ").append(numericData(data[6]));
        buff.append(", ").append(numericData(data[7]));
        buff.append(", ").append(numericData(data[8]));
        buff.append(", ").append(numericData(data[2]));   // Use the parent as the new "delete_id"
        buff.append(", ").append(characterData(data[10]));
        buff.append(", ").append(booleanData(data[11]));
        buff.append(", ").append(booleanData(data[12]));
        buff.append(", ").append(characterData(data[13]));
        buff.append(", ").append(numericData(data[14]));
        buff.append(", ").append(numericData(data[15]));
        buff.append(");");

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
}
